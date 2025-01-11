package handlers;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ChatSession;
import models.Command;
import models.websockets.ExtendedAPIGatewayProxyRequestEvent;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import utils.DependencyFactory;
import utils.Responses;

/**
 * A handler to save (connectionId, roomId) pair to DynamoDB
 * This represents a "create or join" state
 */
public class Connect implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

    public static final String ENV_TABLE_NAME = "TABLE";

    private final ObjectMapper objectMapper;
    private final DynamoDbTable<ChatSession> table;

    // called by the lambda runtime
    public Connect() {
        this(DependencyFactory.dynamoDbEnhancedClient());
    }

    // Constructor with dependencies injected
    public Connect(DynamoDbEnhancedClient dbClient) {
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

        // Extract the connectionId from the request
        String connectionId = event.getRequestContext().getConnectionId();
        String roomId = command.getRoomId();
        if (roomId == null || roomId.isEmpty()){
            context.getLogger().log("roomId missing");
            return Responses.BadRequest("roomId missing");
        }
        context.getLogger().log(String.format("connectionId: %s, roomId: %s", connectionId, roomId));
        // Create a new chat session object
        ChatSession session = new ChatSession();
        session.setConnectionId(connectionId);
        session.setRoomId(roomId);

        context.getLogger().log("adding session: " + connectionId);
        try {
            table.putItem(session);
        } catch (UnsupportedOperationException e) {
            context.getLogger().log("Failed adding session with connectionId: " + e.getMessage());
            return Responses.Error(e);
        } catch (ResourceNotFoundException e){
            context.getLogger().log("Table not found: " + e.getMessage());
            return Responses.Error(e);
        }

        context.getLogger().log("success adding session: " + connectionId);
        return Responses.Success("Client " + connectionId + " Connected");
    }

    private ExtendedAPIGatewayProxyRequestEvent extractEvent(Map<String, Object> input) throws JsonProcessingException {
        String json = null;
        ExtendedAPIGatewayProxyRequestEvent event = null;

        json = objectMapper.writeValueAsString(input);
        event = objectMapper.readValue(json, ExtendedAPIGatewayProxyRequestEvent.class);
        return event;
    }
}
