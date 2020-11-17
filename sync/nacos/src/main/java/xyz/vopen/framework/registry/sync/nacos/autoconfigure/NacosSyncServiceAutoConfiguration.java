package xyz.vopen.framework.registry.sync.nacos.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import xyz.vopen.framework.registry.sync.nacos.NacosService;
import xyz.vopen.framework.registry.sync.nacos.NacosSyncProperties;
import xyz.vopen.framework.registry.sync.nacos.NacosSyncService;
import xyz.vopen.mixmicro.components.boot.httpclient.MixHttpClientScan;

/**
 * {@link NacosSyncServiceAutoConfiguration}
 *
 * <p>Class NacosSyncServiceAutoConfiguration Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/14
 */
@Configuration
@EnableConfigurationProperties(NacosSyncProperties.class)
@MixHttpClientScan(basePackageClasses = NacosService.class)
public class NacosSyncServiceAutoConfiguration {

  // ~~ logger bean defined .

  private final static Logger log = LoggerFactory.getLogger(NacosSyncServiceAutoConfiguration.class);

  @Primary
  @Bean(destroyMethod = "destroy")
  public NacosSyncService nacosSyncService(NacosSyncProperties properties, NacosService nacosService) {

    log.info("[SYNC] nacos sync service auto-configuring ...");

    return new NacosSyncService(properties, nacosService);
  }

  // ~~ application event listener bean defined .

  @Bean
  public ApplicationLifecycleListener lifecycleListener() {
    return new ApplicationLifecycleListener();
  }


  // ~~ listener .

  public static class ApplicationLifecycleListener implements ApplicationListener<ApplicationEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {

      if(event instanceof ApplicationReadyEvent) {

        ApplicationReadyEvent readyEvent = (ApplicationReadyEvent) event;

        NacosSyncService service = readyEvent.getApplicationContext().getBean(NacosSyncService.class);

        log.info("[SYNC] startup sync service ...");

        service.startup();

      }
    }
  }

}
