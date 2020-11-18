package xyz.vopen.framework.registry.sync.nacos.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * {@link SerializableBean}
 *
 * <p>Class SerializableBean Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/18
 */
public class SerializableBean implements Serializable {

  protected static final Gson G =
      new Gson().newBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

  @Override
  public String toString() {
    return G.toJson(this);
  }
}
