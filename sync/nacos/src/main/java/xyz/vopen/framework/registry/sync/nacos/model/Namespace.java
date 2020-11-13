package xyz.vopen.framework.registry.sync.nacos.model;

import lombok.*;

import java.io.Serializable;

/**
 * {@link Namespace}
 *
 * <p>Class Namespace Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Namespace implements Serializable {

  private String namespace;

  private String namespaceShowName;

  private int quota;

  private int configCount;

  private int type;

}
