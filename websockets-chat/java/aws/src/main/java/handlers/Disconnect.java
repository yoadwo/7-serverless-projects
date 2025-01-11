package handlers;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ChatSession;
import models.websockets.ExtendedAPIGatewayProxyRequestEvent;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import utils.DependencyFactory;
import utils.Responses;

/**
 * A handler to remove (connectionId, roomId) pair from DynamoDB
 */
public class Disconnect implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

    public static final String ENV_TABLE_NAME = "TABLE";

    private final ObjectMapper objectMapper;
    private final DynamoDbTable<ChatSession> table;

    // called by the lambda runtime
    public Disconnect() {
        this(DependencyFactory.dynamoDbEnhancedClient());
    }

    public Disconnect(DynamoDbEnhancedClient dbClient) {
        table = dbClient.table(System.getenv(ENV_TABLE_NAME), TableSchema.fromBean(ChatSession.class));
        objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final Map<String, Object> input, final Context context) {

        String json = null;
        ExtendedAPIGatewayProxyRequestEvent event = null;
        try {
            json = objectMapper.writeValueAsString(input);
            event = objectMapper.readValue(json, ExtendedAPIGatewayProxyRequestEvent.class);
        } catch (JsonProcessingException e) {
            context.getLogger().log("API Gateway event serialization error: " + e.getMessage());
            return Responses.Error(e);
        }

        // Extract the connectionId from the request
        String connectionId = event.getRequestContext().getConnectionId();
        context.getLogger().log("deleting session: " + connectionId);
        try {
            table.deleteItem(Key.builder().partitionValue(connectionId).build());
            context.getLogger().log("success deleting session: " + connectionId);
            return Responses.Success("Client " + connectionId + " Disconnected");
        } catch (UnsupportedOperationException e) {
            context.getLogger().log("Failed removing session with connectionId: " + e.getMessage());
            return Responses.Error(e);
        }
    }
}
