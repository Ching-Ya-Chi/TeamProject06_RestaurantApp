
public class DetailComplain {
	private int complainId;
	private int orderId;
	private String complainContent;
	private String replyContent;

	public DetailComplain() {

	}

	public DetailComplain(int complainId, int orderId, String complainContent, String replyContent) {
		this.complainId = complainId;
		this.orderId = orderId;
		this.complainContent = complainContent;
		this.replyContent = replyContent;
	}

	public int getComplainId() {
		return complainId;
	}

	public void setComplainId(int complainId) {
		this.complainId = complainId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getComplainContent() {
		return complainContent;
	}

	public void setComplainContent(String complainContent) {
		this.complainContent = complainContent;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String reply_content) {
		this.replyContent = reply_content;
	}
}
