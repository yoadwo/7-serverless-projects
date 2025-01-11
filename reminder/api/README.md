# Amazon Web Services (AWS) deployment
This folder contains two deployment methods, via `serverless framework` and via the `AWS SAM CLI`.
Using `serverless framework` is easier and more feature-rich, but for the sake of learning the bare-bones `AWS SAM` is used as well.
A simple Angular client is also provided, for easier invokation of the functions.

**Performance Note**
For the provided configuration (`mvn install` and `template.yaml`) there is a major performance hit,
compared to the *serverless framework* method (3 seconds vs 15 seconds).
Since this project is done mostly for educational purposes, no major effor to solve the issue was made, 
except raising the lambda runtime limit (otherwise the lambda gets a timeout).

## Angular Client
1. Install using _npm install_
2. Run using _npx ng serve_

## Serverless Framework

### Deployment

```
$ serverless deploy
```

After deploying, you should see output similar to:

```bash
Deploying reminder to stage dev (us-east-1)

✔ Service deployed to stack reminder-dev (152s)

endpoint: GET - https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/
functions:
  putItem: reminder-dev-putItem (1.9 kB)
  getItems: reminder-dev-getItems (1.9 kB)
```

_Note_: In current form, after deployment, your API is public and can be invoked by anyone. For production deployments, you might want to configure an authorizer. For details on how to do that, refer to [http event docs](https://www.serverless.com/framework/docs/providers/aws/events/apigateway/).


## AWS SAM

### Deployment

1. Make sure you create an S3 bucket to deploy the code into: `aws s3 mb s3://7p-reminder-<UID> --region <region>`
2. Package to validate syntax and generate a proper cloud-formation yaml `aws cloudformation package --region <region> --s3-bucket <chosen-bucket-name> --template-file template.yaml --output-template-file generated-template.yaml`
3. Publish the generated yaml: `aws cloudformation deploy --region <region> --template-file "<full-path>/gen/generated-template.yaml" --stack-name reminder-cf --capabilities CAPABILITY_NAMED_IAM`

### Invocation

After successful deployment, you can call the created application via HTTP:

```bash
curl https://xxxxxxx.execute-api.us-east-1.amazonaws.com/dev/reminders/{userId}
```

Which should result in response similar to the following (removed `message` content for brevity):

```json
{
  "status": "OK",
  "message": {
    ...
  }
}
```