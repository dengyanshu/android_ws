package com.zowee.mes.activity;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * @author Administrator
 * @description 物料收料
 */
public class MaterialRewindActivity extends Activity {

	private LaserScanOperator laserScanOperator;

	public void init() {
		// super.TASKTYPE = TaskType.MaterialRewind;
		// super.commonActivity = this;
		// super.init();
		// laserScanOperator = LaserScanOperator.getLaserScanOperator(this,2);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.material_rewind);
		init();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case MyApplication.SCAN_KEYCODE:
			Toast.makeText(this, "qid" + laserScanOperator.getLaserLisState(),
					1000).show();
			if (laserScanOperator.getLaserLisState())
				// laserScanOperator.startLaserScan();
				// else
				// laserScanOperator.laserInitFailNotify();
				//
				break;
		}

		return super.onKeyDown(keyCode, event);
	}

	//
	// @Override
	// protected void onResume()
	// {
	// super.onResume();
	// }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// LaserScanOperator.closeLaserScanOperator();
	}

}
