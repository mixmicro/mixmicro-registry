package xyz.vopen.framework.registry.sync.nacos;

import lombok.*;
import xyz.vopen.framework.registry.sync.nacos.model.Instance;

import java.io.Serializable;
import java.util.List;

/**
 * {@link InstanceResponse}
 *
 * <p>Class InstanceResponse Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InstanceResponse implements Serializable {

  private int count;

  private List<Instance> list;
}
