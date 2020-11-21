package xyz.vopen.framework.registry.sync.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.registry.sync.nacos.config.DynamicConfigService;
import xyz.vopen.framework.registry.sync.nacos.executors.NacosExecutorManager;
import xyz.vopen.framework.registry.sync.nacos.executors.NacosRegisterServiceExecutor;
import xyz.vopen.framework.registry.sync.nacos.executors.RebuildNacosServiceExecutor;
import xyz.vopen.framework.registry.sync.nacos.model.Namespace;
import xyz.vopen.framework.registry.sync.nacos.model.Service;
import xyz.vopen.framework.registry.sync.nacos.model.response.Response;
import xyz.vopen.framework.registry.sync.nacos.model.response.ServiceResponse;
import xyz.vopen.mixmicro.kits.thread.ThreadKit;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.DEFAULT_NAMESPACE_THREAD_NAME;

/**
 * {@link NacosSyncService}
 *
 * <p>Class NacosSyncService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
public class NacosSyncService {

  private final Logger log = LoggerFactory.getLogger(NacosSyncService.class);

  private final static AtomicBoolean startup = new AtomicBoolean(false);

  private final NacosSyncProperties properties;

  private final DynamicConfigService dynamicConfigService;

  private final NacosService nacosService;

  private RebuildNacosServiceExecutor rebuildExecutor;

  private final AtomicReference<String> authorizationRef = new AtomicReference<>();

  // ~~ namespaced naming service cache
  private final Map<String, NamingService> dnssc = Maps.newConcurrentMap();
  private final Map<String, NamingService> onssc = Maps.newConcurrentMap();

  private ServiceThread fixServiceThread;

  public NacosSyncService(NacosSyncProperties properties, NacosService nacosService, DynamicConfigService dynamicConfigService) {
    this.properties = properties;
    this.nacosService = nacosService;
    this.dynamicConfigService = dynamicConfigService;
  }

  // ~~ startup method .

  public void startup() {

    if(startup.compareAndSet(false, true)) {

      // build origin nacos naming service
      NacosSyncProperties.Origin origin = properties.getOrigin();

      // check rebuild service .
      if(properties.getRebuild().isEnabled()) {
        if(rebuildExecutor == null) {
          rebuildExecutor = new RebuildNacosServiceExecutor(dynamicConfigService, onssc, dnssc, properties.getRebuild());
          rebuildExecutor.initialize();
        }
      }

      // login
      Response<String> ar = nacosService.login(origin.getUsername(), origin.getPassword());

      String authorization = ar.getData();

      authorizationRef.set(authorization);

      this.startup0(); // ~~

      if(properties.getFix().isEnabled()) {
        fixServiceThread = new FixServiceThread(properties.getFix().getDelay(), properties.getFix().getCheckInterval());
        fixServiceThread.setDaemon(true);
        fixServiceThread.start();
      }

    } else {
      log.warn("[SS] sync service is started .");
    }
  }
  private void startup0() {

    String authorization = authorizationRef.get();

    log.info("[SS] Authorization : {}", authorization);

    Response<List<Namespace>> namespacesResponse = nacosService.namespaces(authorization);

    List<Namespace> namespaces = namespacesResponse.getData();

    final NacosSyncProperties.SyncRule rule = properties.getSyncRule();

    for (Namespace namespace : namespaces) {

      String key = Joiner.on("@@").join(DEFAULT_NAMESPACE_THREAD_NAME, namespace.getNamespace(), namespace.getNamespaceShowName());

      if(!rule.matchNamespace(namespace.getNamespaceShowName())) {
        continue;
      }

      if(!dnssc.containsKey(key)) {
        // build dest nacos naming service
        NacosSyncProperties.Destination destination = properties.getDestination();
        Properties destProperties = new Properties();
        destProperties.put(PropertyKeyConst.NAMESPACE, namespace.getNamespace());
        destProperties.put(PropertyKeyConst.SERVER_ADDR, destination.getServerAddr());
        NamingService tempdns = new NacosNamingService(destProperties);
        dnssc.put(key, tempdns);
      }

      if(!onssc.containsKey(key)) {
        // build origin nacos naming service
        NacosSyncProperties.Origin origin = properties.getOrigin();
        Properties originProperties = new Properties();
        originProperties.put(PropertyKeyConst.NAMESPACE, namespace.getNamespace());
        originProperties.put(PropertyKeyConst.SERVER_ADDR, origin.getServerAddr());

        NamingService tempons = new NacosNamingService(originProperties);
        onssc.put(key, tempons);
      }

      log.info("[SS] begin to execute namespace : {}@@{} sync task .", namespace.getNamespace(), namespace.getNamespaceShowName());

      ServiceThread namespaceServiceThread = new ServiceThread() {

        @Override
        public String getServiceName() {
          return key;
        }

        @Override
        public void run() {

          ServiceResponse serviceResponse = nacosService.services(authorization, namespace.getNamespace());

          if(serviceResponse != null) {
            List<Service> services = serviceResponse.getServiceList();

            for(Service service : services) {
              if(service != null) {

                if(!rule.matchService(service.getName())) {
                  continue;
                }

                if(NacosExecutorManager.manager().containsKey(namespace, service)) {
                  continue;
                }

                // add service cache.
                NacosExecutorManager.manager().putIfAbsent(namespace, service);

                // build service executor
                NacosRegisterServiceExecutor executor = new NacosRegisterServiceExecutor(dynamicConfigService, onssc, dnssc,
                    nacosService, namespace, authorization, service, properties.isDeregister());

                log.info("[SSE] execute service sync , service name :{}", service.getName());
                // execute directly
                executor.run();
              }
            }
          }
        }
      };

      // Start
      namespaceServiceThread.run();
    }
  }


  private class FixServiceThread extends ServiceThread {

    private static final String NAME = "NS-FIX-THREAD";

    private final long delay;
    private final long checkInterval;

    private FixServiceThread(long delay, long checkInterval) {
      this.delay = delay;
      this.checkInterval = checkInterval;
    }

    @Override
    public String getServiceName() {
      return NAME;
    }

    @Override
    public void run() {

      try{

        ThreadKit.sleep(delay);

        while (!isStopped()) {
          try{
            startup0();
          } catch (Exception e) {
            log.warn("[FIX] fix thread startup0 failed", e);
          } finally{
            ThreadKit.sleep(checkInterval);
          }
        }

      } catch (Exception e) {
        log.warn("[FIX] fix thread execute failed", e);
      }
    }
  }


  // ~~ destroy method .

  @PreDestroy
  public void destroy() {

    if(startup.compareAndSet(true, false)) {

      if(properties.getRebuild().isEnabled() && rebuildExecutor != null) {
        rebuildExecutor.destroy();
      }

      if(properties.getFix().isEnabled() && fixServiceThread != null) {
        fixServiceThread.shutdown();
      }
    }


  }
}
