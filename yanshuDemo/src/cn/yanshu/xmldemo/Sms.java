package cn.yanshu.xmldemo;

import java.net.URL;
import java.net.URLConnection;

import android.net.Uri;

public class Sms {
	
	
	
	public Sms(String name, String content, String id) {
		super();
		this.name = name;
		this.content = content;
		this.id = id;
	}
	public Sms() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Sms(String name, String content) {
		super();
		this.name = name;
		this.content = content;
	}


	private  String name;
	private  String content;
	private   String id;
	
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
