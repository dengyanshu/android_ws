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
				 * ���ļ���ȡ������û�������  Ȼ��չʾ���û�
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
		    * ����button ����¼�
		    * */
			public void click(View view){
				fileutil.writeFile(this, userEdit.getText().toString(), pwddit.getText().toString());
				Toast.makeText(this, "����ɹ�", 1).show();
			}
			 /*
			    * ����button ����¼�
			    * ���浽SD��
			    * */
				public void click2(View view){
					fileutil.writeSDCard(this, userEdit.getText().toString(), pwddit.getText().toString());
					Toast.makeText(this, "����ɹ�", 1).show();
				}
				
				
				/**
				 * @param view
				 * ���沼��ֱ��д����¼��ش�
				 * demo���ã�
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
				 * ���沼��ֱ��д����¼��ش�
				 * demo���ã�
				 * sharedpreferences
				 */
				public  void  click4(View view){
					SharedPreferences sp= this.getSharedPreferences("user.txt", Context.MODE_APPEND);
					Toast.makeText(this, sp.getString("username", ""), 1).show();
				}
}
