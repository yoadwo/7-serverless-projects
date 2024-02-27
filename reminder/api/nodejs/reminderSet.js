const { DynamoDBClient, PutItemCommand } = require("@aws-sdk/client-dynamodb"); // CommonJS import
const client = new DynamoDBClient();
const TableName = process.env.TABLE_NAME

module.exports.handler = async (event) => {  
  let { userId, message, expireOn, projectType } = JSON.parse(event.body);
  let id = (new Date().getMilliseconds() /1000 * Math.random()).toString(36).substring(2,7);
  let ttl = new Date(expireOn).getTime()/1000;
  const input = {
    "Item": {
      "ReminderId": {
        "S": id
      },
      "UserId": {
        "S": userId
      },
      "ProjectType": {
        "S": projectType
      },
      "Message": {
        "S": message
      },
      "TTL": {
        "N": ttl.toString()
      },
      "ExpireOn": {
        "S": expireOn
      }
    },
    "ReturnConsumedCapacity": "TOTAL",
    "TableName": TableName
  };
  console.log('input', input);

  const command = new PutItemCommand(input);
  let response = undefined;
  
  try {
    response = await client.send(command);
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

  return {
    statusCode: 200,
    body: JSON.stringify(
      {
        message: "set executed successfully!",
        input: event,
        output: response
      },
      null,
      2
    ),
  };
};
