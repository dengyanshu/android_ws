package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.TjeyecheckModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EyeCheckActivity extends CommonActivity implements
		View.OnClickListener, AdapterView.OnItemSelectedListener {

	private Spinner SpinEyecheckMOID;
	private EditText EditEyecheckProductID;
	private EditText EditeyeCheckWorkFlow;
	private EditText EditeyeCheckErrorCode;
	private EditText EditeyeCheckStartSN;
	private EditText Edieyecheckquantity;
	private Button butteyecheckSubmit;
	private Button butteyecheckCancel;

	private EditText EditScanInfo;
	private String msg;
	private GetMOnameModel GetMoModel;
	private TjeyecheckModel eyecheckmodel;
	private String ResourceID;
	private String MOID;
	private String moName;
	private List<HashMap<String, String>> moNames;// ���������������Դ
	private static final String MONAME = "MOName";
	private String LotSN;
	String[] Params = new String[] { "", "", "", "", "", "", "", "", "" };

	private String PreIsErrCode = "false";
	private String PreErrCodeId = "";
	private String PreErrCodeName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eye_check);
		this.setTitleColor(Color.GREEN);
		this.setTitle("TJ-Ŀ��");
		init();

		logDetails(EditScanInfo, "����ȷ����ɨ�費�������в���������ȷ����ɨ�費����Ʒ���ţ�");
		logDetails(EditScanInfo, "ɨ�蹤���ſ����Զ�ѡ�񹤵���Ϣ");
		logDetails(EditScanInfo, "��������Ʒ����ֱ��ɨ������");
		logDetails(EditScanInfo, "����ǲ���Ʒ������ɨ�費�����룬��ɨ������");
		logDetails(EditScanInfo, "��ʾ��");
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.package_eyecheckGetMOinfo;
		super.init();
		msg = "Ŀ��-��ȡ������Ϣ";
		super.progressDialog.setMessage(msg);
		GetMoModel = new GetMOnameModel();
		eyecheckmodel = new TjeyecheckModel();
		SpinEyecheckMOID = (Spinner) findViewById(R.id.Spin_EyecheckMOID);
		EditEyecheckProductID = (EditText) findViewById(R.id.Edit_EyecheckProductID);
		EditeyeCheckWorkFlow = (EditText) findViewById(R.id.Edit_eyeCheckWorkFlow);
		EditeyeCheckErrorCode = (EditText) findViewById(R.id.Edit_eyeCheckErrorCode);
		EditeyeCheckStartSN = (EditText) findViewById(R.id.Edit_eyeCheckStartSN);
		butteyecheckSubmit = (Button) findViewById(R.id.butt_eyecheckSubmit);
		butteyecheckCancel = (Button) findViewById(R.id.butt_eyecheckCancel);
		EditScanInfo = (EditText) findViewById(R.id.edt_sysdetail);
		Edieyecheckquantity = (EditText) findViewById(R.id.Edit_eyecheckquantity);

		SpinEyecheckMOID.setOnItemSelectedListener(this);
		EditeyeCheckStartSN.setOnClickListener(this);
		EditScanInfo.setOnClickListener(this);
		butteyecheckSubmit.setOnClickListener(this);
		butteyecheckCancel.setOnClickListener(this);

		GetResourceId();
		getMONames();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smtstick, menu);
		return true;
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
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.Spin_EyecheckMOID:
			if (null == moNames || 0 == moNames.size())
				return;
			moName = moNames.get(arg2).get(MONAME);
			GetMOinfo(moName);
			ClearErrorCode();
			if (this.EditScanInfo.getLineCount() > 100)
				this.EditScanInfo.setText("");
			break;
		}

	}

	@Override
	public void refresh(Task task) {
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.GetMoNameByMOSTDType:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((List<HashMap<String, String>>) task
							.getTaskResult()).size()) {
				clearWidget(SpinEyecheckMOID, null);
				if (null != moNames)
					moNames.clear();
				moName = "";
				return;
			}
			moNames = (List<HashMap<String, String>>) task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			SpinEyecheckMOID.setAdapter(adapter);
			break;

		case TaskType.GetResourceId:
			ResourceID = "";
			@SuppressWarnings("unchecked")
			List<HashMap<String, String>> getresult = (List<HashMap<String, String>>) task
					.getTaskResult();
			if (super.isNull || 0 == (getresult).size()) {
				logDetails(EditScanInfo, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
				return;
			}
			getresult = (List<HashMap<String, String>>) task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			if (ResourceID.isEmpty())
				logDetails(EditScanInfo, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
			break;
		case TaskType.package_eyecheckGetMOinfo:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				ClearAll();
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			EditeyeCheckStartSN.setText("");
			MOID = getdata.get("MOId");
			Edieyecheckquantity.setText(getdata.get("quantity"));
			EditEyecheckProductID.setText(getdata.get("ProductID"));
			EditeyeCheckWorkFlow.setText(getdata.get("WorkFlow"));
			logDetails(EditScanInfo, "Success_Msg:" + getdata.toString());
			break;
		case TaskType.package_eyecheckscan:
			super.closeProDia();
			EditeyeCheckStartSN.setText("");
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				ClearErrorCode();
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {
				if (getdata.get("ErrCode").toString().trim()
						.equalsIgnoreCase("true")) {
					PreIsErrCode = "true";
					PreErrCodeId = getdata.get("ErrCodeId").toString().trim();
					PreErrCodeName = getdata.get("ErrCodeNote").toString()
							.trim();
					this.EditeyeCheckErrorCode.setText(LotSN + ":"
							+ PreErrCodeName);
				} else {
					ClearErrorCode();
				}
				logDetails(EditScanInfo,
						"Success_Msg:"
								+ getdata.get("ReturnMsg").toString()
										.replaceFirst("ServerMessage:", "")
								+ "\r\n");
				SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
			} else {
				logDetails(EditScanInfo, getdata.get("ReturnMsg").toString()
						.replaceFirst("ServerMessage:", "")
						+ "\r\n");
			}
			break;

		}
	}

	private void ClearAll() {
		MOID = "";
		EditeyeCheckStartSN.setText("");
		EditEyecheckProductID.setText("");
		EditeyeCheckWorkFlow.setText("");
		Edieyecheckquantity.setText("");
	}

	private void ClearErrorCode() {
		PreIsErrCode = "false";
		PreErrCodeId = "";
		PreErrCodeName = "";
		this.EditeyeCheckErrorCode.setText("");
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void getMONames() {
		super.progressDialog.setMessage("��ȡ������");
		super.showProDia();
		String MOSTDType = "";
		Task task = new Task(this, TaskType.GetMoNameByMOSTDType, MOSTDType);
		GetMoModel.getMoNamesByMOSTdType(task);
	}

	private void GetMOinfo(String MOName) {
		super.progressDialog.setMessage("��ȡ������Ϣ");
		super.showProDia();
		Task task = new Task(this, TaskType.package_eyecheckGetMOinfo, MOName);
		eyecheckmodel.GetMOinfo(task);
	}

	private void GetResourceId() {
		super.progressDialog.setMessage("��ȡ��ԴID");
		super.showProDia();
		Task task = new Task(this, TaskType.GetResourceId, MyApplication
				.getAppOwner().toString().trim());
		GetMoModel.GetResourceId(task);
	}

	private void Starteyecheck(String lotSN) {

		Params[0] = MOID;
		Params[1] = lotSN;
		Params[2] = MyApplication.getAppOwner().toString().trim();
		Params[3] = ResourceID;
		Params[4] = PreIsErrCode;
		Params[5] = PreErrCodeId;
		Params[6] = PreErrCodeName;

		if (Params[2].equalsIgnoreCase("system")) {

			logDetails(EditScanInfo, "�����趨��Դ����");
			return;
		}
		if (Params[0].trim().isEmpty()) {

			logDetails(EditScanInfo, "���Ȼ�ȡ������Ϣ");
			return;
		}
		if (Params[3].isEmpty()) {

			logDetails(EditScanInfo, "��ȷ����Դ�����Ƿ���ȷ");
			return;
		}
		super.progressDialog.setMessage("Ŀ��ɨ��");
		super.showProDia();

		Task task = new Task(this, TaskType.package_eyecheckscan, Params);
		eyecheckmodel.Tjeyecheck(task);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.butt_eyecheckCancel:
			ClearErrorCode();
			break;
		case R.id.butt_eyecheckSubmit:

		case R.id.Edit_eyeCheckStartSN:
			LotSN = EditeyeCheckStartSN.getText().toString().trim()
					.toUpperCase();
			if (LotSN.trim().length() < 5) {
				return;
			}
			if (StringUtils.isScannedMONumber(LotSN)) {
				String GetItem;
				for (int i = 0; i < SpinEyecheckMOID.getAdapter().getCount(); i++) {
					GetItem = ((HashMap<String, String>) (this.SpinEyecheckMOID
							.getAdapter().getItem(i))).get(MONAME).toString();
					if (GetItem.equals(LotSN)) {
						SpinEyecheckMOID.setSelection(i);
						break;
					}
					if (i == SpinEyecheckMOID.getAdapter().getCount() - 1)// û���ҵ�ɨ��Ĺ����š�
					{
						Toast.makeText(this, "��ȷ������Ĺ������Ƿ���ȷ", 5).show();
					}
				}
				EditeyeCheckStartSN.setText("");
			} else {
				Starteyecheck(LotSN);
				if (this.EditScanInfo.getLineCount() > 100)
					this.EditScanInfo.setText("");
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("ȷ���˳�Ŀ����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(EyeCheckActivity.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

}
