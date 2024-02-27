const { DynamoDBClient, QueryCommand } = require("@aws-sdk/client-dynamodb"); // CommonJS import
const { unmarshall } = require("@aws-sdk/util-dynamodb");

const client = new DynamoDBClient();
const TableName = process.env.TABLE_NAME

module.exports.handler = async (event) => {
  console.log('event', event);
  let { userId } = event.queryStringParameters;
  console.log('userid', userId);

  const input = {
    "ExpressionAttributeValues": {
      ":v1": {
        "S": userId
      }
    },
    "KeyConditionExpression": "UserId = :v1",
    "ProjectionExpression": "ReminderId, Message, ExpireOn",
    "TableName": TableName
  };
  const command = new QueryCommand(input);
  let response = undefined;
  try {
    response = await client.send(command);
    console.log('response', response);
  } catch (e) {
    return {
      statusCode: 500,
      body: JSON.stringify(
        {
          error: e
        },
        null,
        2
      ),
    };
  }

  const items = response.Items.map( item => unmarshall(item));

  return {
    statusCode: 200,
    body: JSON.stringify(
      {
        message: items,
      },
      null,
      2
    ),
  };
};
