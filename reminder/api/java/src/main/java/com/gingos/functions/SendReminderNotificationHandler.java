package com.gingos.functions;

import com.gingos.models.ReminderDDB;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class SendReminderNotificationHandler  implements RequestHandler<DynamodbEvent, Void> {

    private static final Logger LOG = LogManager.getLogger(SendReminderNotificationHandler.class);


    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        LOG.info("processing {} records", (long) dynamodbEvent.getRecords().size());

        var messages = dynamodbEvent.getRecords().stream()
                .filter(record -> record.getEventName().equals("REMOVE"))
                .map(record -> {
                    var userId = record.getDynamodb().getOldImage().get("UserId").getS();
                    var message = record.getDynamodb().getOldImage().get("Message").getS();
                    var reminder = new ReminderDDB();
                    reminder.setUserId(userId);
                    reminder.setMessage(message);
                    return reminder;
                })
                .toList();

        send(messages);
        return null;
    }

    private void send(List<ReminderDDB> messages) {

        Region region = Region.US_EAST_1;
        SesV2Client sesv2Client = SesV2Client.builder()
                .region(region)
                .build();

        var emailRequests = new ArrayList<SendEmailRequest>();

        // The HTML body of the email.
        String bodyHTMLTemplate = "<html><head></head><body><h1>REMINDER ALARM</h1>"
                + "<p>%s</p></body></html>";

        messages.forEach(message -> {
            Destination destination = Destination.builder()
                    .toAddresses(message.getUserId())
                    .build();

            Content content = Content.builder()
                    .data(String.format(bodyHTMLTemplate, message.getMessage()))
                    .build();

            Content sub = Content.builder()
                    .data("Reminder Alarm")
                    .build();

            Body body = Body.builder()
                    .html(content)
                    .build();

            Message msg = Message.builder()
                    .subject(sub)
                    .body(body)
                    .build();

            EmailContent emailContent = EmailContent.builder()
                    .simple(msg)
                    .build();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(destination)
                    .content(emailContent)
                    .fromEmailAddress(message.getUserId())
                    .build();

            emailRequests.add(emailRequest);
        });

        try {
            System.out.println("Attempting to send an email through Amazon SES "
                    + "using the AWS SDK for Java...");
            emailRequests.forEach(sesv2Client::sendEmail);
            System.out.println("email was sent");
            sesv2Client.close();

        } catch (SesV2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
