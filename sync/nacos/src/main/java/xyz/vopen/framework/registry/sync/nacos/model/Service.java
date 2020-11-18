package xyz.vopen.framework.registry.sync.nacos.model;

import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * {@link Service}
 *
 * <p>Class ServiceInstance Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service implements Serializable {

  private String name;

  private String groupName;

  private int clusterCount;

  private String clusterName = "DEFAULT";

  private int ipCount;

  private int healthyInstanceCount;

  private boolean triggerFlag;

  @Override
  public String toString() {
    return Joiner.on("@@").join(clusterName, groupName, name);
  }
}
