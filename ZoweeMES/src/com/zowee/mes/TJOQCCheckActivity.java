package com.zowee.mes;

import java.util.HashMap;
import java.util.List;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.TjOQCcheckModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TJOQCCheckActivity extends CommonActivity implements
		View.OnClickListener, AdapterView.OnItemSelectedListener {

	private Spinner SpinTJOQCCHECKMOID;
	private EditText EditTJOQCCheckProductID;
	private EditText EditTJOQCCheckquantity;
	private EditText EditTJOQCCheckWorkFlow;
	private EditText EditTJOQCChcekBoxNumber;
	private EditText EditTJOQCCheckSN;
	private EditText EditTJOQCCheckErrorCode;
	private EditText EditScanInfo;
	private EditText EditTJOQCChcekedQty;
	private Button buttTJOQCCheckSubmit;
	private Button buttTJOQCCheckBoxPass;
	private Button buttTJOQCCheckBoxFail;
	private CheckBox checkBox_autosubmit;
	private String ResourceID = "";
	private String MOID = "";
	private String moName = "";
	private String ErrorCode = "";
	private String LotSN = "";
	// private String BoxId ;
	private String BoxLotId = "";
	private String YetSamplingQty;
	private GetMOnameModel GetMoModel;
	private TjOQCcheckModel OQCcheckmodel;
	private List<HashMap<String, String>> moNames;// ���������������Դ
	private List<HashMap<String, String>> CheckBoxResult;
	private static final String MONAME = "MOName";
	String[] Params = new String[] { "", "", "", "", "", "" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tjoqccheck);
		this.setTitleColor(Color.GREEN);
		this.setTitle("TJ-OQC���");
		init();

		logDetails(EditScanInfo, "�Զ��ύѡ��ɨ���겻��������ִ���ύ����");
		logDetails(EditScanInfo, "00000������ ��ʾ��Ʒ");
		logDetails(EditScanInfo, "�ڳ�����������빤���ſ����Զ�ѡ�񹤵���Ϣ");
		logDetails(EditScanInfo, "����˵����趨������趨��ſ�����");
		logDetails(EditScanInfo, "��ʾ��");
	}

	@Override
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.package_OQCcheckGetMOinfo;
		super.init();
		GetMoModel = new GetMOnameModel();
		OQCcheckmodel = new TjOQCcheckModel();
		SpinTJOQCCHECKMOID = (Spinner) findViewById(R.id.Spin_TJOQCCHECKMOID);
		EditTJOQCCheckProductID = (EditText) findViewById(R.id.Edit_TJOQCCheckProductID);
		EditTJOQCCheckquantity = (EditText) findViewById(R.id.Edit_TJOQCCheckquantity);
		EditTJOQCCheckWorkFlow = (EditText) findViewById(R.id.Edit_TJOQCCheckWorkFlow);
		EditTJOQCChcekBoxNumber = (EditText) findViewById(R.id.Edit_TJOQCChcekBoxNumber);
		EditTJOQCCheckSN = (EditText) findViewById(R.id.Edit_TJOQCCheckSN);
		EditTJOQCCheckErrorCode = (EditText) findViewById(R.id.Edit_TJOQCCheckErrorCode);
		EditTJOQCChcekedQty = (EditText) findViewById(R.id.Edit_TJOQCChcekedQty);
		EditScanInfo = (EditText) findViewById(R.id.edt_sysdetail);
		buttTJOQCCheckSubmit = (Button) findViewById(R.id.butt_TJOQCCheckSubmit);
		buttTJOQCCheckBoxPass = (Button) findViewById(R.id.butt_TJOQCCheckBoxPass);
		buttTJOQCCheckBoxFail = (Button) findViewById(R.id.butt_TJOQCCheckBoxFail);
		checkBox_autosubmit = (CheckBox) findViewById(R.id.checkBox_autosubmit);

		SpinTJOQCCHECKMOID.setOnItemSelectedListener(this);
		EditTJOQCChcekBoxNumber.setOnClickListener(this);
		EditTJOQCCheckSN.setOnClickListener(this);
		EditTJOQCCheckErrorCode.setOnClickListener(this);
		buttTJOQCCheckSubmit.setOnClickListener(this);
		checkBox_autosubmit.setOnClickListener(this);
		buttTJOQCCheckSubmit.setEnabled(false);
		final Builder builder = new AlertDialog.Builder(this);
		buttTJOQCCheckBoxPass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);
				builder.setTitle("����ͨ��");
				builder.setMessage("��ȷ���Ƿ�����ͨ��?");
				builder.setPositiveButton("ȷ��"
				// Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Sampling_PassFail(true);
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder.setNegativeButton("ȡ��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create().show();
			}
		});
		final Builder builder3 = new AlertDialog.Builder(this);
		buttTJOQCCheckBoxFail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder3.setIcon(R.drawable.check);
				builder3.setTitle("��������");
				builder3.setMessage("��ȷ���Ƿ���������?");
				builder3.setPositiveButton("ȷ��"
				// Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Sampling_PassFail(false);
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder3.setNegativeButton("ȡ��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder3.create().show();
			}
		});
		GetResourceId();
		getMONames();

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.Spin_TJOQCCHECKMOID:
			if (null == moNames || 0 == moNames.size())
				return;
			moName = moNames.get(arg2).get(MONAME);
			GetMOinfo(moName);
			clearSNAndErrCode();
			ClearAll();
			BoxLotId = "";
			this.EditTJOQCChcekBoxNumber.setEnabled(true);
			if (this.EditScanInfo.getLineCount() > 100)
				this.EditScanInfo.setText("");
			break;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task) {
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		super.refresh(task);
		switch (task.getTaskType()) {
		case TaskType.package_OQCcheckGetMOinfo:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				ClearAll();
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			EditTJOQCCheckSN.setText("");
			MOID = getdata.get("MOId");
			Params[2] = MOID;
			EditTJOQCCheckquantity.setText(getdata.get("OQCquantity"));
			EditTJOQCCheckProductID.setText(getdata.get("ProductID"));
			EditTJOQCCheckWorkFlow.setText(getdata.get("WorkFlow"));
			logDetails(EditScanInfo, "Success_Msg:" + getdata.toString());
			break;
		case TaskType.GetMoNameByMOSTDType:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((List<HashMap<String, String>>) task
							.getTaskResult()).size()) {
				clearWidget(SpinTJOQCCHECKMOID, null);
				if (null != moNames)
					moNames.clear();
				moName = "";
				return;
			}
			moNames = (List<HashMap<String, String>>) task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			SpinTJOQCCHECKMOID.setAdapter(adapter);
			break;

		case TaskType.GetResourceId:
			ResourceID = "";
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

			Params[0] = MyApplication.getAppOwner().toString().trim();
			Params[1] = ResourceID;
			break;
		case TaskType.package_OQCSampling_CheckBoxSN:
			super.closeProDia();
			CheckBoxResult = (List<HashMap<String, String>>) task
					.getTaskResult();
			if (super.isNull || 0 == CheckBoxResult.size()) {
				return;
			}
			for (int i = 0; i < CheckBoxResult.size(); i++) {
				if (CheckBoxResult.get(i).get("ReturnMsg") != null) {
					getdata = CheckBoxResult.get(i);
					CheckBoxResult.remove(i);
					break;
				}
			}
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {

				// BoxId = getdata.get("BoxId").toString().trim() ;
				BoxLotId = getdata.get("BoxLotId").toString().trim();
				YetSamplingQty = getdata.get("YetSamplingQty").toString()
						.trim();
				EditTJOQCChcekedQty.setText(YetSamplingQty);
				logDetails(EditScanInfo,
						"Success_Msg:"
								+ getdata.get("ReturnMsg").toString()
										.replaceFirst("ServerMessage:", "")
								+ "\r\n");
				SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
				this.EditTJOQCChcekBoxNumber.setEnabled(false);
				this.EditTJOQCCheckSN.requestFocus();
			} else {
				logDetails(EditScanInfo, getdata.get("ReturnMsg").toString()
						.replaceFirst("ServerMessage:", "")
						+ "\r\n");
				// BoxId = "";
				this.EditTJOQCChcekBoxNumber.setText("");
				BoxLotId = "";
				YetSamplingQty = "";
			}
			break;
		case TaskType.package_OQCSampling_LotSN:
			super.closeProDia();
			clearSNAndErrCode();
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {
				YetSamplingQty = getdata.get("YetSamplingQty").toString()
						.trim();
				EditTJOQCChcekedQty.setText(YetSamplingQty);
				logDetails(EditScanInfo,
						"Success_Msg:"
								+ getdata.get("ReturnMsg").toString()
										.replaceFirst("ServerMessage:", "")
								+ "\r\n");
				SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
				this.EditTJOQCCheckSN.requestFocus();
			} else {
				logDetails(EditScanInfo, getdata.get("ReturnMsg").toString()
						.replaceFirst("ServerMessage:", "")
						+ "\r\n");

			}
			break;
		case TaskType.package_OQCSampling_PassFail:
			super.closeProDia();
			if (super.isNull
					|| 0 == ((HashMap<String, String>) task.getTaskResult())
							.size()) {
				return;
			}
			getdata = (HashMap<String, String>) task.getTaskResult();
			if (Integer.parseInt(getdata.get("result").toString()) == 0) {
				this.EditTJOQCChcekBoxNumber.setEnabled(true);
				this.EditTJOQCChcekBoxNumber.setText("");
				BoxLotId = "";
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

	private void clearSNAndErrCode() {
		ErrorCode = "";
		LotSN = "";
		this.EditTJOQCCheckSN.setText("");
		this.EditTJOQCCheckErrorCode.setText("");
	}

	private void Sampling_PassFail(boolean All_Pass) {

		Params[3] = EditTJOQCCheckquantity.getText().toString().trim();
		Params[4] = BoxLotId;

		if (Params[0].equalsIgnoreCase("system")) {

			logDetails(EditScanInfo, "�����趨��Դ����");
			return;
		}
		if (Params[1].trim().isEmpty()) {

			logDetails(EditScanInfo, "��ȷ����Դ�����Ƿ���ȷ");
			return;
		}
		if (Params[2].isEmpty()) {

			logDetails(EditScanInfo, "���Ȼ�ȡ����");
			return;
		}
		if (Params[4].isEmpty()) {

			logDetails(EditScanInfo, "����������ţ���MES��֤OK����ɨ������");
			return;
		}
		super.progressDialog.setMessage("��������");
		Params[5] = "false";
		if (All_Pass) {
			Params[5] = "true";
			super.progressDialog.setMessage("����ͨ��");
		}
		super.showProDia();
		Task task = new Task(this, TaskType.package_OQCSampling_PassFail,
				Params);
		OQCcheckmodel.TjOQCSampling_PassFail(task);

	}

	private void OQCSampling_LotSN() {

		Params[3] = LotSN;
		Params[4] = BoxLotId;
		Params[5] = ErrorCode;
		if (Params[0].equalsIgnoreCase("system")) {

			logDetails(EditScanInfo, "�����趨��Դ����");
			return;
		}
		if (Params[1].trim().isEmpty()) {

			logDetails(EditScanInfo, "��ȷ����Դ�����Ƿ���ȷ");
			return;
		}
		if (Params[2].isEmpty()) {

			logDetails(EditScanInfo, "���Ȼ�ȡ����");
			return;
		}
		if (Params[3].isEmpty()) {

			logDetails(EditScanInfo, "���Ų���Ϊ��");
			return;
		}
		if (Params[4].isEmpty()) {

			logDetails(EditScanInfo, "����������ţ���MES��֤OK����ɨ������");
			return;
		}
		if (Params[5].isEmpty()) {

			logDetails(EditScanInfo, "�������벻��Ϊ��");
			return;
		}
		super.progressDialog.setMessage("�������");
		super.showProDia();
		Task task = new Task(this, TaskType.package_OQCSampling_LotSN, Params);
		OQCcheckmodel.TjOQCSampling_LotSN(task);
	}

	private void StartSampling_CheckBoxSN(String BoxSN) {
		Params[3] = BoxSN;
		if (Params[0].equalsIgnoreCase("system")) {
			logDetails(EditScanInfo, "�����趨��Դ����");
			return;
		}
		if (Params[1].trim().isEmpty()) {

			logDetails(EditScanInfo, "��ȷ����Դ�����Ƿ���ȷ");
			return;
		}
		if (Params[2].isEmpty()) {

			logDetails(EditScanInfo, "���Ȼ�ȡ����");
			return;
		}
		super.progressDialog.setMessage("��ż��");
		super.showProDia();
		Task task = new Task(this, TaskType.package_OQCSampling_CheckBoxSN,
				Params);
		OQCcheckmodel.TjOQCBoxcheck(task);
	}

	private void ClearAll() {
		EditTJOQCCheckSN.setText("");
		MOID = "";
		EditTJOQCCheckquantity.setText("");
		EditTJOQCCheckProductID.setText("");
		EditTJOQCCheckWorkFlow.setText("");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.Edit_TJOQCChcekBoxNumber:
			String boxSN = EditTJOQCChcekBoxNumber.getText().toString().trim();
			if (boxSN.length() < 8) {
				EditTJOQCChcekBoxNumber.setText("");
				return;
			}
			EditTJOQCChcekBoxNumber.setText(boxSN.toUpperCase());
			StartSampling_CheckBoxSN(boxSN);
			break;
		case R.id.Edit_TJOQCCheckSN:
			if (EditTJOQCCheckSN.getText().toString().trim().length() < 8) {
				EditTJOQCCheckSN.setText("");
				LotSN = "";
				return;
			}
			if (StringUtils.isScannedMONumber(LotSN)) {
				String GetItem;
				for (int i = 0; i < this.SpinTJOQCCHECKMOID.getAdapter()
						.getCount(); i++) {
					GetItem = ((HashMap<String, String>) (this.SpinTJOQCCHECKMOID
							.getAdapter().getItem(i))).get(MONAME).toString();
					if (GetItem.equals(LotSN)) {
						SpinTJOQCCHECKMOID.setSelection(i);
						break;
					}
					if (i == SpinTJOQCCHECKMOID.getAdapter().getCount() - 1)// û���ҵ�ɨ��Ĺ����š�
					{
						Toast.makeText(this, "��ȷ������Ĺ������Ƿ���ȷ", 5).show();
					}
				}
				EditTJOQCCheckSN.setText("");
			} else {
				LotSN = EditTJOQCCheckSN.getText().toString().trim()
						.toUpperCase();
				EditTJOQCCheckSN.setText(LotSN);
				this.EditTJOQCCheckErrorCode.requestFocus();
				if (this.EditScanInfo.getLineCount() > 100)
					this.EditScanInfo.setText("");
			}

			break;
		case R.id.Edit_TJOQCCheckErrorCode:
			if (EditTJOQCCheckErrorCode.getText().toString().trim().length() < 5) {
				EditTJOQCCheckErrorCode.setText("");
				ErrorCode = "";
				return;
			}
			ErrorCode = EditTJOQCCheckErrorCode.getText().toString().trim()
					.toUpperCase();
			EditTJOQCCheckErrorCode.setText(ErrorCode);
			if (checkBox_autosubmit.isChecked())
				OQCSampling_LotSN();
			else
				this.buttTJOQCCheckSubmit.requestFocus();
			break;
		case R.id.butt_TJOQCCheckSubmit:
			OQCSampling_LotSN();
			break;
		case R.id.checkBox_autosubmit:
			if (checkBox_autosubmit.isChecked())
				buttTJOQCCheckSubmit.setEnabled(false);
			else
				buttTJOQCCheckSubmit.setEnabled(true);

		}
	}

	private void GetResourceId() {
		super.progressDialog.setMessage("��ȡ��ԴID");
		super.showProDia();
		Task task = new Task(this, TaskType.GetResourceId, MyApplication
				.getAppOwner().toString().trim());
		GetMoModel.GetResourceId(task);
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
		Task task = new Task(this, TaskType.package_OQCcheckGetMOinfo, MOName);
		OQCcheckmodel.GetMOinfo(task);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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
		case R.id.action_settings:
			this.EditTJOQCChcekBoxNumber.setEnabled(true);
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
				.setTitle("ȷ���˳�OQC�����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(TJOQCCheckActivity.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

}
