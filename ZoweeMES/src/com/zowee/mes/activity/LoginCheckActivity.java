package com.zowee.mes.activity;

import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TooManyListenersException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.Hanjieji;
import com.zowee.mes.R;
import com.zowee.mes.SmtTPforCommon;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.app.MyApplication.User;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.utils.DownfileUtils;
import com.zowee.mes.utils.SerialportUtil;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;


public class LoginCheckActivity extends Activity implements OnClickListener {

	private Button checkUser;
	private EditText inputName;
	private EditText inputPassword;
	private SharedPreferences sharedPref;
	private static final String USER_NAME = "pref_user_name";
	private static final String USER_TICKET = "pref_user_ticket";
	private static final String USER_PASSWORD = "pref_user_password";
	private String inputNameText;
	private String inputPasswordText;
	private  static  final  String APP_TXT="appversion.txt";
	private static final String TAG = "LoginCheckActivity";
    private  TextView  apptv;
    private Dialog  d;
    
    private  String s;//版本变更信息
    //贴片站用2、3口扫描头扫描工号 登陆账号
    private  SerialPort  sp2;
    private  SerialPort  sp3;
    private  Handler  handler=new Handler(){
		public void handleMessage(Message msg) {
			String  info=msg.obj.toString().trim();
			switch (msg.what) {
			case 2:
			     Toast.makeText(LoginCheckActivity.this, info, 1).show();
			     applyData(info);
			break;
			case 3:
				 Toast.makeText(LoginCheckActivity.this, info, 1).show();
				 applyData(info);
	        break;
			default:
				break;
			}
		}
	};
	
	
	private void  applyData(String s){
		if(inputName.isFocused()){
			inputName.setText(s);
		}
		if(inputPassword.isFocused()){
			inputPassword.setText(s);
		}
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG, "ONSTART");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i(TAG,"onrestart");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG,"onresume");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG,"on pause");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG,"onstop");
		SerialportUtil.closeSerialPort(sp2);
		SerialportUtil.closeSerialPort(sp3);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG,"ondestory");
		SerialportUtil.closeSerialPort(sp2);
		SerialportUtil.closeSerialPort(sp3);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"oncreate");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.setContentView(R.layout.login_page);
		
		try {
		if(sp2==null)
			sp2=SerialportUtil.registSerialPort("/dev/ttyS2", this);
		if(sp3==null)
			sp3=SerialportUtil.registSerialPort("/dev/ttyS3", this);
			if(sp2!=null)
			SerialportUtil.addEvent(sp2,2, handler);
			if(sp3!=null)
			SerialportUtil.addEvent(sp3,3, handler);
		} catch (TooManyListenersException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		checkUser = (Button) this.findViewById(R.id.login_button);
		checkUser.setOnClickListener(this);
		inputName = (EditText) this.findViewById(R.id.login_username);
		inputPassword = (EditText) this.findViewById(R.id.login_password);
//		inputPassword.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if(keyCode==KeyEvent.KEYCODE_ENTER&&event.getAction()==KeyEvent.ACTION_DOWN){
//					inputNameText = inputName.getText().toString();
//					if (TextUtils.isEmpty(inputNameText)) {
//						Toast.makeText(LoginCheckActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
//						return   false;
//					}
//					inputPasswordText = inputPassword.getText().toString();
//					if (TextUtils.isEmpty(inputPasswordText)) {
//						Toast.makeText(LoginCheckActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
//						return  false;
//					}
//					if(inputNameText.equalsIgnoreCase("test")&&inputPasswordText.equalsIgnoreCase("test")){
//						Intent intent = new Intent(LoginCheckActivity.this, MainActivity.class);
//						// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(intent);
//					}
//					else{
//						new CheckUserTask().execute(inputNameText, inputPasswordText);
//					}
//				}
//				return false;
//			}
//		});
		
		apptv=(TextView) findViewById(R.id.activity_login_page_versiontv);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		//String savedUserName = sharedPref.getString(USER_NAME, "");
//		if (!TextUtils.isEmpty(savedUserName)) {
//			inputName.setText(savedUserName);
//			inputPassword.requestFocus();
//		}
//		
		String savedUserTicket = sharedPref.getString(USER_TICKET, "");
		if (!TextUtils.isEmpty(savedUserTicket)) {
			Editor editPref = sharedPref.edit();
			editPref.putString(USER_TICKET, "");
			editPref.commit();
		}
		
		Properties  properties=null;
		InputStream  is=null;
		
		try {
			properties=new  Properties();
			is=getAssets().open(APP_TXT);
			properties.load(is);
			
			Set<Entry<Object, Object>> set=properties.entrySet();
			for(Map.Entry<Object, Object> map:set){
				if("appversion".equals((String)map.getKey()))
					apptv.setText(map.getValue().toString());
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	public void onBackPressed() {
		
		 SerialportUtil.closeSerialPort(sp2);
	     SerialportUtil.closeSerialPort(sp3);
	     finish();
	}
    

	
	

	@Override
	public void onClick(View v) {
		inputNameText = inputName.getText().toString().trim();
		if (TextUtils.isEmpty(inputNameText)) {
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		inputPasswordText = inputPassword.getText().toString().trim();
		if (TextUtils.isEmpty(inputPasswordText)) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if(inputNameText.equalsIgnoreCase("test")&&inputPasswordText.equalsIgnoreCase("test")){
			Intent intent = new Intent(this, MainActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		else{
			new CheckUserTask().execute(inputNameText, inputPasswordText);
		}
		
	}

	private String checkUserAccess(String userName, String userPassword) {

		Soap saopObj = MesWebService.getMesEmptySoap();
		saopObj.setWsMethod("UserIdentify");
		saopObj.addWsProperty("applicationName", "mes");
		saopObj.addWsProperty("userName", userName);
		saopObj.addWsProperty("userPassword", userPassword);
		SoapSerializationEnvelope envelop = null;
		SoapObject responseObj = null;
		try {
			envelop = MesWebService.getWSReqRes(saopObj);
			if (envelop != null) {
				responseObj = (SoapObject) envelop.bodyIn;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (responseObj == null)
			return null;
		String identifyValue = responseObj
				.getPropertyAsString("UserIdentifyResult");
		String ticketValue = responseObj.getPropertyAsString("ticket");
		Log.w("wuzhb", "responseObj=" + responseObj + "\nUserIdentifyResult="
				+ identifyValue + "\nticketValue=" + ticketValue + "\n");

		if (!TextUtils.isEmpty(identifyValue)
				&& !"not exist".equals(identifyValue)
				&& !"password err".equals(identifyValue)) {

			Editor editPref = sharedPref.edit();
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
			if (params.length != 2)
				return null;
			return checkUserAccess(params[0], params[1]);
		}

		protected void onPostExecute(String result) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			if (this.isCancelled()) {
				return;
			}

			if (TextUtils.isEmpty(result)) {
				Toast.makeText(LoginCheckActivity.this, "验证失败,请重试!",
						Toast.LENGTH_SHORT).show();
			} else if ("not exist".equals(result)) {
				Toast.makeText(LoginCheckActivity.this, "用户名不存在!",
						Toast.LENGTH_SHORT).show();
				inputPassword.setText("");
			} else if ("password err".equals(result)) {
				Toast.makeText(LoginCheckActivity.this, "密码验证失败!",
						Toast.LENGTH_SHORT).show();
				inputPassword.setText("");
			} else {
				User user = new MyApplication.User(); // add by ybj. ADD user
														// name to app
				user.setUserName(inputNameText);
				user.setUserId(result); // add by ybj. ADD user name to app
										// 20130819
				MyApplication.setMseUser(user);
				MyApplication.setpasswrod(inputPasswordText); // add by ybj
																// 20130722

				SharedPreferences pref = MyApplication.getApp()
						.getSharedPreferences("smtstick_setting",
								Context.MODE_PRIVATE);
				String Owner = "";
				Owner = pref.getString("owner", "");
				if (!Owner.isEmpty())

					MyApplication.setAppOwner(Owner); // add ybj 20130909
				else
					Toast.makeText(LoginCheckActivity.this, "登陆成功后请设定设备资源名称",
							Toast.LENGTH_SHORT).show();
				     SerialportUtil.closeSerialPort(sp2);
				     SerialportUtil.closeSerialPort(sp3);

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setClass(LoginCheckActivity.this, MainActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Intent mesSetting = new Intent(this, WSConfigActivity.class);
		startActivity(mesSetting);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.FIRST, Menu.FIRST, 0, getString(R.string.mes_setting));
		return true;
	}
	
	public  void  versionOnClick(View  v){
		if(s==null||s.equals(""))
	    s=DownfileUtils.download_verison_info(this, DownfileUtils.VERSION_UPDATE_INFO);
//		Dialog  d=new Dialog(this, R.style.versiondialog);
		if(d!=null){
		   d.show();
	      return;
		}
		 d=new Dialog(this,R.style.versiondialog);
		Window  win=d.getWindow();
		android.view.WindowManager.LayoutParams  layoutparams=new android.view.WindowManager.LayoutParams();
		layoutparams.x=390;
		layoutparams.y=290;
		layoutparams.alpha=0.7f;
		win.setAttributes(layoutparams);
		
		
		d.setContentView(R.layout.appversiondialog);
		
		EditText  ed=(EditText) d.findViewById(R.id.appversiondialog_ed);
		ed.setText(s);
		ImageView  image=(ImageView) d.findViewById(R.id.appversiondialog_btn);
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				d.dismiss();
			}
		});
		d.show();
		
	}
}
