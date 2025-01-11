package models.websockets;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class ExtendedAPIGatewayProxyRequestEvent extends APIGatewayProxyRequestEvent {
    @JsonDeserialize(as = ExtendedProxyContextRequest.class)
    private ExtendedProxyContextRequest requestContext;

    public ExtendedAPIGatewayProxyRequestEvent() {
        requestContext = new ExtendedProxyContextRequest();
    }

    public ExtendedProxyContextRequest getRequestContext() {
        return requestContext;
    }

    @Override
    public void setRequestContext(ProxyRequestContext requestContext) {
        this.requestContext = (ExtendedProxyContextRequest) requestContext;
    }
}
