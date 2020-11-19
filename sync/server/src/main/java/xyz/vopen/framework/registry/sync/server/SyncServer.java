package xyz.vopen.framework.registry.sync.server;

import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static io.undertow.websockets.jsr.WebSocketDeploymentInfo.ATTRIBUTE_NAME;

/**
 * {@link SyncServer}
 *
 * <p>Class SyncServer Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/17
 */
@SpringBootApplication
public class SyncServer {

  public static void main(String[] args) {
    SpringApplication.run(SyncServer.class, args);
  }

  @Bean
  public UndertowServletWebServerFactory undertowServletWebServerFactory() {
    UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
    factory.addBuilderCustomizers(
        builder -> builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true));
    return factory;
  }

  @Component
  public static class CustomizationBean
      implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
      factory.addDeploymentInfoCustomizers(
          deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute(ATTRIBUTE_NAME, webSocketDeploymentInfo);
          });
    }
  }
}
