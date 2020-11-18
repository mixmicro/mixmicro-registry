package xyz.vopen.framework.registry.sample.cloud.client;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * {@link App}
 *
 * <p>Class App Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/11
 */
@SpringCloudApplication
@EnableFeignClients
@EnableScheduling
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }


  // ~~ remote api client defined .

  @FeignClient(
      name = "sample-nacos-server-app",
      fallback = ApiClientFallback.class,
      configuration = FeignConfiguration.class)
  public interface ApiClient {

    @GetMapping("/api")
    String api(@RequestParam("name") String name);
  }


  @RestController
  @RequestMapping("/")
  public static class ClientApiController {

    private final ApiClient apiClient;

    public ClientApiController(ApiClient apiClient) {
      this.apiClient = apiClient;
    }

    @GetMapping("/api")
    public String api() {
      String name = UUID.randomUUID().toString();
      Map<String, String> result = Maps.newHashMap();
      result.put("name", apiClient.api(name));
      return JSON.toJSONString(result);
    }
  }


}


// ~~ Feign Configuration Defined .

class FeignConfiguration {

  @Bean
  public ApiClientFallback apiClientFallback() {
    return new ApiClientFallback();
  }
}

// ~~ Fallback Client Defined

class ApiClientFallback implements App.ApiClient {

  @Override
  public String api(String name) {
    return "api fallback";
  }
}

@Component
class ScheduleTask {

  private final App.ApiClient apiClient;

  ScheduleTask(App.ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  @Scheduled(initialDelay = 10000, fixedRate = 2000)
  public void execute() {

    String name = UUID.randomUUID().toString();
    String result = apiClient.api(name);

    System.out.printf("execute, name: %s , result : %s , time: %tc%n \r\n", name, result, new Date());
  }

}
