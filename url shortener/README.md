# Serverless deployment
This folder contains two deployment methods, via `serverless framework` and via the `AWS SAM CLI`
Using `serverless framework` is easier and more feature-rich, but for the sake of learning the bare-bones `AWS SAM` is used as well.

## Serverless Framework

### Deployment

```
$ serverless deploy
```

After deploying, you should see output similar to:

```bash
Deploying url-shortener to stage dev (us-east-1)

✔ Service deployed to stack url-shortener-dev (152s)

endpoint: GET - https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/
functions:
  shrink: url-shortener-dev-shrink (1.9 kB)
  exapnd: url-shortener-dev-expand (1.9 kB)
```

_Note_: In current form, after deployment, your API is public and can be invoked by anyone. For production deployments, you might want to configure an authorizer. For details on how to do that, refer to [http event docs](https://www.serverless.com/framework/docs/providers/aws/events/apigateway/).


## AWS SAM

### Deployment

1. Make sure you create an S3 bucket to deploy the code into: `aws s3 mb s3://<unique-bucket-name> --region <region>`
2. Package to validate syntax and generate a proper cloud-formation yaml `aws cloudformation package --region <region> --s3-bucket <chosen-bucket-name> --template-file template.yaml --output-template-file gen/generated-template.yaml`
3. Publish the generated yaml: `aws cloudformation deploy --region <region> --template-file "<full-path>/gen/generated-template.yaml" --stack-name <name> --capabilities CAPABILITY_IAM`


## Invocation

After successful deployment, you can call the created application via HTTP:

```bash
curl https://xxxxxxx.execute-api.us-east-1.amazonaws.com/
```

Which should result in response similar to the following (removed `input` content for brevity):

```json
{
  "output": "https://xxxxxxx.execute-api.us-east-1.amazonaws.com/expand?code=yyy",
  "input": {
    ...
  }
}
```