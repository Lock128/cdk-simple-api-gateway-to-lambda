package info.lockhead.cdk.apigateway;

import software.constructs.Construct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.apigateway.Integration;
import software.amazon.awscdk.services.apigateway.IntegrationResponse;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.MethodResponse;
import software.amazon.awscdk.services.apigateway.MockIntegration;
import software.amazon.awscdk.services.apigateway.PassthroughBehavior;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.GlobalSecondaryIndexProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.ILayerVersion;
import software.amazon.awscdk.services.lambda.LayerVersion;
import software.amazon.awscdk.services.lambda.Runtime;

public class CdkSimpleApiGatewayToLambdaStack extends Stack {
	public CdkSimpleApiGatewayToLambdaStack(final Construct scope, final String id) {
		this(scope, id, null);
	}

	public CdkSimpleApiGatewayToLambdaStack(final Construct scope, final String id, final StackProps props) {
		super(scope, id, props);

		RestApi api = RestApi.Builder.create(this, "Example-API").restApiName("Example CDK provided endpoint")
				.description("This service holds all of the APIs").build();

		Map<String, String> lambdaEnvMap = new HashMap<>();
		lambdaEnvMap.put("PRIMARY_KEY", "itemId");
		String sourcePath = System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput")
				+ "/example-cdk-lambda-0.0.1-SNAPSHOT.jar";
		if (System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput") == null
				|| System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput").isEmpty()) {
			sourcePath = "C:\\Users\\JohannesKoch\\git-private\\cdk-simple-api-gateway-to-lambda\\lambda\\build\\libs\\example-cdk-lambda-0.0.1-SNAPSHOT.jar";
		}

		Function accountFunction = new Function(this, "ExampleLambdaFunction",
				getLambdaFunctionProps(sourcePath, lambdaEnvMap, "info.lockhead.cdk.lambda.ExampleLambdaFunction"));
		@Nullable
		IRole role = accountFunction.getRole();
		role.addToPrincipalPolicy(PolicyStatement.Builder.create().effect(Effect.ALLOW).resources(Arrays.asList("*"))
				.actions(Arrays.asList("ssm:DescribeParameters")).build());

		IResource items = api.getRoot().addResource("account");

		Integration getAllIntegration = new LambdaIntegration(accountFunction);
		items.addMethod("GET", getAllIntegration);

		addCorsOptions(items);
		
		Map<String, String> lambdaEnvMap2 = new HashMap<>();
		lambdaEnvMap2.put("PRIMARY_KEY", "itemId");
		String additionalSourcePath = System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput")
				+ "/example-cdk-additionallambda-0.0.1-SNAPSHOT.jar";
		if (System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput") == null
				|| System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput").isEmpty()) {
			additionalSourcePath = "C:\\Users\\JohannesKoch\\git-private\\cdk-simple-api-gateway-to-lambda\\additional-lambda\\build\\libs\\example-cdk-additionallambda-0.0.1-SNAPSHOT.jar";
		}
		
		
		@NotNull
		String layerSourcePath =  System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput")
				+ "/example-cdk-additionallambda-0.0.1-SNAPSHOT.jar";
				if (System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput") == null
						|| System.getenv("CODEBUILD_SRC_DIR_LambdaBuildOutput").isEmpty()) {
					layerSourcePath = "C:\\Users\\JohannesKoch\\git-private\\cdk-simple-api-gateway-to-lambda\\layer\\build\\libs\\layer-0.0.1-SNAPSHOT.jar";
				}
		
		LayerVersion layerVersion = LayerVersion.Builder.create(this, "LambdaLayer").layerVersionName("LambdaLayerExample").compatibleRuntimes(List.of(Runtime.JAVA_11)).code(Code.fromAsset(layerSourcePath)).build();
		
		
		Function additionalAccountFunction = new Function(this, "AdditionalExampleLambdaFunction",
				getLambdaFunctionProps(additionalSourcePath, lambdaEnvMap2, "info.lockhead.cdk.lambda.AdditionalExampleLambdaFunction"));
		additionalAccountFunction.addLayers(layerVersion);
		@Nullable
		IRole additionalRole = additionalAccountFunction.getRole();
		additionalRole.addToPrincipalPolicy(PolicyStatement.Builder.create().effect(Effect.ALLOW).resources(Arrays.asList("*"))
				.actions(Arrays.asList("ssm:DescribeParameters")).build());
		
		IResource additionalItems = api.getRoot().addResource("additional");
		
		Integration getAllAdditionalIntegration = new LambdaIntegration(additionalAccountFunction);
		additionalItems.addMethod("GET", getAllAdditionalIntegration);
		
		addCorsOptions(additionalItems);
		
		Table initCdkExampleTable = initCdkExampleTable();
		initCdkExampleTable.grantReadWriteData(additionalAccountFunction);
	}
	
