package xyz.vopen.framework.registry.sync.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.registry.sync.nacos.executors.NacosRegisterServiceExecutor;
import xyz.vopen.framework.registry.sync.nacos.executors.RebuildNacosServiceExecutor;
import xyz.vopen.framework.registry.sync.nacos.model.Namespace;
import xyz.vopen.framework.registry.sync.nacos.model.Service;
import xyz.vopen.framework.registry.sync.nacos.model.response.Response;
import xyz.vopen.framework.registry.sync.nacos.model.response.ServiceResponse;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

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

  private static final String DEFAULT_NAMESPACE_THREAD_NAME = "NS-THREAD";

  private final static AtomicBoolean startup = new AtomicBoolean(false);

  private final NacosSyncProperties properties;

  private final NacosService nacosService;

  private RebuildNacosServiceExecutor rebuildExecutor;

  private final Map<String, ServiceThread> nsmap = Maps.newConcurrentMap();

  public NacosSyncService(NacosSyncProperties properties, NacosService nacosService) {
    this.properties = properties;
    this.nacosService = nacosService;
  }

  // ~~ startup method .

  public void startup() {

    if(startup.compareAndSet(false, true)) {

      // build origin nacos naming service
      NacosSyncProperties.Origin origin = properties.getOrigin();
      Properties originProperties = new Properties();
      originProperties.put(PropertyKeyConst.SERVER_ADDR, origin.getServerAddr());

      NamingService originNamingService = new NacosNamingService(originProperties);

      // build dest nacos naming service
      NacosSyncProperties.Destination destination = properties.getDestination();
      Properties destProperties = new Properties();
      destProperties.put(PropertyKeyConst.SERVER_ADDR, destination.getServerAddr());

      NamingService destNamingService = new NacosNamingService(destProperties);

      // check rebuild service .
      if(properties.getRebuild().isEnabled()) {
        if(rebuildExecutor == null) {
          rebuildExecutor = new RebuildNacosServiceExecutor(originNamingService, destNamingService, properties.getRebuild());
          rebuildExecutor.initialize();
        }
      }

      // login
      Response<String> ar = nacosService.login(origin.getUsername(), origin.getPassword());

      String authorization = ar.getData();

      log.info("[SS] Authorization : {}", authorization);

      Response<List<Namespace>> namespacesResponse = nacosService.namespaces(authorization);

      List<Namespace> namespaces = namespacesResponse.getData();

      final NacosSyncProperties.SyncRule rule = properties.getSyncRule();

      for (Namespace namespace : namespaces) {

        if(!rule.matchNamespace(namespace.getNamespaceShowName())) {
          continue;
        }

        log.info("[SS] begin to execute namespace : {}@@{} sync task .", namespace.getNamespace(), namespace.getNamespaceShowName());

        ServiceThread namespaceServiceThread = new ServiceThread() {

          @Override
          public String getServiceName() {
            return Joiner.on("@@").join(DEFAULT_NAMESPACE_THREAD_NAME, namespace.getNamespace(), namespace.getNamespaceShowName());
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
                  NacosRegisterServiceExecutor executor = new NacosRegisterServiceExecutor(originNamingService, destNamingService, nacosService, namespace.getNamespace(), authorization, service);
                  log.info("[SSE] execute service sync , service name :{}", service.getName());
                  // execute directly
                  executor.run();
                }
              }
            }
          }
        };

        // Start
        namespaceServiceThread.start();

        // ADD
        nsmap.putIfAbsent(namespaceServiceThread.getServiceName(), namespaceServiceThread);
      }
    } else {
      log.warn("[SS] sync service is started .");
    }

  }

  // ~~ destroy method .

  @PreDestroy
  public void destroy() {

    if(startup.compareAndSet(true, false)) {

      if(properties.getRebuild().isEnabled() && rebuildExecutor != null) {
        rebuildExecutor.destroy();
      }

      nsmap.forEach(
          (name, serviceThread) -> {
            try {
              serviceThread.shutdown();
            } catch (Exception e) {
              log.warn("[SS] task: {} ,shutdown interrupted .", name);
            }
          });
    }


  }
}
