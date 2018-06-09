package com.zowee.mes;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressLint("ResourceAsColor")
public class HanjiejiTabhost  extends  TabActivity {
	
	private  TabHost  tabhost;
	private  Intent  intent;
	private  String  activity;
	private  RadioGroup  radiogroip;
	private  RadioButton  radio1;
	private  RadioButton  radio2;
   @SuppressLint("ResourceAsColor")
   @SuppressWarnings("deprecation")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
	   // TODO Auto-generated method stub
	   super.onCreate(savedInstanceState);
	   requestWindowFeature(Window.FEATURE_NO_TITLE);
	   
	   setContentView(R.layout.activity_tabhost_hanjieji);
	   
	  
	   radiogroip=(RadioGroup) findViewById(R.id.tabhost_maintenance_radiogroup);
	   radiogroip.setOnCheckedChangeListener(radioGroupCheckLis);
	   radio1=(RadioButton) findViewById(R.id.tabhost_maintenance_radiobutton1);
	   radio2=(RadioButton) findViewById(R.id.tabhost_maintenance_radiobutton2);
	   
	   tabhost=getTabHost();
	   intent= getIntent();
	   activity=intent.getStringExtra("hanjieji");
	   
	 // LayoutInflater.from(this).inflate(R.layout.activity_tabhost_maintenence, tabhost.getTabContentView(),true);
   
      TabSpec tab1= tabhost.newTabSpec("FRIST").setIndicator("配置上传").setContent(new Intent(this,Hanjiejipeizhi.class));
      TabSpec tab2= tabhost.newTabSpec("SECOND").setIndicator("波形显示").setContent(new Intent(this,Hanjiejiboxing.class));
      tabhost.addTab(tab1);
      tabhost.addTab(tab2);
      
      if(activity.equalsIgnoreCase("1")){
    	  tabhost.setCurrentTab(0);
    	  radio1.setBackgroundColor(R.color.lightblue);
    	  radio2.setBackgroundColor(R.color.white);
      }
      if(activity.equalsIgnoreCase("2")){
    	  tabhost.setCurrentTab(1);
    	  radio2.setBackgroundColor(R.color.lightblue);
    	  radio1.setBackgroundColor(R.color.white);
      }
   }
   
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	   // TODO Auto-generated method stub
	   return super.onCreateOptionsMenu(menu);
   }
   
   @SuppressLint("ResourceAsColor")
private RadioGroup.OnCheckedChangeListener radioGroupCheckLis = new RadioGroup.OnCheckedChangeListener()
	{	
		@SuppressLint("ResourceAsColor")
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			switch(checkedId)
			{
				case R.id.tabhost_maintenance_radiobutton1:
					tabhost.setCurrentTabByTag("FRIST");
					radio1.setBackgroundColor(R.color.lightblue);
			    	  radio2.setBackgroundColor(R.color.white);
					break;
				case R.id.tabhost_maintenance_radiobutton2:
				      tabhost.setCurrentTabByTag("SECOND");
				      radio2.setBackgroundColor(R.color.lightblue);
			    	  radio1.setBackgroundColor(R.color.white);
					break;
			}	
		}
	};
}
