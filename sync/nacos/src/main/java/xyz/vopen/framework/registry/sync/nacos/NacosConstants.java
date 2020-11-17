package xyz.vopen.framework.registry.sync.nacos;

import java.text.SimpleDateFormat;

/**
 * {@link NacosConstants}
 *
 * <p>Class NacosConstants Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
public final class NacosConstants {

  /**
   * Source Nacos Console Server Address .
   *
   * <p>
   */
  public static final String SOURCE_NACOS_CONSOLE_ADDR_KEY = "${mixmicro.registry.sync.nacos.origin.console-addr}";

  public static final String DEST_NACOS_CONSOLE_ADDR_KEY = "${mixmicro.registry.sync.nacos.destination.console-addr}";

  public static final String METADATA_SYNC_OWNER_KEY = "mixmicro.registry.sync.owner";

  public static final String METADATA_SYNC_OWNER_VALUE = "syncer";

  public static final String METADATA_SYNC_TIMESTAMP_KEY = "mixmicro.registry.sync.timestamp";

  public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat(DEFAULT_FORMAT));
}
