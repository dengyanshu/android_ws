package com.zowee.mes.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;

/**
 * @author Administrator
 * @description 对ZoweeMes软件信息描述
 */
public class AboutZoweeMesActivity extends Activity 
{
	private TextView labEditionInfor;
	private int curEdition ;
	
	private void init()
	{
		labEditionInfor = (TextView)this.findViewById(R.id.labSoftEdition);
		curEdition = MyApplication.getCurAppVersion();
		
		labEditionInfor.setText(getString(R.string.currentEdition)+ " V"+curEdition);
		//labEditionInfor.setTe
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_zoweemes);
		init();
	}
	
	
}
