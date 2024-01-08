const { DynamoDBClient, PutItemCommand } = require("@aws-sdk/client-dynamodb"); // CommonJS import
const client = new DynamoDBClient();
const TableName = process.env.TABLE_NAME

module.exports.handler = async (event, context) => {
  console.log('event', event);
  console.log('context', context);
  console.log('table name', TableName);
  // get their original url (body of the event)
  let { url } = JSON.parse(event.body);
  // generate a random 5 character code
  const code = (new Date().getMilliseconds() /1000 * Math.random()).toString(36).substring(2,7);
  // write to Dyanamo - {code: code, url: originalUrl }
  console.log('url', url, typeof url);
  console.log('code', code, typeof code);
  
  const input = {
    "Item": {
      "code": {
        "S": code
      },
      "originalUrl": {
        "S": url
      },
      "source": {
        "S": "cf-lambda"
      }
    },
    "ReturnConsumedCapacity": "TOTAL",
    "TableName": TableName
  };

  const command = new PutItemCommand(input);
  try {
    const response = await client.send(command);
    console.log('response', response);
  } catch (e){
    return {
      statusCode: 500,
      body: JSON.stringify(
        {
          error: e,
        },
        null,
        2
      ),
    }; 
  }
  
  const apiUrl = `${event.requestContext.domainName}/${event.requestContext.stage}/expand`;
  
  return {
    statusCode: 200,
    body: JSON.stringify(
      {
        output: `${apiUrl}/${code}`,
        input: {event, context},
      },
      null,
      2
    ),
  };
};
