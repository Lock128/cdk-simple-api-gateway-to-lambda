package info.lockhead.cdk.lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import info.lockhead.cdk.layer.BoardParameters;
import info.lockhead.cdk.layer.GatewayResponse;

public class AdditionalExampleLambdaFunction implements RequestHandler<BoardParameters, GatewayResponse> {

	private LambdaLogger logger;

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
		return "yes it worked - AdditionalExampleLambdaFunction";
	}
}
