package xyz.vopen.framework.registry.sync.nacos.model;

import lombok.*;

import java.io.Serializable;
import java.util.Map;

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
public class Instance implements Serializable {

  private String instanceId;
  private String ip;
  private int port;
  private int weight;
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
}