	private Table initCdkExampleTable() {
		TableProps tableProps2;
		Attribute partitionKey = Attribute.builder().name("cdkId").type(AttributeType.STRING).build();
		Attribute seconfPartitionKey = Attribute.builder().name("cdkCategory").type(AttributeType.STRING).build();
		tableProps2 = TableProps.builder().tableName("CDK_EXAMPLE_TABLE").partitionKey(partitionKey)
				.removalPolicy(RemovalPolicy.DESTROY).billingMode(BillingMode.PAY_PER_REQUEST).build();
		Table researchTable = new Table(this, "CDK_EXAMPLE_TABLE", tableProps2);
		researchTable.addGlobalSecondaryIndex(
				GlobalSecondaryIndexProps.builder().indexName("cdkCategory").partitionKey(seconfPartitionKey).build());
		return researchTable;
	}
	

	private FunctionProps getLambdaFunctionProps(String sourcePath, Map<String, String> lambdaEnvMap, String handler) {
		// adding layers enables us to build up dependencies
		List<? extends ILayerVersion> layersList = new ArrayList<ILayerVersion>();

		return FunctionProps.builder().code(Code.fromAsset(sourcePath)).handler(handler).runtime(Runtime.JAVA_11)
				.environment(lambdaEnvMap).timeout(Duration.seconds(900)).memorySize(128).layers(layersList).build();
	}

	private void addCorsOptions(IResource item) {
		List<MethodResponse> methoedResponses = new ArrayList<>();

		Map<String, Boolean> responseParameters = new HashMap<>();
		responseParameters.put("method.response.header.Access-Control-Allow-Headers", Boolean.TRUE);
		responseParameters.put("method.response.header.Access-Control-Allow-Methods", Boolean.TRUE);
		responseParameters.put("method.response.header.Access-Control-Allow-Credentials", Boolean.TRUE);
		responseParameters.put("method.response.header.Access-Control-Allow-Origin", Boolean.TRUE);
		methoedResponses.add(MethodResponse.builder().responseParameters(responseParameters).statusCode("200").build());
		MethodOptions methodOptions = MethodOptions.builder().methodResponses(methoedResponses).build();

		Map<String, String> requestTemplate = new HashMap<>();
		requestTemplate.put("application/json", "{\"statusCode\": 200}");
		List<IntegrationResponse> integrationResponses = new ArrayList<>();

		Map<String, String> integrationResponseParameters = new HashMap<>();
		integrationResponseParameters.put("method.response.header.Access-Control-Allow-Headers",
				"'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent'");
		integrationResponseParameters.put("method.response.header.Access-Control-Allow-Origin", "'*'");
		integrationResponseParameters.put("method.response.header.Access-Control-Allow-Credentials", "'false'");
		integrationResponseParameters.put("method.response.header.Access-Control-Allow-Methods",
				"'OPTIONS,GET,PUT,POST,DELETE'");
		integrationResponses.add(IntegrationResponse.builder().responseParameters(integrationResponseParameters)
				.statusCode("200").build());
		Integration methodIntegration = MockIntegration.Builder.create().integrationResponses(integrationResponses)
				.passthroughBehavior(PassthroughBehavior.NEVER).requestTemplates(requestTemplate).build();

		item.addMethod("OPTIONS", methodIntegration, methodOptions);
	}
}
