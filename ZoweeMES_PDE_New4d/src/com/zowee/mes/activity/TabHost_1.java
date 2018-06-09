package com.zowee.mes.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.zowee.mes.R;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.model.ParpareOperationModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.utils.DisplayDeviceUtils;
import com.zowee.mes.utils.DisplayDeviceUtils.DisplayInfor;
/**
 * @author Administrator
 * @description �����������
 */
public class TabHost_1 extends TabActivity implements CommonActivityInterface
{
   private static String FIRST ;//���ɨ��
   private static String SECOND;//Fifo��ѯ
   private static String THIRD;//��ֳ���
   private static String FOURTH;//��������
   private static String FIFTH;//���Ų�ѯ
   private Resources  res;
   private static final String TAG = "activity";
   private RadioGroup radioGroup;
   private TabHost tabHost;
   private static int curTab = 0;
   private boolean isAfterCome = true;//true: �Խ�����ѡ��������л�ԭ  ����Ǵ�����������Ļ�  false:��ִ��
   private int inComeTabBtnId = 0;//�ñ������� ������TabBtn(radioGroup��Ԫ��)��Id
   
   public void init()
   {
	   res = this.getResources();
	   FIRST = res.getString(R.string.first_tab);
	   SECOND = res.getString(R.string.second_tab);
	   THIRD = res.getString(R.string.third_tab);
	   FOURTH = res.getString(R.string.fourth_tab);
	   FIFTH = res.getString(R.string.fifth_tab);
	   
	   
	   tabHost = this.getTabHost();
	   Log.i(TAG,res.getString(R.string.fiftg_tab_txt));
	   TabHost.TabSpec tabSpa_1 = tabHost.newTabSpec(FIRST).setIndicator(FIRST).setContent(new Intent(this,StorageScanActivity.class));
	   TabHost.TabSpec tabSpa_2 = tabHost.newTabSpec(SECOND).setIndicator(SECOND).setContent(new Intent(this,FifoScanActivity.class));
	   TabHost.TabSpec tabSpa_3 = tabHost.newTabSpec(THIRD).setIndicator(THIRD).setContent(new Intent(this,SplitStoreActivity.class));
	   TabHost.TabSpec tabSpa_4 = tabHost.newTabSpec(FOURTH).setIndicator(FOURTH).setContent(new Intent(this,QuantityAdjustActivity.class));
	   TabHost.TabSpec tabSpa_5 = tabHost.newTabSpec(FIFTH).setIndicator(FIFTH).setContent(new Intent(this,BatNumScanActivity.class));
	   //TabHost.TabSpec tabSpa_2 = tabHost.newTabSpec(SECOND).setIndicator(res.getString(R.string.second_tab_txt)).setContent(new Intent(this,FifoScanActivity.class));
	   tabHost.addTab(tabSpa_1);
	   tabHost.addTab(tabSpa_2);
	   tabHost.addTab(tabSpa_3);
	   tabHost.addTab(tabSpa_4);
	   tabHost.addTab(tabSpa_5);
	   
	   int currentTab = this.getIntent().getIntExtra("currentTab", 0);
	   this.tabHost.setCurrentTab(currentTab);
	   radioGroup = (RadioGroup)this.findViewById(R.id.radioGroup);
	   adjustRg(4, radioGroup);
	   
	   changeTabWeightState(currentTab, radioGroup);
	 
	   
	   
	   
	   radioGroup.setOnCheckedChangeListener(radioGroupCheckLis);
   }
   
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		init();
		Log.i(TAG, "tabhost is creating");
	}
    
	@Override
	public void refresh(Task task) 
	{
			// TODO Auto-generated method stub
			
	}
    
	private RadioGroup.OnCheckedChangeListener radioGroupCheckLis = new RadioGroup.OnCheckedChangeListener()
	{	
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			restoreBgSetting();
			switch(checkedId)
			{
				case R.id.btn_first_tab:
					tabHost.setCurrentTabByTag(FIRST);
					break;
			/*	case R.id.btn_second_tab:
					tabHost.setCurrentTabByTag(SECOND);
					break;
				case R.id.btn_third_tab:
					tabHost.setCurrentTabByTag(THIRD);
					break;
				case R.id.btn_fourth_tab:
					tabHost.setCurrentTabByTag(FOURTH);
					break;
				case R.id.btn_fifth_tab:
					tabHost.setCurrentTabByTag(FIFTH);
					break; */  //ybj 
			}
			
		}
	};
	
	/**
	 * @param index
	 * @param groupView
	 * @description �Դ������������ѡ�ť�������иı� Ϊ��֤��������� ���ڽ���ԭ�ñ�������
	 */
	private void changeTabWeightState(int index,View groupView)
	{
		View view = null;
		switch(index)
		{
			case 0:
				view = groupView.findViewById(R.id.btn_first_tab);
			break;
		/*	case 1:
				view = groupView.findViewById(R.id.btn_second_tab);
			break;
			case 2:
				view = groupView.findViewById(R.id.btn_third_tab);
			break;
			case 3:
				view = groupView.findViewById(R.id.btn_fourth_tab);
			break;
			case 4:
				view = groupView.findViewById(R.id.btn_fifth_tab);
			break; */  //ybj 
		}
		
		if(null!=view)
			view.setBackgroundColor(this.getResources().getColor(R.color.yellow));	
			inComeTabBtnId = view.getId();//��ȡ������ѡ�ťId

	}
	
	/** 
	 * @description �Խ���ѡ���ť�ı������л�ԭ����
	 */
	private void restoreBgSetting()
	{
		if(!isAfterCome)
			return;
		radioGroup.findViewById(inComeTabBtnId).setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_tab_bg));
		isAfterCome = false;
	}
	
	/**
	 *  viewUnit = ÿ����ʾ��Ԫ����������
	 * @description  ����RadioGroup���ı���ʾ ����Ӧ��Ļ��С�ı仯
 	 */
	private void adjustRg(int viewUnit,RadioGroup rg)
	{
		DisplayInfor displayInfor = DisplayDeviceUtils.getDisplayInfor(this);
		int childCount = rg.getChildCount();
		int widthDp = displayInfor.widthDp;
		int maxDp = widthDp/(childCount*viewUnit);//rg���ı��������ֵ
		int viewDp = 1;
		Log.i(TAG, "MaxDp: "+maxDp);
		if(maxDp>=17)
			viewDp = 16;
		else
			viewDp = maxDp-1;
		Log.i(TAG, "viewDp: "+viewDp);
		for(int i=0;i<childCount;i++)
		{
			RadioButton rb = (RadioButton)rg.getChildAt(i);
			rb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, viewDp);
		}
		
	}
	
	
}