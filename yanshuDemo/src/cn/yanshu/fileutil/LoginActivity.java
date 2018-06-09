package cn.yanshu.fileutil;

import cn.yanshu.R;
import cn.yanshu.R.id;
import cn.yanshu.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	    private EditText   userEdit;
	    private EditText   pwddit;
	
		   @Override
			protected void onCreate(Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				super.onCreate(savedInstanceState);
				setContentView(R.layout.login_activity);
				userEdit=(EditText) findViewById(R.id.login_username);
				pwddit=(EditText) findViewById(R.id.login_pwd);
				
				/*
				 * 从文件读取保存的用户和密码  然后展示给用户
				 * 
				 * */
				
				String userinfo=null;
				try {
					userinfo = fileutil.readFile(this, "user.txt");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(userinfo!=null&&userinfo.length()!=0){
					String[] user=userinfo.split("##");
					userEdit.setText(user[0]);
					pwddit.setText(user[1]);
				}
			}
		   
		   /*
		    * 处理button 点击事件
		    * */
			public void click(View view){
				fileutil.writeFile(this, userEdit.getText().toString(), pwddit.getText().toString());
				Toast.makeText(this, "保存成功", 1).show();
			}
			 /*
			    * 处理button 点击事件
			    * 保存到SD卡
			    * */
				public void click2(View view){
					fileutil.writeSDCard(this, userEdit.getText().toString(), pwddit.getText().toString());
					Toast.makeText(this, "保存成功", 1).show();
				}
				
				
				/**
				 * @param view
				 * 界面布局直接写点击事件必传
				 * demo作用：
				 * sharedpreferences
				 */
				public  void  click3(View view){
					SharedPreferences sp= this.getSharedPreferences("user", Context.MODE_APPEND);
					Editor editor= sp.edit();
					editor.putString("username", "dengfeng");
					editor.putString("pwd", "2884785");
					editor.commit();
				}
				/**
				 * @param view
				 * 界面布局直接写点击事件必传
				 * demo作用：
				 * sharedpreferences
				 */
				public  void  click4(View view){
					SharedPreferences sp= this.getSharedPreferences("user.txt", Context.MODE_APPEND);
					Toast.makeText(this, sp.getString("username", ""), 1).show();
				}
}
