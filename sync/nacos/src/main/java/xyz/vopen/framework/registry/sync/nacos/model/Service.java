package xyz.vopen.framework.registry.sync.nacos.model;

import lombok.*;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Service implements Serializable {

  private String name;

  private String groupName;

  private int clusterCount;

  private String clusterName = "DEFAULT";

  private int ipCount;

  private int healthyInstanceCount;

}
