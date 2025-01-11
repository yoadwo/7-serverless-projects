package handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ChatSession;
import models.Command;
import models.websockets.ExtendedAPIGatewayProxyRequestEvent;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import utils.DependencyFactory;
import utils.Responses;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Handler to send message to all registered connections to a given room ID
 */
public class SendMessage implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

    public static final String ENV_TABLE_NAME = "TABLE";

    private final ObjectMapper objectMapper;
    private final DynamoDbTable<ChatSession> table;

    // called by the lambda runtime
    public SendMessage() {
        this(DependencyFactory.dynamoDbEnhancedClient());
    }

    public SendMessage(DynamoDbEnhancedClient dbClient) {
        table = dbClient.table(System.getenv(ENV_TABLE_NAME), TableSchema.fromBean(ChatSession.class));
        objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final Map<String, Object> input, final Context context) {

        ExtendedAPIGatewayProxyRequestEvent event;
        Command command;
        try {
            event = extractEvent(input);
            command = objectMapper.readValue(event.getBody(), Command.class);
        } catch (JsonProcessingException e) {
            context.getLogger().log("API Gateway event deserialization error: " + e.getMessage());
            return Responses.Error(e);
        }

        String roomId = command.getRoomId();
        if (roomId == null || roomId.isEmpty()){
            context.getLogger().log("roomId missing");
            return Responses.BadRequest("roomId missing");
        }

        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                .attributesToProject("connectionId")
                .filterExpression(Expression.builder()
                        .expression("roomId = :roomId")
                        .expressionValues(Map.of(":roomId", AttributeValue.builder().s(roomId).build()))
                        .build())
                .build();

        List<ChatSession> items;

        context.getLogger().log("scanning for sessions" + "\n");
        try {
            items = table.scan(scanRequest).items().stream().toList();
        } catch (UnsupportedOperationException e) {
            context.getLogger().log("Get connections error: " + e.getMessage());
            return Responses.Error(e);
        }
        context.getLogger().log("success scanning for sessions" + "\n");

        int successfulMessages = 0;
        String endpoint = String.format("https://%s/%s",
                event.getRequestContext().getDomainName(), event.getRequestContext().getStage()
        );

        try (var apiClient = ApiGatewayManagementApiClient.builder()
                .endpointOverride(URI.create(endpoint))
                .build()) {

            for (ChatSession session : items) {
                String connectionId = session.getConnectionId();
                try {
                PostToConnectionRequest request = PostToConnectionRequest.builder()
                        .connectionId(connectionId)
                        .data(SdkBytes.fromUtf8String(command.getMessage()))
                        .build();

                apiClient.postToConnection(request);
                context.getLogger().log("Successfully sent command to connection: " + connectionId + "\n");
                successfulMessages++;
                } catch (GoneException e) {
                    // Connection is no longer available, you might want to clean it up from your database
                    context.getLogger().log("Invalid client ID, check if exists: " + connectionId + "\n");
                } catch (Exception e) {
                    context.getLogger().log("Error sending command to connection " + connectionId + ": " + e.getMessage() + "\n");
                }
            }
        }

        return Responses.Success(String.format("sent %d messages", successfulMessages));
    }

    private ExtendedAPIGatewayProxyRequestEvent extractEvent(Map<String, Object> input) throws JsonProcessingException {
        String json = null;
        ExtendedAPIGatewayProxyRequestEvent event = null;

        json = objectMapper.writeValueAsString(input);
        event = objectMapper.readValue(json, ExtendedAPIGatewayProxyRequestEvent.class);
        return event;
    }
}
