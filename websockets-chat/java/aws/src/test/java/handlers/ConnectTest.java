package handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import models.ChatSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ConnectTest {

    @Mock
    DynamoDbEnhancedClient mockClient;
    @Mock
    DynamoDbTable<ChatSession> mockTable;
    @Mock
    Context mockContext;
    @Mock
    LambdaLogger mockLogger;

    @Test
    public void whenRoomIdExists_shouldSaveToChatTable() {
        when(mockClient.table(
                any(),
                eq(TableSchema.fromBean(ChatSession.class))))
                .thenReturn(mockTable);
        when(mockContext.getLogger()).thenReturn(mockLogger);

        Map<String, Object> input = new HashMap<>();
        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("connectionId", "1234");
        input.put("requestContext", requestContext);
        input.put("body", "{\"action\":\"connectToRoom\",\"roomId\":\"room2\"}");

        var connect = new Connect(mockClient);
        APIGatewayProxyResponseEvent result = connect.handleRequest(input, mockContext);
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"message\""));
        assertTrue(content.contains("Client 1234 Connected"));
    }

    @Test
    public void whenRoomIdNotExists_shouldReturnBadRequest() {
        when(mockClient.table(
                any(),
                eq(TableSchema.fromBean(ChatSession.class))))
                .thenReturn(mockTable);
        when(mockContext.getLogger()).thenReturn(mockLogger);

        Map<String, Object> input = new HashMap<>();
        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put("connectionId", "1234");
        input.put("requestContext", requestContext);
        input.put("body", "{\"action\":\"connectToRoom\"}");

        var connect = new Connect(mockClient);
        APIGatewayProxyResponseEvent result = connect.handleRequest(input, mockContext);
        assertEquals(400, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("roomId missing"));
    }
}