package com.zowee.mes;

import com.zowee.mes.service.BackgroundService;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;

public class SMTScanMaterial extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smtscan_material);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smtscan_material, menu);
		return true;
	}
    @Override
    public void onBackPressed() {
    	killMainProcess();
    }
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("确定退出SMT物料扫描吗?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(SMTScanMaterial.this,BackgroundService.class));
								//android.os.Process.killProcess(android.os.Process.myPid());
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no),null).show();
	}

}
