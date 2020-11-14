package xyz.vopen.framework.registry.sync.nacos.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.*;

/**
 * {@link Instance}
 *
 * <p>Class Instance Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("DuplicatedCode")
public class Instance implements Serializable {

  private String instanceId;
  private String ip;
  private int port;
  private double weight;
  private boolean healthy;
  private boolean enabled;
  private boolean ephemeral;
  private String clusterName;
  private String serviceName;
  private long lastBeat;
  private boolean marked;
  private int instanceHeartBeatInterval;
  private int instanceHeartBeatTimeOut;
  private int ipDeleteTimeout;

  private Map<String, String> metadata;

  /**
   * Build Nacos Instance
   *
   * @param instance temp service instance
   * @return instance of {@link com.alibaba.nacos.api.naming.pojo.Instance}
   */
  public static com.alibaba.nacos.api.naming.pojo.Instance build(Instance instance) {
    com.alibaba.nacos.api.naming.pojo.Instance temp = new com.alibaba.nacos.api.naming.pojo.Instance();

    Map<String, String> metadata = instance.getMetadata();
    // Add extensional metadata .
    metadata.put(METADATA_SYNC_OWNER_KEY, METADATA_SYNC_OWNER_VALUE);
    metadata.put(METADATA_SYNC_TIMESTAMP_KEY, DATE_FORMAT.get().format(new Date()));

    temp.setMetadata(metadata);
    temp.setWeight(instance.getWeight());
    temp.setServiceName(instance.getServiceName());
    temp.setPort(instance.getPort());
    temp.setIp(instance.getIp());
    temp.setInstanceId(instance.getInstanceId());
    temp.setHealthy(instance.isHealthy());
    temp.setEphemeral(instance.isEphemeral());
    temp.setEnabled(instance.isEnabled());
    temp.setClusterName(instance.getClusterName());

    return temp;
  }

  /**
   * Build Nacos Instance
   *
   * @param instance temp service instance
   * @return instance of {@link com.alibaba.nacos.api.naming.pojo.Instance}
   */
  public static com.alibaba.nacos.api.naming.pojo.Instance build(com.alibaba.nacos.api.naming.pojo.Instance instance) {
    com.alibaba.nacos.api.naming.pojo.Instance temp = new com.alibaba.nacos.api.naming.pojo.Instance();

    Map<String, String> metadata = instance.getMetadata();
    // Add extensional metadata .
    metadata.put(METADATA_SYNC_OWNER_KEY, METADATA_SYNC_OWNER_VALUE);
    metadata.put(METADATA_SYNC_TIMESTAMP_KEY, DATE_FORMAT.get().format(new Date()));

    temp.setMetadata(metadata);
    temp.setWeight(instance.getWeight());
    temp.setServiceName(instance.getServiceName());
    temp.setPort(instance.getPort());
    temp.setIp(instance.getIp());
    temp.setInstanceId(instance.getInstanceId());
    temp.setHealthy(instance.isHealthy());
    temp.setEphemeral(instance.isEphemeral());
    temp.setEnabled(instance.isEnabled());
    temp.setClusterName(instance.getClusterName());

    return temp;
  }

}
