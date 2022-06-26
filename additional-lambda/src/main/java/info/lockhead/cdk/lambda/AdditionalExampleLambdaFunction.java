package info.lockhead.cdk.lambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import info.lockhead.cdk.lambda.model.CDKExampleBean;
import info.lockhead.cdk.layer.BoardParameters;
import info.lockhead.cdk.layer.GatewayResponse;
import net.pegasusgalaxy.data.Construction;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest.Builder;

public class AdditionalExampleLambdaFunction implements RequestHandler<BoardParameters, GatewayResponse> {

	private LambdaLogger logger;
	private DynamoDbEnhancedClient ddb;

	public AdditionalExampleLambdaFunction() {
		ddb = DynamoDbEnhancedClient.create();
	}

	public GatewayResponse handleRequest(BoardParameters input, Context context) {
		logger = context.getLogger();
		logger.log("Inside net.pegasusgalaxy.lambda: AdditionalExampleLambdaFunction ");

		String output = getData(input);
		logger.log("output " + output);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		return new GatewayResponse(output, headers, 200);
	}

	private String getData(BoardParameters input) {
		List<CDKExampleBean> allActiveConstructions = getAllActiveConstructions();
		return "yes it worked - AdditionalExampleLambdaFunction - found examples: "+allActiveConstructions;
	}

	private List<CDKExampleBean> getAllActiveConstructions() {
		List<CDKExampleBean> constructions = new ArrayList<CDKExampleBean>();
		logger.log("Reading all CDKExampleBean...\n");

		DynamoDbTable<CDKExampleBean> dbTable = ddb.table("CDK_EXAMPLE_TABLE",
				TableSchema.fromBean(CDKExampleBean.class));

		try {
			SdkIterable<Page<CDKExampleBean>> scan = dbTable.scan();
			for (Page<CDKExampleBean> page : scan) {
				List<CDKExampleBean> items = page.items();
				for (CDKExampleBean construction : items) {
					logger.log("Read construction: " + construction);
					constructions.add(construction);
				}
			}
		} catch (AwsServiceException ase) {
			System.err.println("Could not complete operation");
			System.err.println("Error Message:  " + ase.getMessage());
			logger.log("Could not complete operation");
			logger.log("Error Message:  " + ase.getMessage());
		}

		logger.log("CDKExampleBean found: " + constructions.size() + "\n");
		if (constructions.size() == 0) {
			return createBeans();
		}
		return constructions;
	}

	private List<CDKExampleBean> createBeans() {
		List<CDKExampleBean> list = new ArrayList<CDKExampleBean>();
		list.add(createCDKBean(new CDKExampleBean(UUID.randomUUID(), "DevOps")));
		list.add(createCDKBean(new CDKExampleBean(UUID.randomUUID(), "awscdk")));
		list.add(createCDKBean(new CDKExampleBean(UUID.randomUUID(), "cdk.dev")));
		return list;
	}
	
	private CDKExampleBean createCDKBean(CDKExampleBean bean) {
		logger.log("Creating CDKExampleBean in dynamodb for user " + bean.getCdkId());
		DynamoDbTable<CDKExampleBean> researchTable = ddb.table("CDK_EXAMPLE_TABLE",
				TableSchema.fromBean(CDKExampleBean.class));

		try {
			researchTable.putItem(bean);
		} catch (Exception e) {
			logger.log("Error creating research!");
			e.printStackTrace();
		}
		
		logger.log("Created: "+bean.getCdkId()+" in dynamoDB");
		return bean;
	}
}
