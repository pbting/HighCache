package audaque.com.pbting.cache.house;

public class MsgBody implements TempProtocol{

	private String msgkey ;
	
	private StringBuffer msgContent ;
	public MsgBody() {
	}
	public MsgBody(String msgkey, StringBuffer msgContent) {
		super();
		this.msgkey = msgkey;
		this.msgContent = msgContent;
	}
	public String getMsgkey() {
		return msgkey;
	}
	public void setMsgkey(String msgkey) {
		this.msgkey = msgkey;
	}
	public StringBuffer getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(StringBuffer msgContent) {
		this.msgContent = msgContent;
	}
}
