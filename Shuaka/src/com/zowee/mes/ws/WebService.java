package com.zowee.mes.ws;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description 这是服务类 专门进行WebService的请求处理
 */

public class WebService {

	public static final String WSNAMESPACE = "NameSpace";
	public static final String WSMETHOD = "METHOD";
	public static final String WSDL = "WSDL";
	public static final String WSSOAPEDITION = "SoapEditon";
	private static final String TAG = "KsoapTest";
	public static final String WSISDOTNET = "DOTNET";

	/**
	 * @param wbParams
	 *            它必须包含WS请求参数 NameSpace,Method,WSDL,SaopEdition的值也可以添加其他的请求参数
	 *            和前者一样 参数传递都是通过Key-Value的形式
	 * @return
	 */
	public static SoapSerializationEnvelope getWSReqRes(Soap soap)
			throws Exception {
		
		Log.i(TAG, WSNAMESPACE + " : " + soap.wsNameSpa + " ," + WSDL + " : "
				+ soap.wsWsdl + " ," + WSMETHOD + " : " + soap.wsMethod + " ,"
				+ WSSOAPEDITION + " : " + soap.soapEdi);
		String nameSpa = soap.getWsNameSpa();
		String method = soap.getWsMethod();
		String wsdl = soap.getWsWsdl();
		int soapEdt = soap.getSoapEdi();
		boolean isDotNet = soap.isDotNet;
		if (StringUtils.isEmpty(nameSpa))
			throw new NullPointerException("WebService命名空间的值没有指定");
		if (StringUtils.isEmpty(method))
			throw new NullPointerException("WebService调用的方法名没有指定");
		if (StringUtils.isEmpty(wsdl))
			throw new NullPointerException("webservice服务的中转地址没有指定");
		if (SoapSerializationEnvelope.VER10 != soapEdt
				&& SoapSerializationEnvelope.VER11 != soapEdt
				&& SoapSerializationEnvelope.VER12 != soapEdt)
			throw new NullPointerException("webservice服务的soap协议版本没有指定");

		SoapObject saopObj = new SoapObject(nameSpa, method);
		// saopObj.
		// saopObj.addAttribute("Content-Type",
		// "application/soap+xml; charset=utf-8");
		for (Map.Entry<String, Object> wbEntry : soap.getWsProperty()
				.entrySet()) {
			String key = wbEntry.getKey();
			Object value = wbEntry.getValue();
			Log.i(TAG, key + " : " + value);
			saopObj.addProperty(key, value);
		}
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
				Integer.valueOf(soapEdt));
		envelop.bodyOut = saopObj;
		if (isDotNet)
			envelop.dotNet = true;
		HttpTransportSE transport = new HttpTransportSE(wsdl,10*1000);
		transport.call(nameSpa + method, envelop);

		return envelop;
	}

	public static class Soap implements Serializable, Cloneable {
		private String wsNameSpa;// ws命名空间
		private String wsMethod;// ws调用的方法
		private String wsWsdl;// ws的中转服务器
		private int soapEdi;// soap协议的版本
		private boolean isDotNet = false;// ws服务是否是dotnet实现
		private Map<String, Object> wsParas = new HashMap<String, Object>();

		public Soap(String wsNameSpa, String wsMethod, String wsWsdl,
				int soapEdi) {
			super();
			this.wsNameSpa = wsNameSpa;
			this.wsMethod = wsMethod;
			this.wsWsdl = wsWsdl;
			this.soapEdi = soapEdi;

		}

		public Soap() {
			super();
		}

		public String getWsNameSpa() {
			return wsNameSpa;
		}

		public void setWsNameSpa(String wsNameSpa) {
			this.wsNameSpa = wsNameSpa;
		}

		public String getWsMethod() {
			return wsMethod;
		}

		public void setWsMethod(String wsMethod) {
			this.wsMethod = wsMethod;
		}

		public String getWsWsdl() {
			return wsWsdl;
		}

		public void setWsWsdl(String wsWsdl) {
			this.wsWsdl = wsWsdl;
		}

		public int getSoapEdi() {
			return soapEdi;
		}

		public void setSoapEdi(int soapEdi) {
			this.soapEdi = soapEdi;
		}

		public boolean isDotNet() {
			return isDotNet;
		}

		public void setDotNet(boolean isDotNet) {
			this.isDotNet = isDotNet;
		}

		public void addWsProperty(String key, Object value) {
			if (StringUtils.isEmpty(key) || null == value)
				return;
			wsParas.put(key, value);

		}

		public Map<String, Object> getWsProperty() {

			return wsParas;
		}

	}

}
