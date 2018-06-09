package com.zowee.mes.activity;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.app.MyApplication.User;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;
/*  Modify record: 20130819
 *    20130819:
 *    1: ADD user name to app by ybj.
 * */
 
public class LoginCheckActivity extends Activity implements OnClickListener {

	private Button checkUser;
	private EditText inputName;
	private EditText inputPassword;
	private SharedPreferences sharedPref;
	private static final String USER_NAME="pref_user_name";
	private static final String USER_TICKET="pref_user_ticket";
	private String inputNameText;
	private String inputPasswordText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_page);
		checkUser = (Button) this.findViewById(R.id.login_button);
		checkUser.setOnClickListener(this);
		inputName=(EditText) this.findViewById(R.id.login_username);
		inputPassword=(EditText) this.findViewById(R.id.login_password);
		
		sharedPref=PreferenceManager.getDefaultSharedPreferences(this);
		String savedUserName=sharedPref.getString(USER_NAME, "");
		if(!TextUtils.isEmpty(savedUserName)){
			inputName.setText(savedUserName);
		}
		
		String savedUserTicket=sharedPref.getString(USER_TICKET, "");
		if(!TextUtils.isEmpty(savedUserTicket)){
			Editor editPref= sharedPref.edit();
			editPref.putString(USER_TICKET, "");
			editPref.commit();
		}
	}

	@Override
	public void onClick(View v) {
	    inputNameText=inputName.getText().toString();
		if(TextUtils.isEmpty(inputNameText)){
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		inputPasswordText=inputPassword.getText().toString();
		if(TextUtils.isEmpty(inputPasswordText)){
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		 new CheckUserTask().execute(inputNameText,inputPasswordText);
	}

	private String checkUserAccess(String userName, String userPassword) {

		Soap saopObj = MesWebService.getMesEmptySoap();
		saopObj.setWsMethod("UserIdentify");
		saopObj.addWsProperty("applicationName", "mes");
		saopObj.addWsProperty("userName", userName);
		saopObj.addWsProperty("userPassword", userPassword);
		SoapSerializationEnvelope envelop = null;
		SoapObject responseObj=null;
		try {
			envelop = MesWebService.getWSReqRes(saopObj);
			if(envelop!=null){
				responseObj=(SoapObject) envelop.bodyIn;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(responseObj==null) return null;		
		String identifyValue=responseObj.getPropertyAsString("UserIdentifyResult");
		String ticketValue=responseObj.getPropertyAsString("ticket");
		Log.w("wuzhb","responseObj="+responseObj+"\nUserIdentifyResult="+identifyValue+"\nticketValue="+ticketValue+"\n");
		
		if(!TextUtils.isEmpty(identifyValue) 
			&& !"not exist".equals(identifyValue)
			&& !"password err".equals(identifyValue)){
			
			Editor editPref= sharedPref.edit();
			editPref.putString(USER_NAME, userName);
			editPref.putString(USER_TICKET, ticketValue);
			editPref.commit();
		}
		
		return identifyValue;

	}

	private class CheckUserTask extends AsyncTask<String, Object, String>
			implements OnCancelListener {

		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(LoginCheckActivity.this);
			mProgressDialog.setMessage("正在登录中...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setOnCancelListener(this);
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			if(params.length!=2)return null;
			return checkUserAccess(params[0],params[1]);
		}
		
		protected void onPostExecute(String result) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			if (this.isCancelled()) {
				return;
			}
			
			if(TextUtils.isEmpty(result)){
				Toast.makeText(LoginCheckActivity.this, "验证失败,请重试!", Toast.LENGTH_SHORT).show();
			}else if("not exist".equals(result)){
				Toast.makeText(LoginCheckActivity.this, "用户名不存在!", Toast.LENGTH_SHORT).show();	
				inputPassword.setText("");
			}else if("password err".equals(result)){
				Toast.makeText(LoginCheckActivity.this, "密码验证失败!", Toast.LENGTH_SHORT).show();	
				inputPassword.setText("");
			}else{
				User user = new MyApplication.User();  // add by ybj. ADD user name to app  
			    user.setUserName(inputNameText);     
				user.setUserId(result);   // add by ybj. ADD user name to app 20130819
				MyApplication.setMseUser(user);
				String Owner="";				
				SharedPreferences pref = MyApplication.getApp().getSharedPreferences("smtstick_setting", Context.MODE_PRIVATE);
				Owner = pref.getString("owner", "");
				if(!Owner.isEmpty()) {
					MyApplication.getApp();
					MyApplication.setAppOwner(Owner);   // add ybj 20130909
				} else
					Toast.makeText(LoginCheckActivity.this, "登陆成功后请设定设备资源名称", Toast.LENGTH_SHORT).show();	
				
				MyApplication.setpasswrod(inputPasswordText); // add by ybj 20130722
     			Intent intent=new Intent(Intent.ACTION_MAIN);		
				intent.setClass(LoginCheckActivity.this, MainActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			this.cancel(true);
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);		
		Intent mesSetting = new Intent(this, WSConfigActivity.class);
		startActivity(mesSetting);
		return true;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
    	super.onCreateOptionsMenu(menu);
    	menu.add(Menu.FIRST,Menu.FIRST, 0, getString(R.string.mes_setting));
    	return true;
    }
}
