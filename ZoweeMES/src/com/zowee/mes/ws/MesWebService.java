package com.zowee.mes.ws;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description Mes��webservice��
 * ƽ����
 */
public class MesWebService extends WebService {

	private static Soap soap;
	private static final String TAG = "Model";
	private static final String USERTICKET = "UserTicket";

	static {
		// soap = MesSoapParser.getSoap();
		soap = MesSoapParser.getOfficalSoap();
//		/**************************************************************/
//		String wsdl=soap.getWsWsdl();
//		if(wsdl.contains("MES")){
//			String ip=getHostip("MES");
//			wsdl=wsdl.replace("MES", ip);
//			soap.setWsWsdl(wsdl);
//		}
//		/**************************************************************/
	}
//	/***************************************************************/
//	private static  String getHostip(String hostname){
//		     String IPAddress = "";    
//		    InetAddress ReturnStr1 = null;   
//		     try {   
//		        ReturnStr1 = java.net.InetAddress.getByName(hostname);   
//		        IPAddress = ReturnStr1.getHostAddress();   
//		    } catch (UnknownHostException e) {   
//		        // TODO Auto-generated catch block   
//		        e.printStackTrace();   
//		        return  IPAddress;   
//		     }   
//		 return IPAddress;  
//	}
//	/***************************************************************/

	public static void setSoap(Soap finalSoap) {
		soap = finalSoap;
	}
   
	/*
	 * ��ȡר�����MESϵͳ�Ŀ�Soap
	 */
	public static Soap getMesEmptySoap() {
		Soap emptySoap = new Soap(soap.getWsNameSpa(), soap.getWsMethod(),
				soap.getWsWsdl(), soap.getSoapEdi());
		emptySoap.setDotNet(soap.isDotNet());
		if (null != soap.getWsProperty().get(USERTICKET))
			emptySoap.addWsProperty(USERTICKET,
					soap.getWsProperty().get(USERTICKET));

		return emptySoap;
	}
	
	/**
	 * @author Administrator
	 * @description WEBService���ݵ���ؽ�����
	 */

	public static class WsDataParser {
		/*
		 * ��ȡWS���ص������е���Ч����
		 */
		private static String getUsefulData(String repStr) {
			if (StringUtils.isEmpty(repStr))
				return null;
			String sign_1 = "diffgram=anyType{";
			String sign_2 = "\\{|\\}";
			if (repStr.indexOf(sign_1) == -1)
				return null;
			repStr = repStr.substring(repStr.indexOf(sign_1));
			List<int[]> matches = StringUtils.getMatStrIndex(sign_2, repStr);
			if (null != matches && matches.size() > 2) {
				repStr = repStr
						.substring(0, matches.get(matches.size() - 2)[0]);
				return repStr;
			}

			return null;
		}

		/*
		 * ��ȡ��Ч�����е����ݲ���
		 */
		public static String getUseDataForDataPart(String repStr) {
			String resDataStr = getResDataStr(repStr);
			if (null == resDataStr)
				return null;
			resDataStr = resDataStr.substring(resDataStr.indexOf("{") + 1,
					resDataStr.lastIndexOf("}"));// ��ȡ��Ч���ݵ����ݲ���

			return resDataStr;
		}

		/*
		 * ����WS���ص��ַ��� ��ȡ�������ݽ�������ַ���
		 */
		public static String getResDataStr(String repStr) {
			String usefulData = getUsefulData(repStr);
			String sign_3 = "SQLDataSet";
			// Log.i(TAG, usefulData+"");
			if (null == usefulData || -1 == usefulData.indexOf(sign_3))
				return null;
			int start = usefulData.indexOf("{");
			int end = usefulData.lastIndexOf("}");
			usefulData = usefulData.substring(start + 1, end);

			return usefulData;
		}

		/*
		 * �����ݽ�������н��� ��ȡ���а��������� ������ֻ��Ӧ��һ������µ����ݽ������ȡ
		 */
		public static List<String> getResDatSet(String repStr) {
			
			String resDataStr = getUseDataForDataPart(repStr);
			if (StringUtils.isEmpty(resDataStr)) {
				Log.d(TAG, "StringUtils.isEmpty(resDataStr)");
				return null;
			}
			//xiugaiһ  SQLDataSet=anyType\\{\\};?|anyType\\{\\};?ȥ�� ���Ϊ������Ϊ�пյ�ʱ����������  product=anyType{};
			//String regex_1 = "SQLDataSet=anyType\\{\\};?|anyType\\{\\};?";
			String regex_1 = "anyType\\{\\}";
			resDataStr = resDataStr.replaceAll(regex_1, "");
			resDataStr = resDataStr.replaceAll("HideSN= ", "HideSN= ;");
			resDataStr = resDataStr.replaceAll(
					"DefectcodeSn= ;|DefectcodeSn= ", "DefectcodeSn= ;");
			String sign_4 = "SQLDataSet=anyType\\{|SQLDataSet\\d+=anyType\\{";
			Log.i("UtilTest", resDataStr);
			List<String> resDataSet = null;
			List<int[]> matchResData = StringUtils.getMatStrIndex(sign_4,
					resDataStr);
			if (null == matchResData || 0 == matchResData.size()) {
				Log.d(TAG, "null==matchResData||0==matchResData.size()");
				return null;
			}
			resDataSet = new ArrayList<String>(matchResData.size());
			Log.i(TAG, "" + matchResData.size());
			for (int i = 0; i < matchResData.size(); i++) {
				int start = matchResData.get(i)[1];
				int end = 0;
				String temp = "";
				if (i == matchResData.size() - 1) {
					temp = resDataStr.substring(start);
				} else {
					end = matchResData.get(i + 1)[0];
					temp = resDataStr.substring(start, end);
				}
				// Log.i("UtilTest", "start: "+start +" end: "+end);
				// if(start==end)
				// resDataSet.add("�޽������");
				temp = temp.substring(0, temp.lastIndexOf("}"));

				resDataSet.add(temp);
			}
			// Log.i(TAG, "NI HAO");

			return resDataSet;
		}

	}



	

	public static List<HashMap<String, String>> getResMapsLis(
			List<String> resDataSet) {
		
		if (null == resDataSet || 0 == resDataSet.size())
			return null;
		
		List<HashMap<String, String>> resArrs = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < resDataSet.size(); i++) {
			String regex = "=|;";
			
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

}
