package xyz.vopen.framework.registry.sync.nacos.config;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import xyz.vopen.framework.registry.sync.nacos.NacosSyncProperties;
import xyz.vopen.mixmicro.kits.Assert;
import xyz.vopen.mixmicro.kits.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.CORE_CONFIG_PREFIX;
import static xyz.vopen.framework.registry.sync.nacos.config.DynamicConfigService.CoreConfigKeys.MIGRATED;

/**
 * {@link DynamicConfigService}
 *
 * <p>Class ConfigService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/20
 */
@SuppressWarnings("UnusedReturnValue")
public class DynamicConfigService {

  private static final Logger log = LoggerFactory.getLogger(DynamicConfigService.class);

  private final NacosSyncProperties.CoreConfig config;

  public DynamicConfigService(@NonNull NacosSyncProperties.CoreConfig config) {
    Assert.notNull(config);
    this.config = config;
  }

  ConfigService configService;

  // ~~ start up method .

  public void startup() {
    try {
      Properties properties = new Properties();
      properties.put(PropertyKeyConst.NAMESPACE, config.getNamespaceId());
      properties.put(PropertyKeyConst.SERVER_ADDR, config.getConfigServerAddr());
      configService = new NacosConfigService(properties);

      // get init config and register config listener .
      String configContent = configService.getConfigAndSignListener(config.getDataId(), config.getGroup(), config.getTimeout(), new AbstractListener() {
        @Override
        public void receiveConfigInfo(String configInfo) {

          try{
            log.info("[CONFIG] received new config , content : {}", configInfo);
            Properties temp = new Properties();
            temp.load(new StringReader(configInfo));
            DynamicCoreConfigContext.refreshCoreConfigProperties(temp);
          } catch (Exception e) {
            log.warn("[CONFIG-LISTENER] process config listener failed", e);
          }
        }
      });

      Properties temp = new Properties();
      if(StringUtils.isNotBlank(configContent)) {
        temp.load(new StringReader(configContent));
      }

      CoreConfigProperties cc = DynamicCoreConfigContext.refreshCoreConfigProperties(temp);

      log.info("[CONFIG] remote config: {}", cc.toString());

    } catch (NacosException e) {
      log.error("[CONFIG] initialize nacos config service failed", e);
    } catch (IOException e) {
      log.error("[CONFIG] load remote config content failed", e);
    }
  }


  public CoreConfigProperties getConfig() {
    return DynamicCoreConfigContext.getCoreConfigProperties();
  }

  @Getter
  @Setter
  public static class DynamicCoreConfigContext implements Serializable {

    // ~~
    private static AtomicReference<CoreConfigProperties> ccref = new AtomicReference<>();

    /**
     * Return instance of {@link CoreConfigProperties}
     *
     * @return cache instance
     */
    static CoreConfigProperties getCoreConfigProperties() {
      return ccref.get();
    }

    /**
     * Refresh Cached {@link CoreConfigProperties} value
     *
     * @param properties supported properties
     * @return cache instance
     */
    static CoreConfigProperties refreshCoreConfigProperties(CoreConfigProperties properties) {
      ccref.set(properties);
      return properties;
    }

    static CoreConfigProperties refreshCoreConfigProperties(@NonNull Properties properties) {
      CoreConfigProperties coreConfigProperties = CoreConfigProperties.of(properties);
      ccref.set(coreConfigProperties);
      return coreConfigProperties;
    }
  }

  // ~~ Keys Defined

  public enum CoreConfigKeys {

    /**
     * Is the migration complete ?
     *
     * <p>default: false
     */
    MIGRATED(CORE_CONFIG_PREFIX.concat(".migrated"), "false"),
    ;

    private final String key;
    private final String defaultValue;

    CoreConfigKeys(String key, String defaultValue) {
      this.key = key;
      this.defaultValue = defaultValue;
    }

    public String key() {
      return key;
    }

    public String defaultValue() {
      return defaultValue;
    }
  }

  @Getter
  @Setter
  @Builder
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CoreConfigProperties implements Serializable {

    @Builder.Default private boolean migrated = false;

    /**
     * Build {@link CoreConfigProperties} with supported {@link Properties}
     *
     * @param properties supported properties
     * @return instance of {@link CoreConfigProperties}
     */
    public static CoreConfigProperties of(@NonNull Properties properties) {

      Assert.notNull(properties, "support properties must not be null.");

      CoreConfigProperties config =
          CoreConfigProperties.builder()
              // build `migrated` properties
              .migrated(Boolean.parseBoolean(properties.getOrDefault(MIGRATED.key(), MIGRATED.defaultValue()).toString()))
              // ... any other config
              // build
              .build();

      // ~~

      return config;
    }
  }
}
