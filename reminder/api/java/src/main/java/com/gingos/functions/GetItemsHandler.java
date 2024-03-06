package com.gingos.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gingos.infra.DependencyFactory;
import com.gingos.models.ReminderDDB;
import com.gingos.models.ReminderRes;
import com.gingos.models.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.http.HttpStatusCode;

import java.util.Collections;

public class GetItemsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LogManager.getLogger(GetItemsHandler.class);

    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<ReminderDDB> remindersTableSchema;

    public GetItemsHandler() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();
        remindersTableSchema = TableSchema.fromBean(ReminderDDB.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        var userId = requestEvent.getPathParameters().get("userId");

        DynamoDbTable<ReminderDDB> remindersTable = dbClient.table(tableName, remindersTableSchema);

        // 1. Define a QueryConditional instance to return items matching a partition value.
        QueryConditional keyEqual = QueryConditional.keyEqualTo(b -> b.partitionValue(userId));
        // 3. Build the query request.
        QueryEnhancedRequest tableQuery = QueryEnhancedRequest.builder()
                .queryConditional(keyEqual)
                .build();

        LOG.debug("Begin querying DB");
        PageIterable<ReminderDDB> pagedResults = remindersTable.query(tableQuery);
        var results = pagedResults
                .items().
                stream().
                map(item -> {
                    var reminder = new ReminderRes();
                    reminder.setMessage(item.getMessage());
                    reminder.setExpireOn(item.getExpireOn());
                    reminder.setUserId(item.getUserId());
                    return reminder;
                })
                .toArray();
        LOG.info("Querying DB successful, got " + results.length + " results");

        var r = new Response();
        String response;
        int statusCode;
        if (results.length == 0) { statusCode = HttpStatusCode.NO_CONTENT;}
        else { statusCode = HttpStatusCode.OK; }
        try {
            r.setData(results);
            r.setStatus(Response.OK);
            response = r.Build();
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize response", e);
            statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
            response = r.BuildException(e.getMessage());
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(response)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"));
    }
}
