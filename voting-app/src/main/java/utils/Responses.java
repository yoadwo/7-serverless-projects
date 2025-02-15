package utils;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

public class Responses {

    private static final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
            .withHeaders(new HashMap<>(Map.of(
                    "Content-Type", "application/json"))
            );
    public static APIGatewayProxyResponseEvent Error(Exception e){
        return response
                .withBody(String.format("{\"error\": \"%s\"}", e.getMessage()))
                .withStatusCode(500);
    }

    public static APIGatewayProxyResponseEvent BadRequest(String details){
        return response
                .withBody(String.format("{\"missing input\": \"%s\"}", details))
                .withStatusCode(400);
    }

    public static APIGatewayProxyResponseEvent Success(String message) {
        return response
                .withStatusCode(200)
                .withBody(String.format("{\"message\": \"%s\"}", message));
    }
}
