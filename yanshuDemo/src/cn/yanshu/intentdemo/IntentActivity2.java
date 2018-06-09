package cn.yanshu.intentdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.yanshu.R;

public class IntentActivity2 extends Activity {
	
	private  Intent  intent;
	private  EditText  ed2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intent_activity2);
		
		
		
		intent=getIntent();
		Bundle bundle=intent.getBundleExtra("bundle");
		String text=bundle.get("item").toString();
		EditText  ed1=(EditText) findViewById(R.id.intent2_edit1);
		ed2=(EditText) findViewById(R.id.intent2_edit2);
		ed1.setText(text);
	}
   
	
	public void click(View  view){
		//点击 获取ed2的值 回传给activity1
		  String text=ed2.getText().toString();
		  Intent intent=new Intent();
		  intent.putExtra("activity2", text);
		  setResult(0, intent);
		  finish();
	}
}
