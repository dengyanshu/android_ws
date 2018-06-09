package com.zowee.mes.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description Mes的webservice类
 */
public class MesWebService extends WebService
{

	private static  Soap soap;
	private static final String TAG = "Model";
	private static final String USERTICKET = "UserTicket";

	static
	{
		//soap = MesSoapParser.getSoap();
		soap = MesSoapParser.getOfficalSoap();
	}

	public static void setSoap(Soap finalSoap)
	{
		soap = finalSoap;
	}

	/**
	 * @author Administrator
	 * @description WEBService数据的相关解析类
	 */

	public static class WsDataParser
	{
		/*
		 * 获取WS返回的数据中的有效数据
		 */
		private static String getUsefulData(String repStr)
		{
			if(StringUtils.isEmpty(repStr))
				return null;
			String sign_1 = "diffgram=anyType{";
			String sign_2 = "\\{|\\}";
			if(repStr.indexOf(sign_1)==-1)
				return null;
			repStr = repStr.substring(repStr.indexOf(sign_1));
			List<int[]> matches = StringUtils.getMatStrIndex(sign_2,repStr);
			if(null!=matches&&matches.size()>2)
			{
				repStr = repStr.substring(0, matches.get(matches.size()-2)[0]);
				return repStr;
			}

			return null;
		}

		/*
		 * 获取有效数据中的数据部分
		 */
		public static String getUseDataForDataPart(String repStr)
		{
			String resDataStr = getResDataStr(repStr);
			if(null==resDataStr)
				return null;
			resDataStr = resDataStr.substring(resDataStr.indexOf("{")+1, resDataStr.lastIndexOf("}"));//裁取有效数据的数据部分   

			return resDataStr;
		}

		/*
		 * 解析WS返回的字符串 获取包含数据结果集的字符串
		 */
		public static String getResDataStr(String repStr)
		{
			String usefulData = getUsefulData(repStr);
			String sign_3 = "SQLDataSet";
			//Log.i(TAG, usefulData+"");
			if(null==usefulData||-1==usefulData.indexOf(sign_3))
				return null;
			int start = usefulData.indexOf("{");
			int end = usefulData.lastIndexOf("}");
			usefulData = usefulData.substring(start+1, end);

			return usefulData;
		}

		/*
		 * 对数据结果集进行解析 获取其中包含的数据 本方法只适应于一般情况下的数据结果集获取
		 */
		public static List<String> getResDatSet(String repStr)
		{
			//			String resDataStr = getResDataStr(repStr);
			//			if(null==resDataStr)
			//				return null;
			//			resDataStr = resDataStr.substring(resDataStr.indexOf("{")+1, resDataStr.lastIndexOf("}"));//裁取有效数据的数据部分   
			String resDataStr = getUseDataForDataPart(repStr);
			if(StringUtils.isEmpty(resDataStr)) {
				Log.d(TAG, "StringUtils.isEmpty(resDataStr)");
				return null;
			}
			//String regex_1 = "SQLDataSet=anyType\\{\\};?|anyType\\{\\};?";
			String regex_1 = "anyType\\{\\}";
			
			resDataStr = resDataStr.replaceAll(regex_1, "");
			resDataStr = resDataStr.replaceAll("HideSN= ","HideSN= ;");
			resDataStr = resDataStr.replaceAll("RepalceProductName= ","RepalceProductName= ;");

			String sign_4 = "SQLDataSet=anyType\\{|SQLDataSet\\d+=anyType\\{";
			Log.i("UtilTest", resDataStr);
			List<String> resDataSet = null;
			List<int[]> matchResData = StringUtils.getMatStrIndex(sign_4, resDataStr);
			if(null==matchResData||0==matchResData.size()) {
				Log.d(TAG, "null==matchResData||0==matchResData.size()");
				return null;
			}
			resDataSet = new ArrayList<String>(matchResData.size());
			Log.i(TAG, ""+matchResData.size());
			for(int i=0;i<matchResData.size();i++)
			{	
				int start = matchResData.get(i)[1];
				int end = 0;
				String temp = "";
				if(i==matchResData.size()-1)
				{
					temp = resDataStr.substring(start);
				}
				else
				{
					end = matchResData.get(i+1)[0];
					temp = resDataStr.substring(start, end);
				}
				//	Log.i("UtilTest", "start: "+start +" end: "+end);
				//				if(start==end)
				//					resDataSet.add("无结果数据");
				temp = temp.substring(0,temp.lastIndexOf("}"));

				resDataSet.add(temp);
			}
			//	Log.i(TAG, "NI HAO");

			return resDataSet;
		}

	}

