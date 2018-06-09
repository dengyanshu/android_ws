package com.zowee.mes.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.laser.LaserConfigParser;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.service.BackgroundService.Task;
/**
 * @author Administrator
 * @description 激光扫描功能的配置 开启 或关闭
 */
public class LaserFunctionConfigActivity extends Activity  implements CommonActivityInterface ,RadioGroup.OnCheckedChangeListener,View.OnClickListener
{

	private View btnGroups;
	private RadioGroup rgLaserFunc;
	private Button btnCancel;
	private Button btnUpdate;
	private Button btnRestore;
	private boolean isEnable;//激光扫描功能是否开启
	private RadioButton rbLaserOn;
	private RadioButton rbLaserOff;
	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub  //
		btnGroups = this.findViewById(R.id.btnGroups);
		rgLaserFunc = (RadioGroup)this.findViewById(R.id.rg_laserFuncConfig);
		rbLaserOn = (RadioButton)rgLaserFunc.findViewById(R.id.rb_laseron);
		rbLaserOff = (RadioButton)rgLaserFunc.findViewById(R.id.rb_laseroff);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnUpdate = (Button)btnGroups.findViewById(R.id.btn_update);
		btnRestore = (Button)btnGroups.findViewById(R.id.btn_restore);
		btnRestore.setVisibility(View.GONE);
		
		isEnable = LaserConfigParser.getLaserFunctionEnable();
		if(isEnable)  rbLaserOn.setChecked(true);
		else   rbLaserOff.setChecked(true);   
			
		rgLaserFunc.setOnCheckedChangeListener(this);
		btnUpdate.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.laserfunc_config_view);
		init();
	}
	
	@Override
	public void refresh(Task task) 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		switch(checkedId)
		{
			case R.id.rb_laseron:
				isEnable = true;
				break;
			case R.id.rb_laseroff:
				isEnable = false;
				break;
		}
		
	}
	
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_update:
				btnUpdate.setEnabled(false);
				LaserConfigParser.mergeLaserFunctionEnable(isEnable);
				LaserScanOperator.changeLaserEnable(LaserConfigParser.getLaserFunctionEnable());
				Toast.makeText(this, R.string.laserFunc_configSucc, 1500).show();
				btnUpdate.setEnabled(true);
				break;
			case R.id.btn_cancel:
				this.finish();
				break;
		
		}
		
	}
	
	
}
