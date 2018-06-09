package com.zowee.mes.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.SMTStorage;
import com.zowee.mes.model.RemoveMateriModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;
/**
 * @author Administrator
 * @description 卸换料卷
 */
public class RemoveMateriActivity extends CommonActivity  implements View.OnClickListener
{
	private EditText edtStaLotSN;
	private EditText edtSysDetails;
	private RemoveMateriModel model;
	private View btnGroups;
	private Button btnScan;
	private Button btnCancel;
	@Override
	public void init()
	{
		super.commonActivity = this;
		super.TASKTYPE = TaskType.REMOVEMATERI_SCAN;
		super.init();
		model = new RemoveMateriModel();

		btnGroups = this.findViewById(R.id.btnGroups);
		btnGroups.findViewById(R.id.btn_refreshData).setVisibility(View.GONE);
		btnGroups.findViewById(R.id.btn_ensure).setVisibility(View.GONE);

		btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);		
		btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);


		this.edtStaLotSN = (EditText)this.findViewById(R.id.edt_slot_barcode);
		this.edtSysDetails = (EditText)this.findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);

		btnScan.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		this.edtStaLotSN.setOnClickListener(this);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.remove_material);
		init();

	}

	@Override
	public void refresh(Task task) 
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
		case TaskType.REMOVEMATERI_SCAN:
			if(super.isNull)  return;
			String staLotSN = task.getTaskResult().toString().trim();
			this.edtStaLotSN.setText(staLotSN.toUpperCase());
			refreshData(staLotSN);
			break;
		case TaskType.REMOVEMATERI:
			closeProDia();
			this.edtStaLotSN.selectAll();
			if(super.isNull) return;
			boolean result=false;
			String[] reses = (String[])task.getTaskResult();
			StringBuffer sbf = new StringBuffer();
			String retRes = reses[0];
			String retMsg = reses[1];
			if(!StringUtils.isEmpty(retRes)&&"0".equals(retRes.trim())){
				sbf.append(getString(R.string.removeMateriSucc)+"\n");
				result= true;
			}
			else {
				sbf.append(getString(R.string.removeMateriFail)+"\n");
				result= false;
			}
			if(StringUtils.isEmpty(retMsg))
				retMsg = "";
			sbf.append(retMsg+"\n");
			addImforToEdtSysDetai(sbf.toString(),result);
			break;
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.btn_cancel:
			super.clearWidget(edtStaLotSN, null);
			break;
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.edt_slot_barcode:
			if(StringUtils.isScannedSlotNumber(edtStaLotSN.getText().toString().trim()))
				refreshData(edtStaLotSN.getText().toString().trim());
			else
			{
				edtStaLotSN.setText("");
				Toast.makeText(this, "槽位条码不正确", 5).show();
			}
			break;
		}

	}

	private void refreshData(String staLotSN)
	{
		if(!StringUtils.isScannedSlotNumber(staLotSN.toUpperCase()))
		{
			Toast.makeText(this, R.string.error_staLot, 1000).show();
			edtStaLotSN.setText("");
			return;
		}
		progressDialog.setMessage(getString(R.string.TransationData));
		showProDia();
		Task removeMateri = new Task(this,TaskType.REMOVEMATERI,staLotSN);
		model.removeMateri(removeMateri);

	}	
	private void addImforToEdtSysDetai(String str,boolean success)
	{
		CharacterStyle ssStyle=null;
		if(success){
			ssStyle=new ForegroundColorSpan(Color.GREEN);
		}else{
			ssStyle=new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog="["+df.format(new Date())+"]"+str+"\n";		
		SpannableStringBuilder ssBuilder=new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(this.edtSysDetails.getText());
		edtSysDetails.setText(ssBuilder);	
	}
	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("确定退出卸换料卷吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(RemoveMateriActivity.this,
						BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no), null).show();
	}

}
