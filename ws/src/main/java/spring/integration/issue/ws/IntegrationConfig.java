package spring.integration.issue.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class IntegrationConfig {
  
  private static final String WS_URI = "wss://localhost:8811/ws";

  @Bean
  MessageChannel webSocketOutputChannel() {
    return MessageChannels.direct().getObject();
  }

  @Bean
  MessageChannel webSocketErrorChannel() {
    return MessageChannels.direct().getObject();
  }

  @Bean
  WebSocketClient webSocketClient() {
    return new StandardWebSocketClient();
  }

  @Bean
  ClientWebSocketContainer clientWebSocketContainer() {
    return new ClientWebSocketContainer(webSocketClient(), WS_URI);
  }

  @Bean
  WebSocketInboundChannelAdapter webSocketInboundChannelAdapter() {
    return new WebSocketInboundChannelAdapter(clientWebSocketContainer());
  }

  @ServiceActivator(inputChannel = "errorChannel")
  public void handleError(final ErrorMessage errorMessage) {
    System.out.println("!!! Integration error: " + errorMessage.getPayload());
  }
}
