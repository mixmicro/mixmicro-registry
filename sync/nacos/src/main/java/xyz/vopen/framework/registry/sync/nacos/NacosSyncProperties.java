package xyz.vopen.framework.registry.sync.nacos;

import com.google.common.collect.Sets;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.*;
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
public class NacosSyncProperties implements Serializable, InitializingBean {

  public static final String PREFIX = "mixmicro.registry.sync.nacos";

  @NestedConfigurationProperty private Origin origin;

  @NestedConfigurationProperty private Destination destination;

  @Builder.Default
  @NestedConfigurationProperty private SyncRule syncRule = new SyncRule();

  /**
   * is enabled deregister flag .
   * <p>default: false</p>
   */
  @Builder.Default private boolean deregister = false;

  @Builder.Default private Rebuild rebuild = new Rebuild();

  @Builder.Default private Fix fix = new Fix();

  @Builder.Default private CoreConfig core = new CoreConfig();

  @Override
  public void afterPropertiesSet() {
    if(syncRule != null) {
      syncRule.afterPropertiesSet();
    }
  }

  // ~~ inner class

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CoreConfig implements Serializable {

    private String configServerAddr;

    @Builder.Default private String namespaceId = DEFAULT_CONFIG_NAMESPACE_ID;

    @Builder.Default private String dataId = "mixmicro-registry.properties";

    @Builder.Default private String group = DEFAULT_GROUP;

    @Builder.Default private long timeout = 5000;

  }


  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Origin implements Serializable {

    private String consoleAddr;

    private String serverAddr;

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

    private String consoleAddr;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Rebuild implements Serializable {

    @Builder.Default private boolean enabled = true;

    @Builder.Default private long checkInterval = 5000;

    @Builder.Default private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Fix implements Serializable {

    @Builder.Default private boolean enabled = false;

    /**
     * Startup Delay
     *
     * <p>default: 20 s</p>
     */
    @Builder.Default private long delay = 20 * 1000;

    @Builder.Default private long checkInterval = 5000;

    @Builder.Default private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SyncRule implements Serializable, InitializingBean {

    private static final String ALL_REGULAR = "*";

    private static final String SEPARATOR = ";";

    private static final Set<String> namespaces = Sets.newHashSet();
    private static final Set<String> services = Sets.newHashSet();

    @Builder.Default private String namespaceRegular = ALL_REGULAR;

    @Builder.Default private String serviceRegular = ALL_REGULAR;

    public boolean matchNamespace(@NonNull String namespace) {
      if (StringUtils.isBlank(namespace)) {
        return true;
      }

      if (Objects.equals(ALL_REGULAR, namespaceRegular)) {
        return true;
      }

      return namespaces.contains(namespace.toLowerCase());
    }

    public boolean matchService(@NonNull String serviceName) {
      if (StringUtils.isBlank(serviceName)) {
        return true;
      }

      if (Objects.equals(ALL_REGULAR, serviceRegular)) {
        return true;
      }

      return services.contains(serviceName.toLowerCase());
    }

    @Override
    public void afterPropertiesSet() {
      for (String temp : namespaceRegular.split(SEPARATOR)) {
        namespaces.add(temp.toLowerCase());
      }
      for (String temp : serviceRegular.split(SEPARATOR)) {
        services.add(temp.toLowerCase());
      }
    }
  }
}
