package models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class ChatSession {

    private String ConnectionId;
    private String RoomId;

    public String getConnectionId() {
        return ConnectionId;
    }

    @DynamoDbPartitionKey
    public void setConnectionId(String connectionId) {
        ConnectionId = connectionId;
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }
}
