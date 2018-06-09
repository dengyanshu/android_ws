package com.zowee.mes.activity;

import java.util.ArrayList;
import java.util.List;

import com.zowee.mes.R;
import com.zowee.mes.SMTStickSetting;
import com.zowee.mes.adapter.SettingAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.model.MESUpdateModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.update.MESUpdate;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 * @description MES-PDA 软件设置
 */
public class SettingActivity extends Activity implements
		CommonActivityInterface

{

	private ListView lv;
	private String[] settings;// 用于存储设置选项
	private SettingAdapter adapter;
	private ProgressDialog progressDialog;

	public void init() {
		lv = (ListView) this.findViewById(R.id.lv);
		// lv.setOnItemSelectedListener(this);
		settings = getResources().getStringArray(R.array.settings);
		// lsText.add(getString(R.string.laser_config));
		// lsText.add(getString(R.string.ws_config));
		// lsText.add(getString(R.string.mesupdate));
		adapter = new SettingAdapter(settings, this);
		lv.setAdapter(adapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.mesupdate);
		progressDialog.setMessage(getString(R.string.checkingupdate));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.setting);
		init();
	}

	public View.OnClickListener labClicLis = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			TextView tempLab = (TextView) v;
			String textVal = tempLab.getText().toString();
			Intent configIntent = null;
			if (textVal.equals(getString(R.string.laserScan_config)))// 激光扫描配置
			{
				configIntent = new Intent(SettingActivity.this,
						LaserConfigActivity.class);
				SettingActivity.this.startActivity(configIntent);
				return;
			} else if (textVal.equals(getString(R.string.ws_config)))// WebService
																		// 配置
			{
				configIntent = new Intent(SettingActivity.this,
						WSConfigActivity.class);
				SettingActivity.this.startActivity(configIntent);
				return;
			}
			/*
			 * else
			 * if(textVal.equals(getString(R.string.mesupdate_setting)))//WebService
			 * 配置 { configIntent = new Intent(SettingActivity.this,
			 * MESPDAUpdateConfigActivity.class);
			 * SettingActivity.this.startActivity(configIntent); return; }
			 */
			if (textVal.equals(getString(R.string.laser_Config)))// 激光头配置
			{
				configIntent = new Intent(SettingActivity.this,
						LaserFunctionConfigActivity.class);
				SettingActivity.this.startActivity(configIntent);
				return;
			}
			/**
			 * 条码获取方式
			 * 
			 * */
			if (textVal.equals("条码规则配置"))// 关于ZoweeMes版本信息
			{
                AlertDialog.Builder  builder=new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("条码规则配置");
                builder.setItems(new String[]{"无规则","双MAC逗号隔开","华为二维码"}, 
                		new  DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								switch (which) {
								case 0:
									MyApplication.setLotsnRule(0);
									break;
								case 1:
									MyApplication.setLotsnRule(1);
									break;
								case 2:
									MyApplication.setLotsnRule(2);
									break;
								default:
									break;
								}
							}
					});
                builder.create();
                builder.show();
				return;
			}
			
			
			if (textVal.equals(getString(R.string.about)))// 关于ZoweeMes版本信息
			{
				configIntent = new Intent(SettingActivity.this,
						AboutZoweeMesActivity.class);
				SettingActivity.this.startActivity(configIntent);
				return;
			}
			if (textVal.equals(getString(R.string.mesupdate)))// MES 更新
			{
				Task updateCheck = new Task(SettingActivity.this,
						TaskType.MESUPDATE_CHECK, null);
				MESUpdateModel updateModel = new MESUpdateModel();
				if (null != progressDialog && !progressDialog.isShowing())
					progressDialog.show();
				updateModel.existNewEdition(updateCheck);
			}
			if (textVal.equals(getString(R.string.smtstick_setting)))// SMT贴片设置
			{
				configIntent = new Intent(SettingActivity.this,
						SMTStickSetting.class);
				SettingActivity.this.startActivity(configIntent);
				return;
			}

		}
	};

	@Override
	public void refresh(Task task) {
		switch (task.getTaskType()) {
		case TaskType.MESUPDATE_CHECK:
			if (null != progressDialog && progressDialog.isShowing())
				progressDialog.dismiss();
			Object[] reses = (Object[]) task.getTaskResult();
			boolean existNewEdi = (Boolean) reses[0];
			MESUpdate mesUpdate = (MESUpdate) reses[1];
			if (existNewEdi) {
				Intent updateIntent = new Intent(this, MESUpdateActivity.class);
				updateIntent.putExtra(MESUpdateModel.EXISTNEWEDI, existNewEdi);
				updateIntent.putExtra(MESUpdateModel.MESUPDATE, mesUpdate);
				startActivity(updateIntent);
			} else
				Toast.makeText(this, R.string.latest_version, 1500).show();
			break;
		case TaskType.MES_CHECKUPDATE_FAIL:
			if (null != progressDialog && progressDialog.isShowing())
				progressDialog.dismiss();
			String updateFailMsg = null;
			if (StringUtils.isEmpty(task.getTaskResult().toString()))
				updateFailMsg = getString(R.string.mesupdate_fail);
			else
				updateFailMsg = task.getTaskResult().toString();
			Toast.makeText(this, updateFailMsg, 1500).show();
			break;
		}

	}

}
