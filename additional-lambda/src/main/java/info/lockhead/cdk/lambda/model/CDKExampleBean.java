package info.lockhead.cdk.lambda.model;

import java.util.UUID;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/**
 * @author JohannesKoch
 *
 */
@DynamoDbBean
public class CDKExampleBean {
	private String cdkId;
	private String cdkCategory;

	public CDKExampleBean()
	{
		cdkId = UUID.randomUUID().toString();
	}
	public CDKExampleBean(UUID uuid, String string) {
		cdkId = uuid.toString();
		cdkCategory = string;
	}

	@DynamoDbPartitionKey
	public String getCdkId() {
		return cdkId;
	}

	public void setCdkId(String cdkId) {
		this.cdkId = cdkId;
	}

	@DynamoDbSecondaryPartitionKey(indexNames = "cdkCategory")
	public String getCdkCategory() {
		return cdkCategory;
	}

	public void setCdkCategory(String cdkCategory) {
		this.cdkCategory = cdkCategory;
	}

}
