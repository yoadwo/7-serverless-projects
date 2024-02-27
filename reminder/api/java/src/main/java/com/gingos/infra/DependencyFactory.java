package com.gingos.infra;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DependencyFactory {
    public static final String ENV_VARIABLE_TABLE = "TABLE_NAME";
    private static final Logger LOG = LogManager.getLogger(DependencyFactory.class);

    private DependencyFactory() {
    }

    /**
     * @return an instance of DynamoDbClient
     */

    public static DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        // .httpClientBuilder(UrlConnectionHttpClient.builder())
        DynamoDbClient ddbc = DynamoDbClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddbc)
                .build();
    }

    public static String tableName() {
        return System.getenv(ENV_VARIABLE_TABLE);
    }
}
