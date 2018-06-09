package com.zowee.mes.activity;

import com.zowee.mes.R;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService.Task;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TabHost;

public class TabHost_3 extends TabActivity implements CommonActivityInterface
{
  //点击list上的查料站表，上料对料、卸料换料 都是这个程序
	private static String FIRST;
	private static String SECOND;
	private static String THIRD;
	private RadioGroup radioGroup;
	private TabHost tabHost;
	private Resources  res;
	private boolean isAfterCome = true;//true: 对进来的选项卡背景进行还原  如果是从主界面进来的话  false:不执行
	private int inComeTabBtnId = 0;//该变量保存 进来的TabBtn(radioGroup的元素)的Id
	
	@Override
	public void init()
	{
		res = this.getResources();
		radioGroup = (RadioGroup)this.findViewById(R.id.radioGroup_tb2);
	    radioGroup.setOnCheckedChangeListener(radioGroupCheckLis);
		//FIRST = res.getString(R.string.first_tab);
		SECOND = res.getString(R.string.second_tab);
		THIRD = res.getString(R.string.third_tab);
		tabHost = this.getTabHost();   
		//TabHost.TabSpec tabSpa_1 = tabHost.newTabSpec(FIRST).setIndicator(res.getString(R.string.first_tab_txt)).setContent(new Intent(this,FeedOnMateriActivity.class));
		TabHost.TabSpec tabSpa_2 = tabHost.newTabSpec(SECOND).setIndicator(res.getString(R.string.second_tab_txt)).setContent(new Intent(this,CheMatStaTabActivity.class));
		TabHost.TabSpec tabSpa_3 = tabHost.newTabSpec(THIRD).setIndicator(res.getString(R.string.second_tab_txt)).setContent(new Intent(this,RemoveMateriActivity.class));
		//tabHost.addTab(tabSpa_1);
		tabHost.addTab(tabSpa_2);
		tabHost.addTab(tabSpa_3);
		int currentTab = this.getIntent().getIntExtra("currentTab",0);
		changeTabWeightState(currentTab, radioGroup);
		this.tabHost.setCurrentTab(currentTab);
	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost_2);
		init();
		
	}
	
	private RadioGroup.OnCheckedChangeListener radioGroupCheckLis = new RadioGroup.OnCheckedChangeListener()
	{	
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			restoreBgSetting();
			switch(checkedId)
			{
				/*case R.id.btn_first_tab:
					tabHost.setCurrentTabByTag(FIRST);
					break;*/
				case R.id.btn_second_tab:
					tabHost.setCurrentTabByTag(SECOND);
					break;
				case R.id.btn_third_tab:
					tabHost.setCurrentTabByTag(THIRD);
					break;
			}
			
		}
	};
	
	@Override
	public void refresh(Task task) 
	{
		// TODO Auto-generated method stub
		
	}
	
	private void changeTabWeightState(int index,View groupView)
	{
		View view = null;
		switch(index)
		{
			/*case 0:
				view = groupView.findViewById(R.id.btn_first_tab);
			break;*/
			case 1:
				view = groupView.findViewById(R.id.btn_second_tab);
			break;
			case 2:
				view = groupView.findViewById(R.id.btn_third_tab);
			break;
		}
		if(null!=view)
			view.setBackgroundColor(this.getResources().getColor(R.color.yellow));	
		inComeTabBtnId = view.getId();
	}
	
	/** 
	 * @description 对进来选项卡按钮的背景进行还原设置
	 */
	private void restoreBgSetting()
	{
		if(!isAfterCome)
			return;
		radioGroup.findViewById(inComeTabBtnId).setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_tab_bg));
		isAfterCome = false;
	}
	
	
}
