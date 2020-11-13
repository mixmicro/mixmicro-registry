package xyz.vopen.framework.registry.sync.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.vopen.mixmicro.components.boot.httpclient.MixHttpClientScan;

/**
 * {@link NacosClientApp}
 *
 * <p>Class NacosClientApp Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@SpringBootApplication
@MixHttpClientScan("xyz.vopen.framework.registry.sync.nacos")
public class NacosClientApp {

  public static void main(String[] args) {
    SpringApplication.run(NacosClientApp.class, args);
  }
}
