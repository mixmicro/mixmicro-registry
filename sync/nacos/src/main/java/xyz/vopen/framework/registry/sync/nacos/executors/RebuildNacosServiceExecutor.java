package xyz.vopen.framework.registry.sync.nacos.executors;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import xyz.vopen.framework.registry.sync.nacos.NacosSyncProperties;
import xyz.vopen.framework.registry.sync.nacos.ServiceThread;
import xyz.vopen.framework.registry.sync.nacos.event.SyncedServiceRebuildEvent;
import xyz.vopen.framework.registry.sync.nacos.model.Service;
import xyz.vopen.mixmicro.kits.event.Event;
import xyz.vopen.mixmicro.kits.event.EventBus;
import xyz.vopen.mixmicro.kits.event.Subscriber;
import xyz.vopen.mixmicro.kits.thread.ThreadKit;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.*;

/**
 * {@link RebuildNacosServiceExecutor}
 *
 * <p>Class RebuildNacosServiceExecutor Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/18
 */
public class RebuildNacosServiceExecutor {

  private static final Logger log = LoggerFactory.getLogger(RebuildNacosServiceExecutor.class);

  final NamingService originNamingService;

  final NamingService destNamingService;

  final NacosSyncProperties.Rebuild rebuild;

  private final AtomicBoolean initialized = new AtomicBoolean(false);

  SyncedServiceRebuildEventSubscriber subscriber;

  private final Map<String, ServiceThread> services = Maps.newConcurrentMap();

  /**
   * Rebuild Service Executor .
   *
   * @param originNamingService origin nacos naming service instance of {@link NamingService}
   * @param destNamingService dest nacos naming service instance of {@link NamingService}
   * @param rebuild rebuild config properties
   */
  public RebuildNacosServiceExecutor(@NonNull NamingService originNamingService, @NonNull NamingService destNamingService,
                                     @NonNull NacosSyncProperties.Rebuild rebuild) {
    this.originNamingService = originNamingService;
    this.destNamingService = destNamingService;
    this.rebuild = rebuild;
  }


  // ~~ initialize method .

  public void initialize() {

    if(initialized.compareAndSet(false, true)) {

      if(subscriber == null) {
        subscriber = new SyncedServiceRebuildEventSubscriber(this);
      }

      log.info("[REBUILD] register synced event , {}", subscriber);
      EventBus.register(SyncedServiceRebuildEvent.class, subscriber);

      // any else initialize operation(s) ?

    }

  }

  // ~~

  void rebuild(@NonNull ServiceThread thread) {

    // set daemon true .
    thread.setDaemon(true);

    // save
    ServiceThread origin = services.putIfAbsent(thread.getServiceName(), thread);

    if(origin != null) {
      try{
        origin.shutdown();
      } catch (Exception ignore) {}
    }

    // startup
    thread.start();
  }

  // ~~ destroy method .

  public void destroy() {

    if(initialized.compareAndSet(true, false)) {
      if (subscriber != null) {
        log.info("[REBUILD] deregister synced event , {}", subscriber);
        EventBus.unRegister(SyncedServiceRebuildEvent.class, subscriber);
      }

      // other destroy
      if(!services.isEmpty()) {
        services.forEach((name, serviceThread) -> {
          try{
            serviceThread.shutdown();
          } catch (Exception e) {
            log.warn("[REBUILD] shutdown service thread {} failed .", name);
          }
        });
      }
    }
  }

  // ~~ rebuild subscriber class

  private static class SyncedServiceRebuildEventSubscriber extends Subscriber {

    final RebuildNacosServiceExecutor executor;

    private SyncedServiceRebuildEventSubscriber(@NonNull RebuildNacosServiceExecutor executor) {
      this.executor = executor;
    }

    @Override
    public void onEvent(Event event) {

      if(event instanceof SyncedServiceRebuildEvent) {

        SyncedServiceRebuildEvent rebuildEvent = (SyncedServiceRebuildEvent) event;

        Service service = rebuildEvent.getService();

        ServiceThread thread = new ServiceThread() {

              @Override
              public String getServiceName() {
                return service.toString();
              }

              private boolean isSyncOwner(Map<String, String> metadata) {
                return metadata.containsKey(METADATA_SYNC_OWNER_KEY) && Objects.equals(METADATA_SYNC_OWNER_VALUE, metadata.get(METADATA_SYNC_OWNER_KEY));
              }

              @Override
              public void run() {

                while (!isStopped()) {

                  try {

                    NamingService originNamingService = executor.originNamingService;
                    NamingService destNamingService = executor.destNamingService;

                    // loop check origin services .
                    List<Instance> ois = originNamingService.getAllInstances(service.getName(), service.getGroupName());

                    List<Instance> dis = destNamingService.getAllInstances(service.getName(), service.getGroupName());

                    // warning :: is ois is empty or dis instance size large than dis size .
                    if (ois.isEmpty() || ois.size() < dis.size()) {

                      dis.forEach(instance -> {
                        // check this
                        if(isSyncOwner(instance.getMetadata())) {
                          // reset sync owner key .
                          instance.getMetadata().put(METADATA_SYNC_OWNER_KEY, METADATA_SYNC_REBUILD_VALUE);
                          try {
                            originNamingService.registerInstance(service.getName(), service.getGroupName(), instance);
                          } catch (NacosException e) {
                            log.warn("[REBUILD] rebuild thread register service instance failed , service: {}, instance : {}", service.toString(), instance.getServiceName());
                          }
                        }
                      });
                    }

                  } catch (Exception e) {
                    log.warn("[REBUILD] rebuild thread execute failed , service: {}", service.toString());
                  } finally {
                    ThreadKit.sleep(
                        executor.rebuild.getCheckInterval(), executor.rebuild.getTimeUnit());
                  }
                }
              }
            };

        //
        executor.rebuild(thread);
      }

    }
  }

}
