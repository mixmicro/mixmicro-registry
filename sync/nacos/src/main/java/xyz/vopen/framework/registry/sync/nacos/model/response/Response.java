package xyz.vopen.framework.registry.sync.nacos.model.response;

import lombok.*;

import java.io.Serializable;

/**
 * {@link Response}
 *
 * <p>Class BaseResponse Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

  private int code;

  private String message;

  private T data;
}
