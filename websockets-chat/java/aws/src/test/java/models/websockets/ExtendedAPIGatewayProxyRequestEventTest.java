package models.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExtendedAPIGatewayProxyRequestEventTest {

    private final ObjectMapper objectMapper;
    private String EVENT = """
            {
              "headers": {
                "Host": "m932ywd2od.execute-api.us-east-1.amazonaws.com",
                "Sec-WebSocket-Extensions": "permessage-deflate; client_max_window_bits",
                "Sec-WebSocket-Key": "cDKFD/VKnt8EOTKkUILUgw==",
                "Sec-WebSocket-Version": "13",
                "X-Amzn-Trace-Id": "Root=1-67659810-101d2f353e7cd9797a9aa4ba",
                "X-Forwarded-For": "5.28.189.17",
                "X-Forwarded-Port": "443",
                "X-Forwarded-Proto": "https"
              },
              "multiValueHeaders": {
                "Host": [ "m932ywd2od.execute-api.us-east-1.amazonaws.com" ],
                "Sec-WebSocket-Extensions": [ "permessage-deflate; client_max_window_bits" ],
                "Sec-WebSocket-Key": [ "cDKFD/VKnt8EOTKkUILUgw==" ],
                "Sec-WebSocket-Version": [ "13" ],
                "X-Amzn-Trace-Id": [ "Root=1-67659810-101d2f353e7cd9797a9aa4ba" ],
                "X-Forwarded-For": [ "5.28.189.17" ],
                "X-Forwarded-Port": [ "443" ],
                "X-Forwarded-Proto": [ "https" ]
              },
              "requestContext": {
                "routeKey": "$connect",
                "eventType": "CONNECT",
                "extendedRequestId": "DGSykF8qIAMFljQ=",
                "requestTime": "20/Dec/2024:16:15:12 +0000",
                "messageDirection": "IN",
                "stage": "Prod",
                "connectedAt": 1734711312015,
                "requestTimeEpoch": 1734711312022,
                "identity": { "sourceIp": "5.28.189.17" },
                "requestId": "DGSykF8qIAMFljQ=",
                "domainName": "m932ywd2od.execute-api.us-east-1.amazonaws.com",
                "connectionId": "DGSykeXooAMCFPQ=",
                "apiId": "m932ywd2od"
              },
              "isBase64Encoded": false
            }
            """;

    private String CONTEXT = """
            {
                "routeKey": "$connect",
                "eventType": "CONNECT",
                "extendedRequestId": "DGSykF8qIAMFljQ=",
                "requestTime": "20/Dec/2024:16:15:12 +0000",
                "messageDirection": "IN",
                "stage": "Prod",
                "connectedAt": 1734711312015,
                "requestTimeEpoch": 1734711312022,
                "identity": { "sourceIp": "5.28.189.17" },
                "requestId": "DGSykF8qIAMFljQ=",
                "domainName": "m932ywd2od.execute-api.us-east-1.amazonaws.com",
                "connectionId": "DGSykeXooAMCFPQ=",
                "apiId": "m932ywd2od"
              }
            """;

    public ExtendedAPIGatewayProxyRequestEventTest() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSerializeExtendedGatewayRequestEvent() {
        // deserialize variable EVENT into instance of ExtendedApiGatewayProxyRequestEvent
        // using objectmapper and gson
        ExtendedAPIGatewayProxyRequestEvent event = null;
        try {
            event = objectMapper.readValue(EVENT, ExtendedAPIGatewayProxyRequestEvent.class);
        } catch (JsonProcessingException e) {
            // print stacktrace
            e.printStackTrace();
            fail(e.getMessage());
        }

        // You can now add assertions to verify the deserialized object
        assertNotNull(event);
        assertEquals("m932ywd2od.execute-api.us-east-1.amazonaws.com", event.getHeaders().get("Host"));
        assertEquals("cDKFD/VKnt8EOTKkUILUgw==", event.getHeaders().get("Sec-WebSocket-Key"));
        assertNotNull(event.getRequestContext());
        assertEquals("$connect", event.getRequestContext().getRouteKey());
        assertEquals("CONNECT", event.getRequestContext().getEventType());
    }

    @Test
    public void testSerializeExtendedContextRequest(){
        ExtendedProxyContextRequest event = null;
        try {
            event = objectMapper.readValue(CONTEXT, ExtendedProxyContextRequest.class);
        } catch (JsonProcessingException e) {
            // print stacktrace
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertNotNull(event);
        assertEquals("$connect", event.getRouteKey());
        assertEquals("CONNECT", event.getEventType());
        assertEquals("DGSykF8qIAMFljQ=", event.getRequestId());
    }
}