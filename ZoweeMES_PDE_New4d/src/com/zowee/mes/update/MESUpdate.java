package com.zowee.mes.update;

import java.io.Serializable;

/**
 * @author Administrator
 * @description MES的更新配置文件
 */
public class MESUpdate implements Serializable
{
	private String appName;//应用名  有后缀
	private int appVersion;//应用的版本
	private String appUpdateURL;//更新应用的URL
	
	public MESUpdate(String appName, int appVersion, String appUpdateURL) 
	{
		super();
		this.appName = appName;
		this.appVersion = appVersion;
		this.appUpdateURL = appUpdateURL;
	}

	public MESUpdate()
	{
		super();
	}

	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppUpdateURL() {
		return appUpdateURL;
	}

	public void setAppUpdateURL(String appUpdateURL) {
		this.appUpdateURL = appUpdateURL;
	}
	
	
}
