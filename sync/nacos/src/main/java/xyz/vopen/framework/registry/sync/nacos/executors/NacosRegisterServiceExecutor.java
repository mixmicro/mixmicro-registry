package xyz.vopen.framework.registry.sync.nacos.executors;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import xyz.vopen.framework.registry.sync.nacos.NacosService;
import xyz.vopen.framework.registry.sync.nacos.ServiceThread;
import xyz.vopen.framework.registry.sync.nacos.config.DynamicConfigService;
import xyz.vopen.framework.registry.sync.nacos.event.SyncedServiceRebuildEvent;
import xyz.vopen.framework.registry.sync.nacos.model.Instance;
import xyz.vopen.framework.registry.sync.nacos.model.Namespace;
import xyz.vopen.framework.registry.sync.nacos.model.Service;
import xyz.vopen.framework.registry.sync.nacos.model.response.InstanceResponse;
import xyz.vopen.mixmicro.kits.Assert;
import xyz.vopen.mixmicro.kits.event.EventBus;

import java.util.*;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.*;
import static xyz.vopen.framework.registry.sync.nacos.executors.NacosExecutorManager.fixRebuildInstances;
import static xyz.vopen.framework.registry.sync.nacos.model.Instance.build;

/**
 * {@link NacosRegisterServiceExecutor}
 *
 * <p>Class NacosRegisterServiceExecutor Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
public class NacosRegisterServiceExecutor extends ServiceThread {

  private static final Logger log = LoggerFactory.getLogger(NacosRegisterServiceExecutor.class);

  private final Map<String, NamingService> originNamingServices;

  private final Map<String, NamingService> destNamingServices;

  private final NacosService nacosService;

  private final Namespace namespace;

  private final String authorization;

  private final Service service;

  private final boolean deregister;

  final DynamicConfigService dynamicConfigService;

  public NacosRegisterServiceExecutor(
      DynamicConfigService dynamicConfigService,
      // Nacos Naming Service Instance
      @NonNull Map<String, NamingService> originNamingServices,
      @NonNull Map<String, NamingService> destNamingServices,
      // Nacos Console Service
      NacosService nacosService,
      Namespace namespace,
      String authorization,
      Service service) {
    this(dynamicConfigService, originNamingServices, destNamingServices, nacosService, namespace, authorization, service, false);
  }

  public NacosRegisterServiceExecutor(
      DynamicConfigService dynamicConfigService,
      // Nacos Naming Service Instance
      @NonNull Map<String, NamingService> originNamingServices,
      @NonNull Map<String, NamingService> destNamingServices,
      // Nacos Console Service
      NacosService nacosService,
      Namespace namespace,
      String authorization,
      Service service,
      boolean deregister) {
    super();
    this.dynamicConfigService = dynamicConfigService;
    this.authorization = authorization;
    this.namespace = namespace;
    this.originNamingServices = originNamingServices;
    this.destNamingServices = destNamingServices;
    this.nacosService = nacosService;
    this.service = service;
    this.deregister = deregister;
  }

  @Override
  public void run() {

    log.info("[EXE] begin process service : {}, {} , {} , {}", namespace.getNamespace(), service.getClusterCount(), service.getGroupName(), service.getName());

    InstanceResponse response = nacosService.instances(authorization, service.getName(), service.getClusterName(), service.getGroupName(), namespace.getNamespace());

    log.info("[EXE] found service instances , count: {} ", response.getCount());

    List<Instance> instances = response.getList();

    // register instance into target nacos cluster
    for (Instance instance : instances) {

      String key = instance.key();
      if(fixRebuildInstances.add(key)) {
        // check origin service instance s owner is rebuild .?
        Map<String, String> metadata = instance.getMetadata();
        if(isRebuildOwner(metadata)) {
          log.info("[EXE-FIX] try to fix origin instance metadata , instance: {}", instance.getServiceName());
          boolean result = this.fixRegisterInstance(service.getName(), service.getGroupName(), instance);
          log.info("[EXE-FIX] service instance :{} ,register result : {}", instance.getServiceName(), result);
        }
      }

      boolean registered = registerInstance(service.getName(), service.getGroupName(), instance);
      log.info("[EXE] service instance :{} ,register result : {}", instance.getServiceName(), registered);
    }

    // subscribe service

    try {
      NamingService originNamingService = originNamingServices.get(namespace.key());
      log.info("[EXE] found origin namespace: {} NamingService instance : {}", namespace.getNamespaceShowName(), originNamingService);
      Assert.notNull(originNamingService, "origin namespace's naming service instance must not be null .");

      NamingService destNamingService = destNamingServices.get(namespace.key());
      log.info("[EXE] found dest namespace: {} NamingService instance : {}", namespace.getNamespaceShowName(), destNamingService);
      Assert.notNull(destNamingService, "dest namespace's naming service instance must not be null .");

      originNamingService.subscribe(
          service.getName(), // service name
          service.getGroupName(), // service group name
          event -> { // listener for service
            if (event instanceof NamingEvent) {

              try{
                // if app flow is migrated , stop listener origin service .
                if(!dynamicConfigService.getConfig().isMigrated()) {

                  NamingEvent namingEvent = (NamingEvent) event;

                  Set<String> instanceKeySet = new HashSet<>();

                  // find all service s instance from origin nacos cluster .
                  // warning ::
                  //    if source cluster nginx is reloaded , need to be shutdown origin -> dest syncer server .
                  //
                  List<com.alibaba.nacos.api.naming.pojo.Instance> sourceInstances = originNamingService.getAllInstances(service.getName(), service.getGroupName());

                  log.info("[SUBSCRIBE] received service event , {} | {} | {} | Current Size: {}",
                      namingEvent.getServiceName(), service.getGroupName(), namingEvent.getInstances() == null ? 0 : namingEvent.getInstances().size(), sourceInstances.size());

                  // register instance .
                  for (com.alibaba.nacos.api.naming.pojo.Instance temp : sourceInstances) {

                    // check instance is from rebuild execute.?
                    // warning :: If you switch back to the original cluster after switching Nginx traffic,
                    //            it is recommended that you restart the synchronization cluster. (Reason: When the original cluster has new service nodes up and down,
                    //            it may cause data inconsistencies between the master and slave clusters.)
                    // todo fixed :: need fixed origin nacos cluster service instance's metadata .(removed key: mixmicro.registry.sync.owner) .
                    if(!isRebuildOwner(temp.getMetadata())) {
                      registerInstance(service.getName(), service.getGroupName(), build(temp));
                      instanceKeySet.add(composeInstanceKey(temp));

                      // check application is migrated x?x -> post rebuild event .
                      EventBus.post(SyncedServiceRebuildEvent.builder().namespace(namespace).service(service).build());

                    }
                  }

                  // unregister instance .

                  if(deregister) { // check is deregister enabled ?
                    List<com.alibaba.nacos.api.naming.pojo.Instance> destInstances = destNamingService.getAllInstances(service.getName(), service.getGroupName());

                    for (com.alibaba.nacos.api.naming.pojo.Instance temp : destInstances) {
                      if(isSyncOwner(temp.getMetadata()) && !instanceKeySet.contains(composeInstanceKey(temp))) {
                        destNamingService.deregisterInstance(service.getName(), temp.getIp(), temp.getPort());
                      }
                    }
                  }

                  instanceKeySet.clear();

                } else {
                  log.warn("[EXE] Nginx service traffic has been switched to suspend listening callbacks on the source nacos service list.");
                }

              } catch (NacosException e) {
                log.warn("[EXE] event process failed , code: {} ,err: {}", e.getErrCode(), e.getErrMsg());
              }
            }
          });
    } catch (NacosException e) {
      log.warn("[EXE] subscribe service failed , code: {} ,err: {}", e.getErrCode(), e.getErrMsg());
    }
  }

  private boolean registerInstance(String serviceName, String groupName, Instance instance) {
    return this.registerInstance(serviceName, groupName, build(instance));
  }

  private boolean registerInstance(String serviceName, String groupName, com.alibaba.nacos.api.naming.pojo.Instance instance) {
    try {
      NamingService destNamingService = destNamingServices.get(namespace.key());
      destNamingService.registerInstance(serviceName, groupName, build(instance));
      log.info("[EXE] register service instance , {} | {} | {}", serviceName, groupName, instance.getInstanceId());

    } catch (NacosException e) {
      log.warn("[EXE] register service failed , code: {} ,err: {}", e.getErrCode(), e.getErrMsg());
      return false;
    }
    return true;
  }

  private boolean fixRegisterInstance(String serviceName, String groupName, Instance instance) {
    return this.fixRegisterInstance(serviceName, groupName, build(instance, false));
  }

  private boolean fixRegisterInstance(String serviceName, String groupName, com.alibaba.nacos.api.naming.pojo.Instance instance) {
    try {
      NamingService originNamingService = originNamingServices.get(namespace.key());
      instance.getMetadata().remove(METADATA_SYNC_OWNER_KEY, METADATA_SYNC_REBUILD_VALUE);
      originNamingService.registerInstance(serviceName, groupName, instance);
      log.info("[EXE] fix origin service instance , {} | {} | {}", serviceName, groupName, instance.getInstanceId());

    } catch (NacosException e) {
      log.warn("[EXE] fix origin service failed , code: {} ,err: {}", e.getErrCode(), e.getErrMsg());
      return false;
    }
    return true;
  }

  private String composeInstanceKey(com.alibaba.nacos.api.naming.pojo.Instance instance) {
    return Joiner.on(":").join(instance.getIp(),instance.getPort());
  }

  private boolean isSyncOwner(Map<String, String> metadata) {
    return metadata.containsKey(METADATA_SYNC_OWNER_KEY) && Objects.equals(METADATA_SYNC_OWNER_VALUE, metadata.get(METADATA_SYNC_OWNER_KEY));
  }

  private boolean isRebuildOwner(Map<String, String> metadata) {
    return metadata.containsKey(METADATA_SYNC_OWNER_KEY) && Objects.equals(METADATA_SYNC_REBUILD_VALUE, metadata.get(METADATA_SYNC_OWNER_KEY));
  }

  @Override
  public String getServiceName() {
    return service.toString();
  }
}
