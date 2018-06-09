package com.zowee.mes.update;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.utils.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class MesUpdateParse {
	private static final String VERSION = "version";
	private static final String APPNAME = "appname";
	private static final String APPURL = "appurl";
	private static final String TAG = "MesUpdateParse";
	// private static final String UPDATESOURCE_LOCAL =
	// "com/yksj/mse/update/update_config.properties";
	private static final String UPDATESOURCE = "updateSource";
	private static final String LOCALUPDATESOURCEFILE = "update_config.properties";
	private static final String USERDEFUPDATESOURCEFILE = "update_config";// 用户自定义的更新配置文件

	public static MESUpdate getParseMesUpdateObj(InputStream instream) {
		MESUpdate mesUpdate = null;
		try {
			if (null == instream || 0 == instream.available())
				return null;
			DocumentBuilderFactory builderFactory = XMLParser.getDocBuiFac();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(instream);
			Element rootEle = document.getDocumentElement();
			NodeList nodeList = rootEle.getChildNodes();
			mesUpdate = new MESUpdate();
			System.out.println(rootEle.getNodeName() + "  "
					+ nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node tempNode = nodeList.item(i);
				if (Node.ELEMENT_NODE == tempNode.getNodeType()) {
					Element ele = (Element) tempNode;
					if (ele.getNodeName().equals(APPNAME)
							&& null != ele.getFirstChild())
						mesUpdate
								.setAppName(ele.getFirstChild().getNodeValue());
					if (ele.getNodeName().equals(VERSION))
						mesUpdate.setAppVersion(Integer.valueOf(ele
								.getFirstChild().getNodeValue()));
					if (ele.getNodeName().equals(APPURL))
						mesUpdate.setAppUpdateURL(ele.getFirstChild()
								.getNodeValue());
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		} finally {
			try {
				if (null != instream)
					instream.close();
			} catch (IOException e) {
				Log.i(TAG, e.toString());
			}
		}

		return mesUpdate;
	}

	public static String getLocalUpdateSource() {
		InputStream inStream = null;
		String updateSource = null;
		try {
			// inStream =
			// MesUpdateParse.class.getClassLoader().getResourceAsStream(UPDATESOURCE_LOCAL);
			inStream = MyApplication.getApp().getAssets()
					.open(LOCALUPDATESOURCEFILE);
			if (null == inStream || 0 == inStream.available())
				return null;
			Properties pro = new Properties();
			pro.load(inStream);
			updateSource = pro.getProperty(UPDATESOURCE);
		} catch (IOException e) {
			Log.i(TAG, e.toString());
		} finally {
			try {
				if (null != inStream)
					inStream.close();
			} catch (IOException e) {
				Log.i(TAG, e.toString());
			}

		}

		return updateSource;
	}

	/**
	 * 获取更新源
	 */
	public static String getUserDefUpdateSource() {
		/*
		 * SharedPreferences sharedPreferences =
		 * MyApplication.getApp().getSharedPreferences(USERDEFUPDATESOURCEFILE,
		 * Context.MODE_PRIVATE); String updateSource =
		 * sharedPreferences.getString(UPDATESOURCE, "");
		 * if(StringUtils.isEmpty(updateSource)) { updateSource =
		 * getLocalUpdateSource();// if(StringUtils.isEmpty(updateSource))
		 * updateSource = ""; Editor editor = sharedPreferences.edit();
		 * editor.putString(UPDATESOURCE,updateSource); editor.commit(); }
		 */// changed by ybj 20130913
		String updateSource = getLocalUpdateSource();
		return updateSource;
	}

	/**
	 * 
	 * @description 对更新源进行修改
	 */
	public static void mergeUserDefUpdateSource(String updateSource) {
		if (StringUtils.isEmpty(updateSource))
			updateSource = "";
		SharedPreferences sharedPreferences = MyApplication.getApp()
				.getSharedPreferences(USERDEFUPDATESOURCEFILE,
						Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(UPDATESOURCE, updateSource);
		editor.commit();

	}

}
