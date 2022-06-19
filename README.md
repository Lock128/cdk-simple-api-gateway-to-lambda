# Welcome to your CDK Java project - to show APIGateway + Lambda Integration

This is a simply project for CDK development with Java without meaningful results.

The `cdk\cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Gradle](https://www.gradle.org/) based project, so you can open this project with any Gradle compatible Java IDE to build and run tests.

## Useful commands

 * `gradlew customFatJar` prepare lambda build
 * within the "cdk" folder:
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!

## This has been tested on a Windows machine - using the following steps:

* Run `gradlew customFatJar` in main directory
* run `cdk deploy` in "cdk" folder (you might need to adjust the path in cdk/src/main/../CdkSimpleApiGatewayToLambdaStack.java)