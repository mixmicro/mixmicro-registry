package xyz.vopen.framework.registry.sync.nacos.executors;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.registry.sync.nacos.model.response.InstanceResponse;
import xyz.vopen.framework.registry.sync.nacos.NacosService;
import xyz.vopen.framework.registry.sync.nacos.ServiceThread;
import xyz.vopen.framework.registry.sync.nacos.model.Instance;
import xyz.vopen.framework.registry.sync.nacos.model.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.METADATA_SYNC_OWNER_KEY;
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

  private final NamingService originNamingService;

  private final NamingService destNamingService;

  private final NacosService nacosService;

  private final String namespaceId;

  private final String authorization;

  private final Service service;

  private final boolean deregister;

  public NacosRegisterServiceExecutor(
      // Nacos Naming Service Instance
      NamingService originNamingService,
      NamingService destNamingService,
      // Nacos Console Service
      NacosService nacosService,
      String namespaceId,
      String authorization,
      Service service) {
    this(originNamingService, destNamingService, nacosService, namespaceId, authorization, service, false);
  }

  public NacosRegisterServiceExecutor(
      // Nacos Naming Service Instance
      NamingService originNamingService,
      NamingService destNamingService,
      // Nacos Console Service
      NacosService nacosService,
      String namespaceId,
      String authorization,
      Service service,
      boolean deregister) {
    super();
    this.authorization = authorization;
    this.namespaceId = namespaceId;
    this.originNamingService = originNamingService;
    this.destNamingService = destNamingService;
    this.nacosService = nacosService;
    this.service = service;
    this.deregister = deregister;
  }

  @Override
  public void run() {

    log.info("[EXE] begin process service : {}, {} , {} , {}", namespaceId, service.getClusterCount(), service.getGroupName(), service.getName());

    InstanceResponse response = nacosService.instances(authorization, service.getName(), service.getClusterName(), service.getGroupName(), namespaceId);

    log.info("[EXE] found service instances , count: {} ", response.getCount());

    List<Instance> instances = response.getList();

    // register instance into target nacos cluster
    for (Instance instance : instances) {

      boolean registered = registerInstance(service.getName(), service.getGroupName(), instance);

      log.info("[EXE] service instance :{} ,register result : {}", instance.getServiceName(), registered);

    }

    // subscribe service

    try {
      originNamingService.subscribe(
          service.getName(), // service name
          service.getGroupName(), // service group name
          event -> { // listener for service
            if (event instanceof NamingEvent) {

              try{

                NamingEvent namingEvent = (NamingEvent) event;

                Set<String> instanceKeySet = new HashSet<>();

                log.info("[SUBSCRIBE] received service event , {} | {} | {}",
                    namingEvent.getServiceName(), service.getGroupName(), namingEvent.getInstances() == null ? 0 : namingEvent.getInstances().size());

                // find all service s instance from origin nacos cluster .
                // warning ::
                //    if source cluster nginx is reloaded , need to be shutdown origin -> dest syncer server .
                //
                List<com.alibaba.nacos.api.naming.pojo.Instance> sourceInstances = originNamingService.getAllInstances(service.getName(), service.getGroupName());

                // register instance .
                for (com.alibaba.nacos.api.naming.pojo.Instance temp : sourceInstances) {
                  registerInstance(service.getName(), service.getGroupName(), build(temp));
                  instanceKeySet.add(composeInstanceKey(temp));
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
      destNamingService.registerInstance(serviceName, groupName, build(instance));
      log.info("[EXE] register service instance , {} | {} | {}", serviceName, groupName, instance.getInstanceId());

    } catch (NacosException e) {
      log.warn("[EXE] register service failed , code: {} ,err: {}", e.getErrCode(), e.getErrMsg());
      return false;
    }
    return true;
  }

  private String composeInstanceKey(com.alibaba.nacos.api.naming.pojo.Instance instance) {
    return Joiner.on(":").join(instance.getIp(),instance.getPort());
  }

  private boolean isSyncOwner(Map<String, String> metadata) {
    return metadata.containsKey(METADATA_SYNC_OWNER_KEY);
  }

  @Override
  public String getServiceName() {
    return service.toString();
  }
}
