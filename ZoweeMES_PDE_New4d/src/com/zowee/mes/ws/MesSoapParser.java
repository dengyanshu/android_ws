package com.zowee.mes.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.util.Log;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.utils.XMLParsers;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description ���ڽ���MES SOAP�����ļ� 
 */
public class MesSoapParser 
{

	private static final String WSNameSpace = "WsSpaceName";
	private static final String WSMETHOD = "WsMethod";
	private static final String WSWSDL = "WsWSDL";
	private static final String WSEDTION = "WsEdition";
	private static final String ISDOTNET = "DotNet";
	private static final String TAG = "WebServiceTest";
//	private static final String WSCONFIG_INPUTPATH = "com/yksj/mse/ws/wsconfig.properties";//ws���������ļ����صĵ�ַ 
	//private static final String WSCONFIG_OUTPUTPATH = "wsconfig.properties";//ws���������ļ����صĵ�ַ 
	private static final String WSCONFIG_LOCALFILE = "wsconfig.properties";//WSԭʼ�����ļ�
	private static final String WSCONFIGFILENAME = "wsconfig";//WS�û��Զ��������ļ�
	private static final String USERTICKET = "UserTicket";
	//private static final String SqlSentenceName = "";
	
	
	/**
	 * ���� wsconfig.properties ��ȡһ��ԭʼ��WS����Soap
	 */
	public static Soap getSoap()
	{
		Soap soap = null;
	//	InputStream inStream = MesSoapParser.class.getClassLoader().getResourceAsStream(WSCONFIG_INPUTPATH);
		AssetManager asManager = MyApplication.getApp().getAssets();
		InputStream inStream = null;
		Properties pro = new Properties();
		try 
		{
			inStream = asManager.open(WSCONFIG_LOCALFILE);
			if(null==inStream) return null;
			//Log.i(TAG,""+inStream);
			pro.load(inStream);
			String wsNameSpace= pro.getProperty(WSNameSpace);
			String wsMethod = pro.getProperty(WSMETHOD);
			String wsWSDL = pro.getProperty(WSWSDL);
			String tempWsEdition = pro.getProperty(WSEDTION);
			String tempIsDotNet = pro.getProperty(ISDOTNET);
		    String userTicket = pro.getProperty(USERTICKET);
		    Log.i(TAG,""+(null==userTicket)+""+("".equals(userTicket)));
		    
		    if(null==wsNameSpace) wsNameSpace = "";
			if(null==wsMethod) wsMethod = "";
			if(null==wsWSDL) wsWSDL = "";
			if(null==tempWsEdition) tempWsEdition = "110";
			int wsEdition = Integer.valueOf(tempWsEdition);
			if(null==tempIsDotNet) tempIsDotNet = "true";
			if(!"true".equals(tempIsDotNet)&&!"false".equals(tempIsDotNet))
				tempIsDotNet = "true";
			boolean isDotNet = Boolean.valueOf(tempIsDotNet);
			 Log.i(TAG, "getSoap(): "+(userTicket.equals(""))+"");
		    if(null==userTicket)
		    	userTicket = "";
			soap = new Soap(wsNameSpace, wsMethod, wsWSDL, wsEdition);
			soap.setDotNet(isDotNet);
			soap.addWsProperty(USERTICKET,userTicket);//���ʹ����ͨ��֤
		}
		catch (Exception e) 
		{
			Log.i(TAG, e.toString());
		}
		finally
		{
			try 
			{
				inStream.close();
			} 
			catch (IOException e) 
			{
				Log.i(TAG, e.toString());
			}
		}
		
		return soap;
	}
	
	
	
	/** 
	 * @description ��WebService�ļ������޸�
	 */
	public static void mergerSoap(Soap soap)
	{
		if(null==soap) return ;
		SharedPreferences preferences = MyApplication.getApp().getSharedPreferences(WSCONFIGFILENAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();//���ԭʼ������
		editor.putString(WSNameSpace,soap.getWsNameSpa());
		editor.putString(WSMETHOD,soap.getWsMethod());
		editor.putString(WSWSDL,soap.getWsWsdl()); 
		editor.putInt(WSEDTION,soap.getSoapEdi());
		editor.putBoolean(ISDOTNET,soap.isDotNet());
		if(null!=soap.getWsProperty().get(USERTICKET))
			editor.putString(USERTICKET, soap.getWsProperty().get(USERTICKET).toString());
		
		editor.commit();
		
	}
	
	/** 
	 * @description  ��ȡ�û�WS���õ�soap
	 */
	public static Soap getOfficalSoap()
	{
		SharedPreferences preferences = MyApplication.getApp().getSharedPreferences(WSCONFIGFILENAME, Context.MODE_PRIVATE);
		String wsNameSpace = preferences.getString(WSNameSpace,"");
		String wsWSDL = preferences.getString(WSWSDL, "");
		//String wsMethod = preferences.getString(WSMETHOD, "");
		if(StringUtils.isEmpty(wsNameSpace)&&StringUtils.isEmpty(wsWSDL))//�û���ԭʼ�ļ��ж�ȡ����������Ϣ ������д����Զ���������ļ���
		{
			Soap soap = getSoap();
			mergerSoap(soap);
		}
		wsNameSpace = preferences.getString(WSNameSpace,"");
		wsWSDL = preferences.getString(WSWSDL, "");
		String wsMethod = preferences.getString(WSMETHOD, "");
		int wsEdition = preferences.getInt(WSEDTION,0);
		boolean isDotnet = preferences.getBoolean(ISDOTNET, true);
        String userTicket = preferences.getString(USERTICKET, "");
        Soap finalSoap = new Soap(wsNameSpace, wsMethod, wsWSDL, wsEdition);
	    finalSoap.setDotNet(isDotnet);
	    Log.i(TAG,( userTicket.equals(""))+" f");
	    finalSoap.addWsProperty(USERTICKET, userTicket);
	   
	   return finalSoap;
	}
	
	
}
