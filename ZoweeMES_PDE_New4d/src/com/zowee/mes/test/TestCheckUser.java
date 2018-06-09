package com.zowee.mes.test;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.zowee.mes.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestCheckUser extends Activity implements OnClickListener{
	
	private Button checkUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_page);
		checkUser=(Button) this.findViewById(R.id.login_button);
		checkUser.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		String nameSpace="http://tempuri.org/";
		String methodName="UserIdentify";
		String httpURL="http://mes.zowee.com.cn:8008/MobileService.asmx";
		
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(110);		
		SoapObject saopObj = new SoapObject(nameSpace,methodName);
		saopObj.addProperty("applicationName", "mes");
		saopObj.addProperty("userName", "mes");
		saopObj.addProperty("userPassword", "mes");
		envelop.bodyOut = saopObj;		
		envelop.dotNet = true;
		
		HttpTransportSE transport = new HttpTransportSE(httpURL);
		try {
			transport.call(nameSpace+methodName,envelop);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(null==envelop||null==envelop.bodyIn){
			Log.w("wuzhb","envolop.bodyIn is NULL");
		}else{
			Log.d("wuzhb","envolop.bodyIn="+envelop.bodyIn.toString());
		}
	}

}
