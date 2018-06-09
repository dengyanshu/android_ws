package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.DIPStartModel;
import com.zowee.mes.model.TJBoxSNCheckModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BoxLotSNCheckActivity extends CommonActivity implements
		View.OnClickListener {

	private EditText EditTjBoxLotSNCheckBoxNumber;
	private EditText EditTjBoxLotSNCheckSN;
	private EditText EditScanInfo;
	private Button buttTjBoxLotSNCheckClear;
	private String msg;
	private TJBoxSNCheckModel BoxSNCheckModel;
	private String BoxSN;
	private String LotSN;
	String[] Params = new String[] { "", "" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_box_lot_sncheck);
		this.setTitle("箱号与产品SN一致性检查");
		this.setTitleColor(Color.GREEN);

		init();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task) {
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		super.refresh(task);
		switch (task.getTaskType()) {

		case TaskType.package_TjBoxLotSNCheck:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {
				logDetails(EditScanInfo,
						"Success_Msg:"
								+ getdata.get("ReturnMsg").toString()
										.replaceFirst("ServerMessage:", "")
								+ "\r\n");
				SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
			} else {

				this.EditTjBoxLotSNCheckBoxNumber.setEnabled(true);
				EditTjBoxLotSNCheckBoxNumber.setText("");
				logDetails(EditScanInfo, getdata.get("ReturnMsg").toString());
				EditTjBoxLotSNCheckBoxNumber.requestFocus();
			}
			this.EditTjBoxLotSNCheckSN.setText("");
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smtstick, menu);
		return true;
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.package_TjBoxLotSNCheck;
		super.init();
		msg = "箱号及产品SN检查";
		super.progressDialog.setMessage(msg);
		BoxSNCheckModel = new TJBoxSNCheckModel();

		EditTjBoxLotSNCheckBoxNumber = (EditText) findViewById(R.id.Edit_TjBoxLotSNCheckBoxNumber);
		EditTjBoxLotSNCheckSN = (EditText) findViewById(R.id.Edit_TjBoxLotSNCheckSN);
		EditScanInfo = (EditText) findViewById(R.id.edt_sysdetail);
		buttTjBoxLotSNCheckClear = (Button) findViewById(R.id.butt_TjBoxLotSNCheckClear);

		EditTjBoxLotSNCheckBoxNumber.setOnClickListener(this);
		EditTjBoxLotSNCheckSN.setOnClickListener(this);
		buttTjBoxLotSNCheckClear.setOnClickListener(this);
	}

	private void StartDIPLotSN(String lotSN) {
		Params[0] = BoxSN;
		Params[1] = lotSN;

		super.progressDialog.setMessage(msg);
		super.showProDia();
		Task task = new Task(this, TaskType.package_TjBoxLotSNCheck, Params);
		BoxSNCheckModel.TjBoxSNCheck(task);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.Edit_TjBoxLotSNCheckBoxNumber:
			BoxSN = EditTjBoxLotSNCheckBoxNumber.getText().toString().trim()
					.toUpperCase();
			if (BoxSN.length() < 8) {

				logDetails(EditScanInfo, "箱号条码小于8位");
				Toast.makeText(this, "箱号条码小于8位 ", 5).show();
				BoxSN = "";
				EditTjBoxLotSNCheckSN.setText("");
				return;
			}
			EditTjBoxLotSNCheckBoxNumber.setText(BoxSN);
			EditTjBoxLotSNCheckBoxNumber.setEnabled(false);
			EditTjBoxLotSNCheckSN.requestFocus();
			break;
		case R.id.Edit_TjBoxLotSNCheckSN:
			if (this.EditScanInfo.getLineCount() > 100)
				this.EditScanInfo.setText("");
			LotSN = EditTjBoxLotSNCheckSN.getText().toString().trim()
					.toUpperCase();
			if (BoxSN.isEmpty()) {
				logDetails(EditScanInfo, "请先扫描箱号");
				Toast.makeText(this, "请先扫描箱号", 5).show();
				LotSN = "";
				EditTjBoxLotSNCheckSN.setText("");
			}
			if (LotSN.length() < 8) {

				logDetails(EditScanInfo, "产品条码小于8位");
				Toast.makeText(this, "产品条码小于8位 ", 5).show();
				LotSN = "";
				EditTjBoxLotSNCheckSN.setText("");
				return;
			}
			EditTjBoxLotSNCheckSN.setText(LotSN);
			StartDIPLotSN(LotSN);

			break;
		case R.id.butt_TjBoxLotSNCheckClear:
			EditTjBoxLotSNCheckBoxNumber.setText("");
			EditTjBoxLotSNCheckBoxNumber.setEnabled(true);
			EditTjBoxLotSNCheckSN.setText("");
			EditTjBoxLotSNCheckSN.requestFocus();
			break;

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_clear:
			EditScanInfo.setText("");
			break;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("确定退出箱号与产品SN一致性检查吗?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(
										BoxLotSNCheckActivity.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

}
