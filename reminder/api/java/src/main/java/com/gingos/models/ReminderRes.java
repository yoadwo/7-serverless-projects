package com.gingos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class ReminderRes {

    //@JsonProperty("ReminderId")
    //private String ReminderId;

    @JsonProperty("userId")
    private String UserId;

    private String message;

    private String expireOn;

//    public String getReminderId() {
//        return reminderId;
//    }
//
//    public void setReminderId(String reminderId) {
//        this.ReminderId = reminderId;
//    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(String expireOn) {
        this.expireOn = expireOn;
    }
}
