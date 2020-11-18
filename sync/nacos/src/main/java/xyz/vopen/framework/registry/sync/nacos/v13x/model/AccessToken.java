package xyz.vopen.framework.registry.sync.nacos.v13x.model;

import lombok.*;
import xyz.vopen.framework.registry.sync.nacos.model.SerializableBean;

/**
 * {@link AccessToken}
 *
 * <p>Class AccessToken Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/18
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken extends SerializableBean {

  private String accessToken;

  private int tokenTtl;

  private boolean globalAdmin;
}
