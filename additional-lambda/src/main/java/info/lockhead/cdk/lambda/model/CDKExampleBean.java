package info.lockhead.cdk.lambda.model;

import java.util.UUID;

/**
 * @author JohannesKoch
 *
 */
public class CDKExampleBean {
	private String cdkId;
	private String cdkCategory;

	public CDKExampleBean(UUID uuid, String string) {
		cdkId = uuid.toString();
		cdkCategory = string;
	}

	public String getCdkId() {
		return cdkId;
	}

	public void setCdkId(String cdkId) {
		this.cdkId = cdkId;
	}

	public String getCdkCategory() {
		return cdkCategory;
	}

	public void setCdkCategory(String cdkCategory) {
		this.cdkCategory = cdkCategory;
	}

}
