const { DynamoDBClient, GetItemCommand } = require("@aws-sdk/client-dynamodb"); // CommonJS import
const client = new DynamoDBClient();

const TableName = process.env.TABLE_NAME;

module.exports.handler = async (event) => {
  // get code from the url path
  const code = event.queryStringParameters.code;
  console.log("code", code);
  // get from dynamo by ID
  const input = {
    Key: {
      code: {
        S: code,
      },
    },
    TableName: TableName,
  };

  const command = new GetItemCommand(input);
  let response = undefined;
  let item = undefined;
  try {
    response = await client.send(command);
    console.log('response', response)
  } catch (e) {
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
  // return the original url
  item = response.Item;

  return {
    statusCode: 301, // or 308 for permanent redirect
    headers: {
      Location: "https://" + item.originalUrl.S,
    },
    body: "", // Empty body for redirects
  };
};
