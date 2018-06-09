package cn.yanshu.intentdemo;

import java.util.List;

import cn.yanshu.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class IntentActivity1 extends Activity {
	
	private  static final  int REQUESTCODE=1;
	
	private  EditText  ed;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intent_activity);
		
		/*Spinner  sp=(Spinner) findViewById(R.id.intent_lv);
		ArrayAdapter<String>  adapter=new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item,new String[]{"111","222"});
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		sp.setAdapter(adapter);*/
		
		ed=(EditText) findViewById(R.id.intent_edit);
		
		ListView lv=(ListView) findViewById(R.id.intent_lv);
		ArrayAdapter<String>  adapter=new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item,new String[]{"111","222"});
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				/*adapter  parent =listview
				 * view=textview
				 * */
				String text=parent.getItemAtPosition(position).toString();
				/**
				 * 其他写法:
				 * view.getText() 因为getview方法展示的
				 * arry[position]  原数据
				 * */
				
				//显示意图 启动第2个activity
				/*
				 * 隐士意图的话 需要配置activity的intent-filter  里面添加action  category  scheme 等参数 
				 * 类似web中的  servelet隐射配置
				 * 参考系统的电话拨打意图
				 * Intent  intent=new Intent();
				 * intent.setAction(Intent.Action_CALL);
				 * 等等
				 * startActivity(intent)
				 * */
				
				Intent  intent=new Intent();
				Bundle  bundle=new Bundle();
				bundle.putString("item", text);
				intent.putExtra("bundle", bundle);
				intent.setComponent(new ComponentName(IntentActivity1.this, IntentActivity2.class));
				IntentActivity1.this.startActivityForResult(intent, REQUESTCODE);
			}
		});
		
		
	}
	
	/**
	 * startactivityforresult的逆向
	 * */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1){
			if(resultCode==0){
				ed.setText(data.getStringExtra("activity2"));
			}
		}
	}
	
	

}
