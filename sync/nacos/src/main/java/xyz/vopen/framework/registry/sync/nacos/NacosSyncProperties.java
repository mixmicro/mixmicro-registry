package xyz.vopen.framework.registry.sync.nacos;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

import static xyz.vopen.framework.registry.sync.nacos.NacosSyncProperties.PREFIX;

/**
 * {@link NacosSyncProperties}
 *
 * <p>Class NacosSyncProperties Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/14
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = PREFIX)
public class NacosSyncProperties implements Serializable {

  public static final String PREFIX = "mixmicro.registry.sync.nacos";

  @NestedConfigurationProperty private Origin origin;

  @NestedConfigurationProperty private Destination destination;


  // ~~ inner class

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Origin implements Serializable {

    private String consoleAddr;

    private String username;

    private String password;

  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Destination implements Serializable {

    private String serverAddr;

  }

}
