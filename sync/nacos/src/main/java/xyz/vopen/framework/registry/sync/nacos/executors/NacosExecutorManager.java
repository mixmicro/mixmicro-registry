package xyz.vopen.framework.registry.sync.nacos.executors;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.lang.NonNull;
import xyz.vopen.framework.registry.sync.nacos.model.Namespace;
import xyz.vopen.framework.registry.sync.nacos.model.Service;
import xyz.vopen.mixmicro.kits.Assert;

import java.util.Map;
import java.util.Set;

/**
 * {@link NacosExecutorManager}
 *
 * <p>Class NacosExecutorManager Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
public class NacosExecutorManager {

  private static final String DEFAULT_NAMESPACE = "public";

  // ~~ Instance Builder

  private NacosExecutorManager() {}

  private static class InstanceHolder {
    private static final ThreadLocal<NacosExecutorManager> INSTANCE =
        ThreadLocal.withInitial(NacosExecutorManager::new);
  }

  public static NacosExecutorManager manager() {
    return InstanceHolder.INSTANCE.get();
  }

  // ~~ Context Properties

  private static volatile Map<String, Service> running_services = Maps.newConcurrentMap();

  static volatile Set<String> fixRebuildInstances = Sets.newHashSet();

  /**
   * Save Running Service Instance
   *
   * @param namespace instance of {@link Namespace}
   * @param service instance of {@link Service}
   * @return the previous value associated with the specified key, or {@code null} if there was no
   *     mapping for the key. (A {@code null} return can also indicate that the map previously
   *     associated {@code null} with the key, if the implementation supports null values.)
   */
  @SuppressWarnings("UnusedReturnValue")
  public Service putIfAbsent(@NonNull Namespace namespace, @NonNull Service service) {
    Assert.notNull(namespace, "namespace must not be null");
    Assert.notNull(service, "service instance must not be null");
    String key = Joiner.on("@@").join(namespace.key(), service.toString());
    return running_services.put(key, service);
  }

  /**
   * Returns {@code true} if this map contains a mapping for the specified key. More formally,
   * returns {@code true} if and only if this map contains a mapping for a key {@code k} such that
   * {@code Objects.equals(key, k)}. (There can be at most one such mapping.)
   *
   * @param namespace instance of {@link Namespace}
   * @param service instance of {@link Service}
   * @return {@code true} if this map contains a mapping for the specified key
   */
  public boolean containsKey(@NonNull Namespace namespace, @NonNull Service service) {
    Assert.notNull(namespace, "namespace must not be null");
    Assert.notNull(service, "service instance must not be null");
    String key = Joiner.on("@@").join(namespace.key(), service.toString());
    return running_services.containsKey(key);
  }

  /**
   * * Removes the mapping for a key from this map if it is present * (optional operation). More
   * formally, if this map contains a mapping * from key {@code k} to value {@code v} such that *
   * {@code Objects.equals(key, k)}, that mapping * is removed. (The map can contain at most one
   * such mapping.)
   *
   * @param namespace namespace instance
   * @param serviceName service name
   */
  public void remove(@NonNull Namespace namespace, @NonNull String serviceName) {
    Assert.notNull(namespace, "namespace must not be null");
    Assert.notNull(serviceName, "service name must not be null");
    String key = Joiner.on("@@").join(namespace.key(), serviceName);
    running_services.remove(key);
  }
}
