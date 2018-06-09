package com.zowee.mes.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.update.MesUpdateParse;
import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description  MES-PDA 更新配置
 */
public class MESPDAUpdateConfigActivity extends Activity implements CommonActivityInterface ,View.OnClickListener
{

	private View btnGroups;
	private Button btnModify;
	private EditText edtUpsource;
	private Button btnExit;
	private Button btnRestore;
	
	@Override
	public void init() 
	{
		btnGroups = this.findViewById(R.id.btnGroups);
		btnModify = (Button)btnGroups.findViewById(R.id.btn_update);
		btnExit = (Button)btnGroups.findViewById(R.id.btn_cancel);
		btnRestore = (Button)btnGroups.findViewById(R.id.btn_restore);
		edtUpsource = (EditText)this.findViewById(R.id.edt_updatesource);
		String updateSource = MesUpdateParse.getUserDefUpdateSource();
		edtUpsource.setText(updateSource);
		
		btnModify.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mesupdate_config_view);
		init();
		
	}
	
	
	@Override
	public void refresh(Task task) 
	{
		

	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_update:
				String updateSource = edtUpsource.getText().toString();
				boolean isPass = subUpdateSource(updateSource);
				if(!isPass) return;
				MesUpdateParse.mergeUserDefUpdateSource(updateSource);
				Toast.makeText(this, R.string.mesupdate_setting_success, 1500).show();
				break;
			case R.id.btn_cancel:
				this.finish();
				break;
			case R.id.btn_restore:
				String originalUpdateSource = MesUpdateParse.getLocalUpdateSource();
				if(StringUtils.isEmpty(originalUpdateSource))
					originalUpdateSource = "";
				MesUpdateParse.mergeUserDefUpdateSource(originalUpdateSource);
				edtUpsource.setText(originalUpdateSource);
				Toast.makeText(this, R.string.mesupdate_setting_restore, 1500).show();
				break;
				
		}
		
	}
	
	/**
	 * @description 对更新源进行有效的数据验证  
	 */
	private boolean subUpdateSource(String str)
	{
		boolean isPass = true;
		
		if(StringUtils.isEmpty(str))
		{
			isPass = false;
			Toast.makeText(this, R.string.updateSource_isnull, 1500).show();
			return isPass;
		}
		
		String regex = "^https?://(www.)?[\\S]+$";//对更新源地址进行有效性验证
		if(!StringUtils.findStr(str, regex))
		{
			isPass = false;
			Toast.makeText(this, R.string.updateSource_error, 1500).show();
			return isPass;
		}
		
		return isPass;
	}
	
	
}
