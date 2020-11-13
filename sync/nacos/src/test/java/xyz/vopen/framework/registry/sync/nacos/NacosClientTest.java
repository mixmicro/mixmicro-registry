package xyz.vopen.framework.registry.sync.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Properties;

/**
 * {@link NacosClientTest}
 *
 * <p>Class NacosClientTest Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/11
 */
public class NacosClientTest {

  public static void main(String[] args) throws Exception {

    Properties properties = new Properties();
    properties.put(PropertyKeyConst.NAMESPACE, "public");
    properties.put(PropertyKeyConst.SERVER_ADDR, "beta-middle.hgj.net:8848");
    NamingService namingService = new NacosNamingService(properties);

    String serviceName = "mixmicro-register-demo-service-name";
    String groupName = "DEFAULT_GROUP";
    Instance instance = new Instance();
    instance.setClusterName("DEFAULT");
    instance.setEnabled(true);
    instance.setEphemeral(true);
    instance.setHealthy(true);
    instance.setInstanceId("mixmicro-register-demo-service-instance-id-1");
    instance.setIp("10.10.10.63");
    instance.setPort(22882);
    instance.setServiceName(serviceName);
    instance.setWeight(1.0);

    Map<String, String> metadata = Maps.newHashMap();
    metadata.put("side", "server");
    metadata.put("test", "true");
    instance.setMetadata(metadata);

    namingService.registerInstance(serviceName, groupName, instance);
    //        namingService.deregisterInstance(serviceName, instance);

    Thread.sleep(10000);
    // ============================================================

    //    Instance instance1 = new Instance();
    //    instance1.setClusterName("DEFAULT");
    //    instance1.setEnabled(true);
    //    instance1.setEphemeral(true);
    //    instance1.setHealthy(true);
    //    instance1.setInstanceId("mixmicro-register-demo-service-instance-id-2");
    //    instance1.setIp("10.10.10.63");
    //    instance1.setPort(8099);
    //    instance1.setServiceName(serviceName);
    //    instance1.setWeight(1.0);
    //    metadata.put("test", "true");
    //    instance1.setMetadata(metadata);

    metadata.put("test", "false");
    instance.setMetadata(metadata);

    namingService.registerInstance(serviceName, groupName, instance);
    //        namingService.deregisterInstance(serviceName, instance1);

    Thread.currentThread().join();

  }
}
