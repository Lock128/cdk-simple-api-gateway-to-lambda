package info.lockhead.cdk.apigateway;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class CdkSimpleApiGatewayToLambdaApp {
    public static void main(final String[] args) {
        App app = new App();

        new CdkSimpleApiGatewayToLambdaStack(app, "CdkSimpleApiGatewayToLambdaStack", StackProps.builder()
                .build());

        app.synth();
    }
}

