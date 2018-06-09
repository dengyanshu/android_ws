package com.zowee.mes.activity;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserConfigParser;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.laser.SerialPortOper.SerialParams;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Administrator
 * @description 激光扫描用户自定义配置界面
 */
public class LaserConfigActivity extends CommonActivity implements View.OnClickListener
{
	private View btnGroups;
	private EditText edtSerialName;
	private EditText edtOwner;
	private EditText edtBaurate;
	private EditText edtTimeout;
	private EditText edtDatabits;
	private EditText edtEndbits;
	private EditText edtPriority;
	private EditText edtFlowControl;
	private Button btnModify;
	private Button btnCancel;
	private Button btnRestore;
	//private ProgressDialog progressDialog;
	
	@Override
	public void init()
	{
		//progressDialog = new ProgressDialog(this);
		//progressDialog.setTitle("正在配置激光扫描!");
		btnGroups = this.findViewById(R.id.btnGroups);
	//	((Button)btnGroups.findViewById(R.id.btn_scan)).setVisibility(View.GONE);
		btnModify = (Button)btnGroups.findViewById(R.id.btn_update);
		btnRestore = (Button)btnGroups.findViewById(R.id.btn_restore);
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
		edtSerialName = (EditText)this.findViewById(R.id.edt_serialName);
		edtOwner = (EditText)this.findViewById(R.id.edt_owner);
		edtBaurate = (EditText)this.findViewById(R.id.edt_baurate);
		edtTimeout = (EditText)this.findViewById(R.id.edt_timeout);
		edtDatabits = (EditText)this.findViewById(R.id.edt_databit);
		edtEndbits = (EditText)this.findViewById(R.id.edt_endbit);
		edtPriority = (EditText)this.findViewById(R.id.edt_priorityBit);
		edtFlowControl = (EditText)this.findViewById(R.id.edt_flowControl);
		SerialParams serialParams  = LaserConfigParser.getFinalSerialParams();
		initUIData(serialParams);
		
		btnModify.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
		//btnCancel.setVisibility(View.GONE);
		//btnCancel.setText(R.string.abolish);
		

	}

	/**
	 * @description 将激光扫描配置信息显示在UI界面上
	 */
	private void initUIData(SerialParams serialParams) 
	{
		if(null==serialParams) return;
		edtSerialName.setText(serialParams.name);
		edtOwner.setText(serialParams.owner);
		edtBaurate.setText(serialParams.baudRate+"");
		edtTimeout.setText(serialParams.timeOut+"");
		edtDatabits.setText(serialParams.dataBits+"");
		edtEndbits.setText(serialParams.endBits+"");
		edtPriority.setText(serialParams.parity+"");
		edtFlowControl.setText(serialParams.flowControl+"");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.laserconfig_view);
		init();
	}
	

	
	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.btn_update:
			btnModify.setEnabled(false);
			boolean isPass = subUIData();
			if(!isPass)
			{
				btnModify.setEnabled(true);
				return;
			}
			String name = edtSerialName.getText().toString().trim();
			String owner = edtOwner.getText().toString().trim();
			int baudRate = Integer.valueOf(edtBaurate.getText().toString().trim());
			int timeOut = Integer.valueOf(edtTimeout.getText().toString().trim());
			int dataBits = Integer.valueOf(edtDatabits.getText().toString().trim());
			int endBits = Integer.valueOf(edtEndbits.getText().toString().trim());
			int parity = Integer.valueOf(edtPriority.getText().toString().trim());
			int flowControl = Integer.valueOf(edtFlowControl.getText().toString().trim());
			SerialParams serialParams = new SerialParams(name, owner, timeOut, baudRate, dataBits, endBits, parity, flowControl);
		//	progressDialog.setMessage(getString(R.string.configuring_laser));
		//	progressDialog.show();
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			LaserConfigParser.mergeLaserConfig(serialParams);
			serialParams = LaserConfigParser.getFinalSerialParams();
			LaserScanOperator.setSerialParams(serialParams);
		//	progressDialog.dismiss();
			Toast.makeText(this, R.string.laserConfig_Success, Toast.LENGTH_SHORT).show();
			btnModify.setEnabled(true);
			break;
		case R.id.btn_cancel:
			this.finish();
			break;
		case R.id.btn_restore:
			SerialParams originalSerialParams = LaserConfigParser.getLaserConfig();
			LaserConfigParser.mergeLaserConfig(originalSerialParams);
			initUIData(LaserConfigParser.getFinalSerialParams());
			LaserScanOperator.setSerialParams(LaserConfigParser.getFinalSerialParams());
			Toast.makeText(this, R.string.laserConfig_restore, 1500).show();
			break;
		}
		
	}
	
	private boolean subUIData()
	{
		boolean isPass = false;
		isPass = subView(edtSerialName);
		if(!isPass)
		{
			Toast.makeText(this, R.string.serialName_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtOwner);
		if(!isPass)
		{
			Toast.makeText(this, R.string.owner_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtBaurate);
		if(!isPass)
		{
			Toast.makeText(this, R.string.baurate_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtTimeout);
		if(!isPass)
		{
			Toast.makeText(this, R.string.timeout_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtDatabits);
		if(!isPass)
		{
			Toast.makeText(this, R.string.databits_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtEndbits);
		if(!isPass)
		{
			Toast.makeText(this, R.string.endbits_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtPriority);
		if(!isPass)
		{
			Toast.makeText(this, R.string.priority_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		isPass = subView(edtFlowControl);
		if(!isPass)
		{
			Toast.makeText(this, R.string.flowControl_isnull, Toast.LENGTH_SHORT).show();
			return isPass;
		}
		
		return isPass;	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode ==MyApplication.SCAN_KEYCODE)
			return true;
		if(keyCode ==27) // add by ybj 20140321
			return true;
		if(keyCode ==KeyEvent.KEYCODE_SEARCH) // add by ybj 20140321
			return true;
		return super.onKeyDown(keyCode, event);
	}
	
	
}
