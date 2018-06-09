package cn.yanshu.smartimageview;

public class Sms {
	
	private  String name;
	private String content;
	private String headurl;
	public Sms(String name, String content, String headurl) {
		super();
		this.name = name;
		this.content = content;
		this.headurl = headurl;
	}
	
	public Sms() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHeadurl() {
		return headurl;
	}
	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}
	

}
