package info.lockhead.cdk.layer;

public class BoardParameters {
	String title;
	String jql;
	int firstProdId;
	int secondProdId;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoardParameters [");
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (jql != null) {
			builder.append("jql=");
			builder.append(jql);
			builder.append(", ");
		}
		builder.append("firstProdId=");
		builder.append(firstProdId);
		builder.append(", secondProdId=");
		builder.append(secondProdId);
		builder.append("]");
		return builder.toString();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJql() {
		return jql;
	}

	public void setJql(String jql) {
		this.jql = jql;
	}

	public int getFirstProdId() {
		return firstProdId;
	}

	public void setFirstProdId(int firstProdId) {
		this.firstProdId = firstProdId;
	}

	public int getSecondProdId() {
		return secondProdId;
	}

	public void setSecondProdId(int secondProdId) {
		this.secondProdId = secondProdId;
	}
}
