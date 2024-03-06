package com.gingos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class ReminderReq {

    public static final String PROJECTTYPE = "PROJECTTYPE";

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("projectType")
    private String projectType = System.getenv(PROJECTTYPE);

    private String message;

    private String expireOn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
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

    public ReminderDDB toReminderDDB(String reminderId, Long ttl){
        var reminderDDB = new ReminderDDB();

        reminderDDB.setReminderId(reminderId);
        reminderDDB.setUserId(this.userId);
        reminderDDB.setMessage(this.message);
        reminderDDB.setExpireOn(this.expireOn);
        reminderDDB.setTTL(ttl);
        reminderDDB.setProjectType(this.projectType);

        return reminderDDB;
    }

    public ReminderRes toReminderRes() {
        var reminderRes = new ReminderRes();
        reminderRes.setUserId(this.userId);
        reminderRes.setMessage(this.message);
        reminderRes.setExpireOn(this.expireOn);

        return reminderRes;
    }
}
