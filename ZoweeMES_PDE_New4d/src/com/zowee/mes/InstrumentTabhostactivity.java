package com.zowee.mes;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost;

public class InstrumentTabhostactivity extends TabActivity {
	 private   TabHost  tabhost;
	 private  RadioGroup  radioGroup;
	 private  RadioButton  button1;
	 private  RadioButton  button2;
	 private  RadioButton  button3;
	 private  RadioButton  button4;
	 private  Intent  intent;
	 private  String which_activity;
	@SuppressLint("ResourceAsColor")
	@Override
     protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.activity_tabhost_instrument);
    	
    	radioGroup=(RadioGroup) findViewById(R.id.tabhost_instrument_radiogroup);
    	button1=(RadioButton) findViewById(R.id.tabhost_instrument_radiobutton1);
    	button2=(RadioButton) findViewById(R.id.tabhost_instrument_radiobutton2);
    	button3=(RadioButton) findViewById(R.id.tabhost_instrument_radiobutton3);
    	button4=(RadioButton) findViewById(R.id.tabhost_instrument_radiobutton4);
    	
    	
    	tabhost=getTabHost();
    	TabSpec tab1= tabhost.newTabSpec("FRIST").setIndicator("配置上传").setContent(new Intent(this,Instrument_linesetup.class));
        TabSpec tab2= tabhost.newTabSpec("SECOND").setIndicator("波形显示").setContent(new Intent(this,Instrument_removebinding.class));
        TabSpec tab3= tabhost.newTabSpec("THIRD").setIndicator("配置上传").setContent(new Intent(this,Instrument_molinkworkcenter.class));
        TabSpec tab4= tabhost.newTabSpec("FOURTH").setIndicator("波形显示").setContent(new Intent(this,Instrument_query.class));
        tabhost.addTab(tab1);
        tabhost.addTab(tab2);
        tabhost.addTab(tab3);
        tabhost.addTab(tab4);
        intent= getIntent();
        which_activity=intent.getStringExtra("instrument");
        if(which_activity.equalsIgnoreCase("1")){
      	  tabhost.setCurrentTab(0);
      	  button1.setBackgroundColor(R.color.lightblue);
      	  button2.setBackgroundColor(R.color.white);
      	  button3.setBackgroundColor(R.color.white);
      	  button4.setBackgroundColor(R.color.white);
        }
        if(which_activity.equalsIgnoreCase("2")){
        	  tabhost.setCurrentTab(1);
        	  button2.setBackgroundColor(R.color.lightblue);
				button1.setBackgroundColor(R.color.white);
				button3.setBackgroundColor(R.color.white);
				button4.setBackgroundColor(R.color.white);
          }
        if(which_activity.equalsIgnoreCase("3")){
        	  tabhost.setCurrentTab(2);
        	  button3.setBackgroundColor(R.color.lightblue);
        	  button1.setBackgroundColor(R.color.white);
        	  button2.setBackgroundColor(R.color.white);
        	  button4.setBackgroundColor(R.color.white);
          }
        if(which_activity.equalsIgnoreCase("4")){
        	  tabhost.setCurrentTab(3);
        	  button4.setBackgroundColor(R.color.lightblue);
        	  button2.setBackgroundColor(R.color.white);
        	  button3.setBackgroundColor(R.color.white);
        	  button1.setBackgroundColor(R.color.white);
          }
        
    	
    	
    	
    	
    	
    	radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@SuppressLint("ResourceAsColor")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(checkedId)
				{
					case R.id.tabhost_instrument_radiobutton1:
						tabhost.setCurrentTabByTag("FRIST");
						button1.setBackgroundColor(R.color.lightblue);
						button2.setBackgroundColor(R.color.white);
						button3.setBackgroundColor(R.color.white);
						button4.setBackgroundColor(R.color.white);
						break;
					case R.id.tabhost_instrument_radiobutton2:
						tabhost.setCurrentTabByTag("SECOND");
						button2.setBackgroundColor(R.color.lightblue);
						button1.setBackgroundColor(R.color.white);
						button3.setBackgroundColor(R.color.white);
						button4.setBackgroundColor(R.color.white);
						break;
					case R.id.tabhost_instrument_radiobutton3:
						tabhost.setCurrentTabByTag("THIRD");
						button3.setBackgroundColor(R.color.lightblue);
						button1.setBackgroundColor(R.color.white);
						button2.setBackgroundColor(R.color.white);
						button4.setBackgroundColor(R.color.white);
						break;
					case R.id.tabhost_instrument_radiobutton4:
						tabhost.setCurrentTabByTag("FOURTH");
						button4.setBackgroundColor(R.color.lightblue);
						button2.setBackgroundColor(R.color.white);
						button3.setBackgroundColor(R.color.white);
						button1.setBackgroundColor(R.color.white);
						break;
				
				}	
			}
		});
    	
    	
    	
    }
}
