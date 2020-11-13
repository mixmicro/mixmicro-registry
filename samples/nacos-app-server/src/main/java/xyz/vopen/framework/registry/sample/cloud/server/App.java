package xyz.vopen.framework.registry.sample.cloud.server;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @RestController
  public static class Api {

    @GetMapping("/api")
    String api(@RequestParam("name") String name) {
      return "Hello ,".concat(name);
    }
  }
}
