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
public class XbcTabhost  extends  TabActivity {
	
	private  TabHost  tabhost;
	private  Intent  intent;
	private  String  xbc;//intent传递过来的
	private  RadioGroup  radiogroip;
	private  RadioButton  radio1;
	private  RadioButton  radio2;
	private  RadioButton  radio3;
   @SuppressLint("ResourceAsColor")
   @SuppressWarnings("deprecation")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
	   // TODO Auto-generated method stub
	   super.onCreate(savedInstanceState);
	   requestWindowFeature(Window.FEATURE_NO_TITLE);
	   
	   setContentView(R.layout.activity_tabhost_xbc);
	   
	  
	   radiogroip=(RadioGroup) findViewById(R.id.tabhost_xbc_radiogroup);
	   radiogroip.setOnCheckedChangeListener(radioGroupCheckLis);
	   radio1=(RadioButton) findViewById(R.id.tabhost_xbc_radiobutton1);
	   radio2=(RadioButton) findViewById(R.id.tabhost_xbc_radiobutton2);
	   radio3=(RadioButton) findViewById(R.id.tabhost_xbc_radiobutton3);
	   
	   tabhost=getTabHost();
	   intent= getIntent();
	   xbc=intent.getStringExtra("xbc");
	   
	 // LayoutInflater.from(this).inflate(R.layout.activity_tabhost_maintenence, tabhost.getTabContentView(),true);
   
      TabSpec tab1= tabhost.newTabSpec("FRIST").setIndicator("物料绑定").setContent(new Intent(this,Xbcbinding.class));
      TabSpec tab2= tabhost.newTabSpec("SECOND").setIndicator("物料解绑").setContent(new Intent(this,Xbcunbinding.class));
      TabSpec tab3= tabhost.newTabSpec("THIRD").setIndicator("单个解绑").setContent(new Intent(this,XbcunbindingSingle.class));
      tabhost.addTab(tab1);
      tabhost.addTab(tab2);
      tabhost.addTab(tab3);
      
      if(xbc.equalsIgnoreCase("1")){
    	  tabhost.setCurrentTab(0);
    	  radio1.setBackgroundColor(R.color.lightblue);
    	  radio2.setBackgroundColor(R.color.white);
    	  radio3.setBackgroundColor(R.color.white);
      }
      if(xbc.equalsIgnoreCase("2")){
    	  tabhost.setCurrentTab(1);
    	  radio2.setBackgroundColor(R.color.lightblue);
    	  radio1.setBackgroundColor(R.color.white);
    	  radio3.setBackgroundColor(R.color.white);
      }
      if(xbc.equalsIgnoreCase("3")){
    	  tabhost.setCurrentTab(2);
    	  radio3.setBackgroundColor(R.color.lightblue);
    	  radio1.setBackgroundColor(R.color.white);
    	  radio2.setBackgroundColor(R.color.white);
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
				case R.id.tabhost_xbc_radiobutton1:
					tabhost.setCurrentTabByTag("FRIST");
					 radio1.setBackgroundColor(R.color.lightblue);
			    	  radio2.setBackgroundColor(R.color.white);
			    	  radio3.setBackgroundColor(R.color.white);
					break;
				case R.id.tabhost_xbc_radiobutton2:
				      tabhost.setCurrentTabByTag("SECOND");
				      radio2.setBackgroundColor(R.color.lightblue);
			    	  radio1.setBackgroundColor(R.color.white);
			    	  radio3.setBackgroundColor(R.color.white);
					break;
					
				case R.id.tabhost_xbc_radiobutton3:
				      tabhost.setCurrentTabByTag("SECOND");
				      radio3.setBackgroundColor(R.color.lightblue);
			    	  radio1.setBackgroundColor(R.color.white);
			    	  radio2.setBackgroundColor(R.color.white);
					break;
			}	
		}
	};
}
