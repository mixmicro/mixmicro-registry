package xyz.vopen.framework.registry.sync.nacos.config;

import xyz.vopen.framework.registry.sync.nacos.NacosSyncProperties;

/**
 * {@link DynamicConfigServiceTest}
 *
 * <p>Class DynamicConfigServiceTest Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/20
 */
class DynamicConfigServiceTest {

  public static void main(String[] args) throws InterruptedException {

    NacosSyncProperties.CoreConfig config = NacosSyncProperties.CoreConfig.builder().build();
    config.setConfigServerAddr("127.0.0.1:8848");

    DynamicConfigService service = new DynamicConfigService(config);

    service.startup();

    Thread thread =
        new Thread(
            () -> {
              while (true) {
                try {
                  DynamicConfigService.CoreConfigProperties result = service.getConfig();
                  System.out.println("config result: " + result.toString());
                  Thread.sleep(5000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            });

    thread.setName("config-loop");
    thread.setDaemon(true);
    thread.start();

    Thread.currentThread().join();
  }
}
