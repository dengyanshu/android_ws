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
 * @description ���Ƿ����� ר�Ž���WebService��������
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
	 *            ���������WS������� NameSpace,Method,WSDL,SaopEdition��ֵҲ��������������������
	 *            ��ǰ��һ�� �������ݶ���ͨ��Key-Value����ʽ
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
			throw new NullPointerException("WebService�����ռ��ֵû��ָ��");
		if (StringUtils.isEmpty(method))
			throw new NullPointerException("WebService���õķ�����û��ָ��");
		if (StringUtils.isEmpty(wsdl))
			throw new NullPointerException("webservice�������ת��ַû��ָ��");
		if (SoapSerializationEnvelope.VER10 != soapEdt
				&& SoapSerializationEnvelope.VER11 != soapEdt
				&& SoapSerializationEnvelope.VER12 != soapEdt)
			throw new NullPointerException("webservice�����soapЭ��汾û��ָ��");

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
		private String wsNameSpa;// ws�����ռ�
		private String wsMethod;// ws���õķ���
		private String wsWsdl;// ws����ת������
		private int soapEdi;// soapЭ��İ汾
		private boolean isDotNet = false;// ws�����Ƿ���dotnetʵ��
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
