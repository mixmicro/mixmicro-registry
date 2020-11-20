package xyz.vopen.framework.registry.sync.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;

import java.util.Properties;

/**
 * {@link NacosConfigTest}
 *
 * <p>Class NacosConfigTest Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/20
 */
public class NacosConfigTest {

  public static void main(String[] args) throws NacosException, InterruptedException {

    Properties properties = new Properties();
    properties.put(PropertyKeyConst.NAMESPACE, "");
    properties.put(PropertyKeyConst.SERVER_ADDR, "127.0.0.1:18848");
    ConfigService configService = new NacosConfigService(properties);

    String content =
        configService.getConfigAndSignListener(
            "mixmicro-registry.properties",
            "DEFAULT_GROUP",
            5000,
            new AbstractListener() {
              @Override
              public void receiveConfigInfo(String configInfo) {

                System.out.println("[==]" + configInfo);
              }
            });

    System.out.println("[XX]: " + content);

    Thread.currentThread().join();
  }
}
