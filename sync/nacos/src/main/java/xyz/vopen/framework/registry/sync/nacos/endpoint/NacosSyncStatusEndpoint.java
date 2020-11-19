package xyz.vopen.framework.registry.sync.nacos.endpoint;

import com.google.common.collect.Maps;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;

/**
 * {@link NacosSyncStatusEndpoint}
 *
 * <p>Class NacosSyncStatusEndpoint Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/14
 */
@Endpoint(id = "nacossyncstatus")
public class NacosSyncStatusEndpoint {



  @ReadOperation
  public Map<String, Object> invoke() {
    // TODO
    return Maps.newHashMap();
  }
}
