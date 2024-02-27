package com.gingos.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import com.fasterxml.jackson.annotation.JsonProperty;

@DynamoDbBean
public class ReminderDDB {

    private String UserId;

    private String ReminderId;

    private String projectType;

    private String message;

    private long TTL;

    //@JsonProperty("ExpireOn")
    private String expireOn;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("UserId")
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("ReminderId")
    public String getReminderId() {
        return ReminderId;
    }

    public void setReminderId(String ReminderId) {
        this.ReminderId = ReminderId;
    }

    @DynamoDbAttribute("ProjectType")
    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    @DynamoDbAttribute("Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDbAttribute("TTL")
    public long getTTL() {
        return TTL;
    }

    public void setTTL(long TTL) {
        this.TTL = TTL;
    }

    @DynamoDbAttribute("ExpireOn")
    public String getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(String expireOn) {
        this.expireOn = expireOn;
    }
}
