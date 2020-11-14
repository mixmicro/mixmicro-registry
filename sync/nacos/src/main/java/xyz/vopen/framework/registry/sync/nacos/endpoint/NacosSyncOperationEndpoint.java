package xyz.vopen.framework.registry.sync.nacos.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.http.ResponseEntity;

/**
 * {@link NacosSyncOperationEndpoint}
 *
 * <p>Class NacosSyncOperationEndpoint Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/14
 */
@Endpoint(id = "nacos-sync-operation")
public class NacosSyncOperationEndpoint {

  @WriteOperation
  public ResponseEntity<?> execute(@Selector String namespace) {

    // TODO
    return ResponseEntity.ok().build();
  }
}
