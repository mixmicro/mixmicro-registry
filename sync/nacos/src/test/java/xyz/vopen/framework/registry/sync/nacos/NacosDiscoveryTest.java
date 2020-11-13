package xyz.vopen.framework.registry.sync.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

/**
 * {@link NacosDiscoveryTest}
 *
 * <p>Class NacosClientTest Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/11
 */
public class NacosDiscoveryTest {

  @Test
  public void connect() throws Exception {

    Properties properties = new Properties();
    properties.put(PropertyKeyConst.NAMESPACE, "public");
    properties.put(PropertyKeyConst.SERVER_ADDR, "beta-middle.hgj.net:8848");
    NamingService namingService = new NacosNamingService(properties);

    new Thread(
            () -> {
              while (true) {

                List<Instance> instances2 = null;
                try {
                  instances2 = namingService.getAllInstances("mixmicro-register-demo-service-name");
                } catch (NacosException e) {
                  e.printStackTrace();
                }

                System.out.println(instances2);

                try {
                  Thread.sleep(2000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            })
        .start();

    Thread.currentThread().join();
  }
}
