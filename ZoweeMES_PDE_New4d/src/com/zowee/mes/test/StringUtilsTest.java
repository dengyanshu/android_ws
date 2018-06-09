package com.zowee.mes.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.test.AndroidTestCase;
import android.util.Log;

import com.zowee.mes.R;
import com.zowee.mes.update.MesUpdateParse;
import com.zowee.mes.utils.DateUtils;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesWebService;
/**
 * @author Administrator
 * @description 字符串辅助测试类
 */
public class StringUtilsTest extends AndroidTestCase 
{
	private static final String TAG = "UtilTest";
		
	public void testMatStrIndex()throws Exception
	{
		//String str = "07-27 06:40:47.889: I/WebServiceTest(532): GetDataSetWithSQLStringResponse{GetDataSetWithSQLStringResult=anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; }; }; }; element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{SQLDataSet=anyType{SQLDataSet=anyType{Column1=1F6D05DE-5D8; Column2=so-xxxxx1; }; SQLDataSet1=anyType{DNName=DNxxxx1; CustomerName=FR01; CustomerDescription=深圳市福瑞康电子有限公司; }; }; }; }; }";
//		String sign_1 = str.substring(str.indexOf("diffgram=anyType{"));
//		Log.i(TAG, sign_1);
//		String sign_2 = "SQLDataSet";
//		boolean isHasData = sign_1.indexOf(sign_2)!=-1;
//		if(isHasData)
//		String str = "SQLDataSet=anyType{Return_Value=-1; I_ReturnMessage=当前批号不存在,请检查!; }; SQLDataSet1=anyType{Column1=        -1; Column2=当前批号不存在,请检查!; }; ";
//		String sign = "SQLDataSet[0-9]?=anyType\\{";
//		List<String> resDataSet = null;
//		List<int[]> matchResData = StringUtils.getMatStrIndex(sign, str);
//		if(null==matchResData||0==matchResData.size())
//			return ;
//		resDataSet = new ArrayList<String>(matchResData.size());
//		for(int i=0;i<matchResData.size();i++)
//		{
//			int start = matchResData.get(i)[1];
//			int end = 0;
//			String temp = "";
//			if(i==matchResData.size()-1)
//			{                                 
//				temp = str.substring(start);         
//			}
//			else
//			{
//				end = matchResData.get(i+1)[0];
//				temp = str.substring(start, end);
//			}
//		//	Log.i("UtilTest", "start: "+start +" end: "+end);
////			if(start==end)
////				resDataSet.add("无结果数据");
//			temp = temp.substring(0,temp.lastIndexOf("}"));
//			
//			resDataSet.add(temp);
//		}
		
//		{
//			String regex = "\\{|\\}";
//			String useDateRegex = "SQLDataSet=anyType\\{";
//			List<int[]> matches = StringUtils.getMatStrIndex(regex, sign_1);
//			List<int[]> useDataMatches = StringUtils.getMatStrIndex(useDateRegex, sign_1);
//			Log.i(TAG, "有效数据个数为: "+useDataMatches.size());
//			if(null !=matches)
//			{
//				Log.i(TAG, "匹配的个数为：" +matches.size()+"\n");
//				Log.i(TAG, sign_1.substring(0, matches.get(matches.size()-2)[0]));String sign_4 = "SQLDataSet=anyType\\{";
//				matches.remove(matches.size()-1);
//				matches.remove(matches.size()-1);
//				
//				Log.i(TAG, "下标值:"+matches.get(matches.size()-1)[0]);
//				Log.i(TAG, "第一此裁剪："+sign_1.substring(matches.get(0)[1],matches.get(matches.size()-1)[0]-1));
//				Log.i(TAG, "下标值:"+matches.get(matches.size()-2)[0]);
//				Log.i(TAG, "第二次裁剪: "+sign_1.substring(matches.get(1)[1],matches.get(matches.size()-2)[0]-1));
//				sign_1 = sign_1.substring(matches.get(1)[1],matches.get(matches.size()-2)[0]-1);
//				useDataMatches = StringUtils.getMatStrIndex(useDateRegex, sign_1);
//				for(int i=0;i<useDataMatches.size();i++)
//				{
//					int start = useDataMatches.get(i)[1];
//					int end = 0;
//					if(i==useDataMatches.size()-1)
//					{
//						Log.i(TAG,"最后的有效的数据为:"+sign_1.substring(start));
//					}
//					else
//					{
//						end = useDataMatches.get(i+1)[0];
//						Log.i(TAG,"有效的数据为:"+sign_1.substring(start,end));
//					}
//				}
////				for(int i=0;i<matches.size();i++)
////				{
////					Log.i(TAG, "START:"+matches.get(i)[0]);
////					Log.i(TAG, "END:"+matches.get(i)[1]+"\n");
////				}
//			}
//			
//		}
//			String str = "GetDataSetWithSQLStringResponse{GetDataSetWithSQLStringResult=anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; }; }; }; };"
//					+"diffgram=anyType{SQLDataSet=anyType{SQLDataSet=anyType{BoxSN=条码有误,请检查!;LotSN=anyType{}; 库位	=anyType{};}; }; };}; }";
			//str = WebService.WsDataParser.getUsefulData(str);
		//	List<String>  resDataSet = WebService.WsDataParser.getResDatSet(str);
			
//			if(null!=resDataSet)
//			{
//				for(int i=0;i<resDataSet.size();i++)
//				{
//					Log.i(TAG, "第 "+i+"个数据 ："+resDataSet.get(i));
//				}
//			}
		
//			List<String> wsDataSet = MesWebService.WsDataParser.getResDatSet(str);
//			List<HashMap<String,String>> resMapsLis = MesWebService.getResMapsLis(resDataSet);
////			
////			
////			
//			for(int k=0;k<resMapsLis.size();k++)
//			{
//				HashMap<String,String> tempMap = resMapsLis.get(k);
//				for(Map.Entry<String, String> entry:tempMap.entrySet())
//				{
//					Log.i(TAG, "KEY: "+entry.getKey() +" VALUE: "+entry.getValue() );
//				}
//	
//			}
			//String str = "12345";
//		Date  date = new Date(System.currentTimeMillis());
//		String regex = "yyyy-MM-dd";
//		Log.i(TAG, DateUtils.conDateToStr(date, regex));
//		List<String> lis = MesWebService.WsDataParser.getResDatSet(str);
//		for(int i=0;i<lis.size();i++)
//		{
//			Log.i(TAG, lis.get(i));
//		}
//		
//		String str = "112.3543";
////		String str1 = "++112";
//		String str2 = "-112.393";
////		String str3 = "2.53";
////		String str6 = "-112.sk";
////		String str4 = "-skfs";
//		//String str5 = "112.2";
//		Log.i(TAG, ""+str.substring(0,str.length()));
       
      //  datetime = datetime.replaceAll("/", "-");
	//	String updateSource = MesUpdateParse.getLocalUpdateSource();
		//Log.i(TAG, ""+updateSource);
//		String[] setting = this.getContext().getResources().getStringArray(R.array.settings);
//		Log.i(TAG,setting[0]);
		
		//String datetime3 = "2011/08/11";
		//String datetime2 = "33-8f-11";
//		Log.i(TAG, datetime);
//		Log.i(TAG, ""+DateUtils.subDate(datetime1));
//		//Log.i(TAG, ""+DateUtils.subDate(datetime2));
//		Log.i(TAG, ""+DateUtils.subDate(datetime3));
      //(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		//Log.i(TAG, ""+Float.M);
		
//		Log.i(TAG, ""+StringUtils.conDeciStr(str,1));
//		Log.i(TAG, ""+StringUtils.conDeciStr(str2,9));
//		Log.i(TAG, ""+StringUtils.conDeciStr(str5,1));
//		Log.i(TAG, ""+StringUtils.isNumberal(str2));
//		Log.i(TAG, ""+StringUtils.isNumberal(str3));
//		Log.i(TAG, ""+StringUtils.isNumberal(str4));
//		//Log.i(TAG, ""+StringUtils.isNumberal(str5));
//		Log.i(TAG, ""+StringUtils.isNumberal(str6));
//		MesUpdateParse.mergeUserDefUpdateSource("");
//		Log.i(TAG, ""+MesUpdateParse.getUserDefUpdateSource());
		String regex = "^https?://(www.)?[\\S]+$";
		//Log.i(TAG, regex);
		String urlPath = "htt://ww.eoeandroid.com/thread-169344-1-1.html";
		Log.i(TAG, StringUtils.findStr(urlPath, regex)+"");
	}
	
	
	
	
	
}