	//	/**
	//	 * @return
	//	 * @description 对最终的数据进行处理 并以HashMap形式返回结果集
	//	 */
	//	public static List<HashMap<String,String>> getResMapsLis(String[] keys,List<HashMap<String,String>> datas)
	//	{
	//		List<HashMap<String,String>> resMapsLis = new ArrayList<HashMap<String,String>>();
	//		if(null==keys||0==keys.length||null==datas||0==datas.size())
	//			return null;
	//		for(int i=0;i<datas.size();i++)
	//		{}
	//		
	//		
	//	}

	/*
	 * 获取专门针对MES系统的空Soap
	 */
	public static Soap getMesEmptySoap()
	{
		Soap emptySoap = new Soap(soap.getWsNameSpa(),soap.getWsMethod(),soap.getWsWsdl(),soap.getSoapEdi());
		emptySoap.setDotNet(soap.isDotNet());
		if(null!=soap.getWsProperty().get(USERTICKET))
			emptySoap.addWsProperty(USERTICKET,soap.getWsProperty().get(USERTICKET));

		return emptySoap;
	}

	public static List<HashMap<String, String>> getResMapsLis(
			List<String> resDataSet) {
		
		if (null == resDataSet || 0 == resDataSet.size())
			return null;
		
		List<HashMap<String, String>> resArrs = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < resDataSet.size(); i++) {
			String regex = "=|;";
			// 8月28日修改 做天津过站动作时 {I_ReturnValue=1; I_ReturnMessage=OK;COMMAND TYPE IS 10; }
			List<int[]> indes = StringUtils.getMatStrIndex(regex, resDataSet
					.get(i).toString());
			
			if (null != indes && 0 != indes.size()) {
				HashMap<String, String> resItem = new HashMap<String, String>();
				String key = "";
				String value = "";
				for (int j = 0; j < indes.size();) {
					if (0 == j)
						key = resDataSet.get(i).substring(0, indes.get(j)[0])
								.trim();
					else
						key = resDataSet
								.get(i)
								.substring(indes.get(j - 1)[1], indes.get(j)[0])
								.trim();
					value = resDataSet.get(i)
							.substring(indes.get(j)[1], indes.get(j + 1)[0])
							.trim();
					resItem.put(key, value);
					j = j + 2;
				}
				resArrs.add(resItem);
			}
		}

		return resArrs;
	}
	
	public static HashMap<String,String> GetHwTmisreturndata(String resDataSet)
	{
		//Log.i(TAG, "COME IN4");

		if(null==resDataSet||0==resDataSet.length())
			return null;
		//Log.i(TAG, "COME IN5");
		HashMap<String,String> result = new  HashMap<String,String>();
		resDataSet = resDataSet.substring(resDataSet.indexOf("{")+1, resDataSet.lastIndexOf("}"));//裁取有效数据的数据部分
		String regex = "=|;";
 
		//	Log.i(TAG, "COME IN5");
		while( 2 < resDataSet.length())
		{
			String firtstring = resDataSet.substring(0,resDataSet.indexOf(";")+1);
			resDataSet =  resDataSet.substring(resDataSet.indexOf(";")+1,resDataSet.length());
			String key = "";
			String value = "";
			
			key = firtstring.substring(0,firtstring.indexOf("=")).trim();
			value = firtstring.substring(firtstring.indexOf("=")+1,firtstring.indexOf(";")).trim();
			result.put(key, value);
		 }
		return result;
	}





}
