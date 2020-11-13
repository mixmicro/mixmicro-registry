package xyz.vopen.framework.registry.sync.nacos.executors;

import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.framework.registry.sync.nacos.NacosService;
import xyz.vopen.framework.registry.sync.nacos.model.Service;

/**
 * {@link NacosRegisterServiceExecutor}
 *
 * <p>Class NacosRegisterServiceExecutor Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
public class NacosRegisterServiceExecutor implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(NacosRegisterServiceExecutor.class);

  private final NamingService namingService;

  private final NacosService nacosService;

  private final Service service;

  public NacosRegisterServiceExecutor(NamingService namingService, NacosService nacosService, Service service) {
    this.namingService = namingService;
    this.nacosService = nacosService;
    this.service = service;
  }

  @Override
  public void run() {

    //


  }
}
