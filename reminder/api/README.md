# Amazon Web Services (AWS) deployment
This folder contains on this commit only the `serverless framework` method and a simple angular client.
Update: Started a new job working with Java, so switched working with it in here as well.

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
  set-slsfw: reminder-dev-setslsfw (1.9 kB)
  get-slsfw: reminder-dev-getslsfw (1.9 kB)
```

_Note_: In current form, after deployment, your API is public and can be invoked by anyone. For production deployments, you might want to configure an authorizer. For details on how to do that, refer to [http event docs](https://www.serverless.com/framework/docs/providers/aws/events/apigateway/).


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