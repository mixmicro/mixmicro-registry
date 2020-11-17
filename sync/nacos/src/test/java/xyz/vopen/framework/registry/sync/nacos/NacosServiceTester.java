package xyz.vopen.framework.registry.sync.nacos;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.vopen.framework.registry.sync.nacos.model.Namespace;
import xyz.vopen.framework.registry.sync.nacos.model.Service;
import xyz.vopen.framework.registry.sync.nacos.model.response.InstanceResponse;
import xyz.vopen.framework.registry.sync.nacos.model.response.Response;
import xyz.vopen.framework.registry.sync.nacos.model.response.ServiceResponse;

import java.util.List;

/**
 * {@link NacosServiceTester}
 *
 * <p>Class NacosServiceTester Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NacosClientApp.class)
public class NacosServiceTester {

  @Autowired private NacosService nacosService;

  @Test
  public void login() throws Exception {

    Response<String> response = nacosService.login("nacos", "nacos");

    System.out.println(response);

    String authorization = response.getData();

    Response<List<Namespace>> namespaces = nacosService.namespaces(authorization);

//    System.out.println(JSON.toJSONString(namespaces, false));

    // FOREACH <namespaces>
    ServiceResponse services = nacosService.services(authorization, "public");

//    System.out.println(JSON.toJSONString(services, false));

    // FOREACH <services>
    Service service = services.getServiceList().get(0);

    // FOREACH register <instances>
    InstanceResponse instances = nacosService.instances(authorization, service.getName(),"public");

//    System.out.println(JSON.toJSONString(instances, true));

    // FOREACH subscribe <services>

    /*
    namingService.subscribe("service-name", new EventListener() {
      @Override
      public void onEvent(Event event) {
        NamingEvent namingEvent = (NamingEvent) event;
        //
      }
    });
    */

  }
}
