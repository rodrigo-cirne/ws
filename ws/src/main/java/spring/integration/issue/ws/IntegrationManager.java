package spring.integration.issue.ws;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowContext.IntegrationFlowRegistration;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.stereotype.Component;

@Component
public class IntegrationManager {

  public static final String FLOW_ID = "flowId";

  private final IntegrationFlowContext integrationFlowContext;

  private final WebSocketInboundChannelAdapter webSocketInboundChannelAdapter;

  public IntegrationManager(
    final IntegrationFlowContext integrationFlowContext,
    final WebSocketInboundChannelAdapter webSocketInboundChannelAdapter
  ) {
    this.integrationFlowContext = integrationFlowContext;
    this.webSocketInboundChannelAdapter = webSocketInboundChannelAdapter;
  }

  public void startFlow(final String id) {
    Optional.ofNullable(integrationFlowContext.getRegistrationById(id)).ifPresent(IntegrationFlowRegistration::start);
  }

  public void stopFlow(final String id) {
    Optional.ofNullable(integrationFlowContext.getRegistrationById(id)).ifPresent(IntegrationFlowRegistration::stop);
  }
  
  public void recreateLocationFlow() throws IOException {
    stopFlow(FLOW_ID);
    unregisterFlow();
    registerFlow();
    startFlow(FLOW_ID);
  }


  @PostConstruct
  private void init() throws IOException {
    registerFlow();
    startFlow(FLOW_ID);
  }

  private IntegrationFlow buildIntegrationFlow() {
    return IntegrationFlow.from(webSocketInboundChannelAdapter).get();
  }

  private void registerFlow() throws IOException {
    integrationFlowContext
        .registration(buildIntegrationFlow())
        .id(FLOW_ID)
        .autoStartup(false)
        .register();
  }

  private void unregisterFlow() {
    integrationFlowContext.remove(FLOW_ID);
  }
}
