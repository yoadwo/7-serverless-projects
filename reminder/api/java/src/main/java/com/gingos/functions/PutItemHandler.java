package com.gingos.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gingos.infra.DependencyFactory;
import com.gingos.models.ReminderDDB;
import com.gingos.models.ReminderReq;
import com.gingos.models.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class PutItemHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LogManager.getLogger(PutItemHandler.class);

    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<ReminderDDB> remindersTableSchema;
    private final DynamoDbTable<ReminderDDB> remindersTable;

    public PutItemHandler() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();
        remindersTableSchema = TableSchema.fromBean(ReminderDDB.class);
        remindersTable = dbClient.table(tableName, remindersTableSchema);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String body = requestEvent.getBody();
        var r = new Response();
        int statusCode;
        ReminderReq item;
        if (body != null && !body.isEmpty()) {
            LOG.info("read body: " + body);
            var reminderId = generateRandomWord();
            try {
                item = new ObjectMapper().readValue(body, ReminderReq.class);
                long ttl = convertExpireToTTL(item.getExpireOn());
                var reminderDDB = item.toReminderDDB(reminderId, ttl);
                LOG.info(String.format("creating reminderDDB with userId %s and reminderId %s", reminderDDB.getUserId(), reminderDDB.getReminderId()));

                if (reminderDDB.getUserId().equals(requestEvent.getPathParameters().get("userId"))) {
                    remindersTable.putItem(reminderDDB);
                    statusCode = HttpStatusCode.CREATED;
                    r.setStatus(Response.OK);
                    r.setData(item.toReminderRes());
                    LOG.info("Reminder saved to DB successfully");
                } else {
                    statusCode = HttpStatusCode.BAD_REQUEST;
                    r.setStatus(Response.ERROR);
                    r.setData("Inconsistent User ID in URL and body");
                }
            } catch (JsonProcessingException e) {
                LOG.error("failed parsing body", e);
                statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
                r.setStatus(Response.ERROR);
                r.setData("JSON error: " + e.getMessage());
            } catch (NullPointerException e) {
                LOG.error("One or more properties may be empty", e);
                statusCode = HttpStatusCode.BAD_REQUEST;
                r.setStatus(Response.ERROR);
                r.setData("One or more properties may be empty");
            } catch (ParseException e) {
                LOG.error("failed parsing expiry date", e);
                statusCode = HttpStatusCode.BAD_REQUEST;
                r.setStatus(Response.ERROR);
                r.setData("DateTime error: " + e.getMessage());
            } catch (DynamoDbException e) {
                LOG.error("dynamodb error", e);
                statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
                r.setStatus(Response.ERROR);
                r.setData("Error saving data to DB");
            }
        } else {
            statusCode = HttpStatusCode.BAD_REQUEST;
            r.setStatus(Response.ERROR);
            r.setData("Missing request body");
        }

        String response;
        try {
            response = r.Build();
        } catch (JsonProcessingException e) {
            response = r.BuildException(e.getMessage());
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(response)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"));
    }

    private String generateRandomWord() {
        String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private long convertExpireToTTL(String expireOn) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date expireDate = dateFormat.parse(expireOn);
        long ttlInSeconds = expireDate.getTime() / 1000;
        return ttlInSeconds;
    }
}
