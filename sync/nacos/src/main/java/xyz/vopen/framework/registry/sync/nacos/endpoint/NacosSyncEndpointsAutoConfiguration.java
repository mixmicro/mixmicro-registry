package xyz.vopen.framework.registry.sync.nacos.endpoint;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * {@link NacosSyncEndpointsAutoConfiguration}
 *
 * <p>Class NacosSyncEndpointsAutoConfiguration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/14
 */
@ConditionalOnWebApplication
@ConditionalOnClass(Endpoint.class)
public class NacosSyncEndpointsAutoConfiguration {

  @ConditionalOnMissingBean
  @ConditionalOnAvailableEndpoint
  @Bean
  public NacosSyncStatusEndpoint nacosSyncStatusEndpoint() {
    return new NacosSyncStatusEndpoint();
  }


  @ConditionalOnMissingBean
  @ConditionalOnAvailableEndpoint
  @Bean
  public NacosSyncOperationEndpoint nacosSyncOperationEndpoint() {
    return new NacosSyncOperationEndpoint();
  }
}
