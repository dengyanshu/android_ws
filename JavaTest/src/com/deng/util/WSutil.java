package com.deng.util;

import java.util.ArrayList;
import java.util.List;




public class WSutil {
	
	
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
	
	public static List<String> getResDatSet(String repStr)
	{
		//			String resDataStr = getResDataStr(repStr);
		//			if(null==resDataStr)
		//				return null;
		//			resDataStr = resDataStr.substring(resDataStr.indexOf("{")+1, resDataStr.lastIndexOf("}"));//裁取有效数据的数据部分   
		String resDataStr = getUseDataForDataPart(repStr);
		if(StringUtils.isEmpty(resDataStr)) {
			//Log.d(TAG, "StringUtils.isEmpty(resDataStr)");
			return null;
		}
		//String regex_1 = "SQLDataSet=anyType\\{\\};?|anyType\\{\\};?";
		String regex_1 = "anyType\\{\\}";
		
		resDataStr = resDataStr.replaceAll(regex_1, "");
		resDataStr = resDataStr.replaceAll("HideSN= ","HideSN= ;");
		resDataStr = resDataStr.replaceAll("RepalceProductName= ","RepalceProductName= ;");

		String sign_4 = "SQLDataSet=anyType\\{|SQLDataSet\\d+=anyType\\{";
		//Log.i("UtilTest", resDataStr);
		List<String> resDataSet = null;
		List<int[]> matchResData = StringUtils.getMatStrIndex(sign_4, resDataStr);
		if(null==matchResData||0==matchResData.size()) {
			//Log.d(TAG, "null==matchResData||0==matchResData.size()");
			return null;
		}
		resDataSet = new ArrayList<String>(matchResData.size());
		//Log.i(TAG, ""+matchResData.size());
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
