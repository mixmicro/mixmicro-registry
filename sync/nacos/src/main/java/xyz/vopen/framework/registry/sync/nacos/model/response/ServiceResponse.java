package xyz.vopen.framework.registry.sync.nacos.model.response;

import lombok.*;
import xyz.vopen.framework.registry.sync.nacos.model.Service;

import java.io.Serializable;
import java.util.List;

/**
 * {@link ServiceResponse}
 *
 * <p>Class ServiceResponse Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse implements Serializable {

  private List<Service> serviceList;

  private int count;

}
