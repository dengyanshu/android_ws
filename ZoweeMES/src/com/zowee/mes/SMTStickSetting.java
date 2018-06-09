package com.zowee.mes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.zowee.mes.app.MyApplication;

public class SMTStickSetting extends Activity implements View.OnClickListener {

	private EditText edit_owner;
	private EditText edit_scan_c;
	private EditText edit_scan_to;
	// private EditText edit_timeout;
	private EditText edit_rescan;

	private CheckBox checkbox_addEnter;

	private Button buttonSave;
	private Button buttonCancel;

	private String owner;
	private String scan_command;
	private String scan_to_command;
	// private String timeout;
	private int rescan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtsc_setting);

		initView();

		setDefaultValue();
		updateUI();
	}

	private void initView() {
		edit_owner = (EditText) findViewById(R.id.edit_owner);
		edit_scan_c = (EditText) findViewById(R.id.edit_scan_c);
		edit_scan_to = (EditText) findViewById(R.id.edit_timeout_c);
		// edit_timeout = (EditText)findViewById(R.id.edit_timeout);
		edit_rescan = (EditText) findViewById(R.id.edit_rescan);

		buttonSave = (Button) findViewById(R.id.button_ok1);
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		checkbox_addEnter = (CheckBox) findViewById(R.id.check_showInfo);
		checkbox_addEnter.setChecked(getBoolean("addEnter"));

		buttonSave.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);

	}

	public void setDefaultValue() {
		SharedPreferences pref = getSharedPreferences("smtstick_setting",
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();

		if (!pref.contains("setting")) { // 第一次运行，初始化默认值
			editor.putBoolean("setting", false);
			// editor.putBoolean("port_rebinding", false);
			editor.putString("owner", SMTStick.OWNER);
			MyApplication.setAppOwner(SMTStick.OWNER); // add ybj 20130906
			editor.putString("scan_command", SMTStick.SCAN_COMMAND);
			editor.putString("scan_to_command", SMTStick.SCAN_TIMEOUT_COMMAND);
			editor.putInt("rescan", SMTStick.SCAN_RESCAN);
			editor.commit();
			Log.i("SMTStickSetting", "editor commit");
		}
	}

	public void saveSetting() {
		SharedPreferences pref = MyApplication.getApp().getSharedPreferences(
				"smtstick_setting", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.clear();

		editor.putBoolean("setting", true);
		// editor.putBoolean("port_rebinding", false);
		editor.putString("owner", owner);
		MyApplication.setAppOwner(owner); // add ybj 20130906
		editor.putString("scan_command", scan_command);
		editor.putString("scan_to_command", scan_to_command);
		editor.putInt("rescan", rescan);
		if (checkbox_addEnter.isChecked()) {
			editor.putBoolean("addEnter", true);
		} else {
			editor.putBoolean("addEnter", false);
		}

		editor.commit();
	}

	public boolean getBoolean(String key) {
		SharedPreferences p = MyApplication.getApp().getSharedPreferences(
				"smtstick_setting", Context.MODE_PRIVATE);
		return p.getBoolean(key, true);
	}

	public String getSetting(String key) {
		SharedPreferences p = MyApplication.getApp().getSharedPreferences(
				"smtstick_setting", Context.MODE_PRIVATE);
		return p.getString(key, "");
	}

	public int getIntSet(String key) {
		SharedPreferences pref = MyApplication.getApp().getSharedPreferences(
				"smtstick_setting", Context.MODE_PRIVATE);
		return pref.getInt(key, 3);
	}

	public void getSPValues() {

		owner = getSetting("owner");
		scan_command = getSetting("scan_command");
		scan_to_command = getSetting("scan_to_command");
		// timeout = pref.getString("timeout", "");
		rescan = getIntSet("rescan");

	}

	public void updateUI() {
		getSPValues();

		edit_owner.setText(owner);
		edit_rescan.setText(rescan + "");
		edit_scan_c.setText(scan_command);
		edit_scan_to.setText(scan_to_command);
		// edit_timeout.setText(timeout+"");
	}

	private void getEditValues() {
		owner = edit_owner.getText().toString().trim();
		scan_command = edit_scan_c.getText().toString().trim();
		scan_to_command = edit_scan_to.getText().toString().trim();
		// timeout= edit_timeout.getText().toString().trim();
		String rs = edit_rescan.getText().toString().trim();
		if (rs == null) {
			rs = "0";
		}
		rescan = Integer.parseInt(rs);
		if (rescan < 0)
			rescan = 0;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_ok1:
			if (edit_owner.getText().length() < 1) {
				new AlertDialog.Builder(this).setTitle("使用者为空")
						.setMessage("使用者不能为空").setNegativeButton("确定", null);
			} else {
				getEditValues();
				saveSetting();
				updateUI();
				this.finish();
			}
			break;

		case R.id.button_cancel:
			this.finish();
			break;
		}
	}

}