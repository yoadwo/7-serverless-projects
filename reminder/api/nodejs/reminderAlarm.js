const { SendEmailCommand } = require("@aws-sdk/client-ses");
const { sesClient } = requre("./libs/sesClient.js");
//const { DynamoDBClient, QueryCommand } = require("@aws-sdk/client-dynamodb"); // CommonJS import
//const { unmarshall } = require("@aws-sdk/util-dynamodb");
//const { unmarshall } = require("@aws-sdk/util-dynamodb");

//const SenderEmail = process.env.

module.exports.handler = async (event) => {  
    console.log('dynamodb event', event);
    // const sendEmailCommand = createSendEmailCommand(
    //     "recipient@example.com",
    //     "sender@example.com",
    //   );
    
    // await sesClient.send(sendEmailCommand);

    const response = {
        statusCode: 200,
        body: JSON.stringify({
          message: 'Mail sent successfully'
        }),
      };

    return response;
    
};

const createSendEmailCommand = (toAddress, fromAddress) => {
    return new SendEmailCommand({
      Destination: {
        /* required */
        CcAddresses: [
          /* more items */
        ],
        ToAddresses: [
          toAddress,
          /* more To-email addresses */
        ],
      },
      Message: {
        /* required */
        Body: {
          /* required */
          Html: {
            Charset: "UTF-8",
            Data: "HTML_FORMAT_BODY",
          },
          Text: {
            Charset: "UTF-8",
            Data: "TEXT_FORMAT_BODY",
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: "EMAIL_SUBJECT",
        },
      },
      Source: fromAddress,
      ReplyToAddresses: [
        /* more items */
      ],
    });
  };
