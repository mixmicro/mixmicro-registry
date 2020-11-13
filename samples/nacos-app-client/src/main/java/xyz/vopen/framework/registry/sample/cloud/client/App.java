package xyz.vopen.framework.registry.sample.cloud.client;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * {@link App}
 *
 * <p>Class App Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/11
 */
@SpringCloudApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
