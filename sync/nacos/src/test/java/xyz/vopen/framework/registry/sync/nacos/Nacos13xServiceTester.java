package xyz.vopen.framework.registry.sync.nacos;

import com.alibaba.fastjson.JSON;
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
import xyz.vopen.framework.registry.sync.nacos.v13x.Nacos13xService;
import xyz.vopen.framework.registry.sync.nacos.v13x.model.AccessToken;

import java.util.List;

/**
 * {@link Nacos13xServiceTester}
 *
 * <p>Class NacosServiceTester Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NacosClientApp.class)
public class Nacos13xServiceTester {

  @Autowired private Nacos13xService nacos13xService;

  @Test
  public void login() throws Exception {

    AccessToken accessToken = nacos13xService.login("nacos", "nacos");

    Response<List<Namespace>> namespaces = nacos13xService.namespaces(accessToken);

    System.out.println(JSON.toJSONString(namespaces, false));

    // FOREACH <namespaces>
    ServiceResponse services = nacos13xService.services(accessToken.toString(), accessToken.getAccessToken(), "public");

    System.out.println(JSON.toJSONString(services, false));

    // FOREACH <services>
    Service service = services.getServiceList().get(0);

    // FOREACH register <instances>
    InstanceResponse instances = nacos13xService.instances(accessToken, service.getName(),"public");

    System.out.println(JSON.toJSONString(instances, true));

//    // FOREACH subscribe <services>

  }
}
