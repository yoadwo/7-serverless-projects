# websockets-handlers

## Amazon Web Services (AWS) resources

This project contains source code and supporting files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders.
- src/main/handlers - Code for the application's Lambda functions.
- events - Invocation events that you can use to invoke the function.
- template.yaml - A template that defines the application's AWS resources.

A simple Angular client is also provided, for easier invokation of the functions.


### Deployment

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. 
It uses Docker to run your functions in an Amazon Linux environment that matches Lambda.

To use the [SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html), you'll need the java SDK, maven CLI and docker installed.

To build and deploy your application for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```

You can find your API Gateway Endpoint URL in the output values displayed after deployment.

For further deployments with the same cloudformation stack, `sam deploy` (without `--guided` flag) should suffice.

### Invocation

#### Test locally

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. 
Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
sam local invoke OnConnectFunction --event events/ws-connect.json
```

#### With WebSockets client

1. From the browser
	1. from the client directory, query your stack for the websockets URI and save to file: `aws cloudformation describe-stacks --stack-name <stack-name> --query "Stacks[0].Outputs[?OutputKey=='WebSocketURI'].OutputValue" --output text > websockets-server.txt` 
	2. open the `index.html` file with your browser (has a fetch call to _websockets-server.txt_)
2. Install _wscat_ CLI and call: `wscat -c wss://xxxxxxx.execute-api.us-east-1.amazonaws.com/Prod/`, followed by
	```
	> {"action": "connectToRoom", "roomId": "room"}
	> {"action": "sendMessage", "message": "hello world"}
	``` 