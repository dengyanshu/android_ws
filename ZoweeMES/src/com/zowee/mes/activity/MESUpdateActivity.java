package com.zowee.mes.activity;

import java.io.File;

import com.zowee.mes.R;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.model.MESUpdateModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.update.DownLoader;
import com.zowee.mes.update.MESUpdate;
import com.zowee.mes.utils.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 * @description 软件更新界面 如果更新程序检测到更新服务器上有新的版本的话 那么会调用本程序进行软件的升级更新
 */
public class MESUpdateActivity extends Activity implements
		View.OnClickListener, CommonActivityInterface {

	private ProgressBar progressBar;
	// private TextView labSchedule;//用来显示当前的下载进度
	private Button btnCancel;// 取消下载按钮
	// private String downPath =
	// "http://gdown.baidu.com/data/wisegame/149235b7960ba954/changxiangpingjiaqi.apk";
	// private String storePath = "/data/data/files/1.apk";
	private DownLoader downLoader;
	private Button btnUpdate;
	// private static final String updateSource =
	// "http://192.168.0.27:8080/MyFirst/first.action";
	private boolean existNewEdi = true;
	private MESUpdate mesUpdate;
	private String filePath;
	private static final String TAG = "activity";

	public void init() {
		progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		// labSchedule = (TextView)this.findViewById(R.id.lab_progress);
		btnCancel = (Button) this.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		// downLoader = new DownLoader(downPath, storePath, handler,this);
		// downLoader.startDown();//
		btnUpdate = (Button) this.findViewById(R.id.btnUpdate);
		btnUpdate.setOnClickListener(this);
		Intent reqIntent = this.getIntent();
		existNewEdi = reqIntent.getBooleanExtra(MESUpdateModel.EXISTNEWEDI,
				true);
		mesUpdate = (MESUpdate) reqIntent
				.getSerializableExtra(MESUpdateModel.MESUPDATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update);
		init();

		// View.OnClickListener
	}

	// private Handler handler = new Handler()
	// {
	//
	// @Override
	// public void handleMessage(Message msg)
	// {
	// super.handleMessage(msg);
	// switch(msg.what)
	// {
	// case DownLoader.DOWN_PROGRESS:
	// int curDownLen = Integer.valueOf(msg.obj.toString());
	// progressBar.setProgress(curDownLen);
	// int curDownRate = 100*curDownLen/progressBar.getMax();
	// labSchedule.setText(getString(R.string.schedule)+" "+curDownRate+"%");
	// break;
	// case DownLoader.DOWN_FAIL://提示下载失败
	// Toast.makeText(UpdateActivity.this, R.string.download_fail, 1500).show();
	// break;
	// case DownLoader.DOWNFILE_LENGTH:
	// progressBar.setMax(Integer.valueOf(msg.obj.toString()));
	// break;
	// case DownLoader.DOWNLOAD_FINISHED:
	// Toast.makeText(UpdateActivity.this, R.string.download_finish,
	// 1500).show();
	// progressBar.setProgress(Integer.valueOf(msg.obj.toString()));
	// labSchedule.setText(getString(R.string.schedule)+" "+100+"%");
	// installApk();
	// break;
	// case DownLoader.DOWNLOADSTOP_USER:
	// Toast.makeText(UpdateActivity.this, R.string.update_cancel,
	// 1500).show();//由于采用的是非断点 删除下载的临时文件
	//
	// break;
	// }
	//
	//
	// }
	//
	// };

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			if (null != downLoader)
				downLoader.setStop(true);// 停止下载
			this.finish();
			break;
		case R.id.btnUpdate:
			if (null == mesUpdate
					|| StringUtils.isEmpty(mesUpdate.getAppName())
					|| StringUtils.isEmpty(mesUpdate.getAppUpdateURL())
					|| 0 >= mesUpdate.getAppVersion()) {
				Toast.makeText(this, R.string.updateSourceDate_error, 1500)
						.show();
				return;
			}
			filePath = "/data/data/" + this.getPackageName() + "/files/"
					+ mesUpdate.getAppName();
			downLoader = new DownLoader(mesUpdate.getAppUpdateURL(),
					mesUpdate.getAppName(), this);
			downLoader.startDown();
			break;
		}

	};

	private void installApk() {
		// if(null==mesUpdate||StringUtils.isEmpty(mesUpdate.getAppName())||StringUtils.isEmpty(mesUpdate.getAppUpdateURL())||0>=mesUpdate.getAppVersion())
		// {
		// Toast.makeText(this, R.string.updateSourceDate_error, 1500).show();
		// return;
		// }
		Intent installApk = new Intent(Intent.ACTION_VIEW);
		File file = new File(filePath);
		// file.setReadable(true);
		if (!file.exists()) {
			Toast.makeText(this, R.string.apk_notExist, 1500).show();
			return;
		}
		Log.i("DownLoader", this.getPackageName());
		installApk.setDataAndType(Uri.parse("file://" + filePath),
				"application/vnd.android.package-archive");
		startActivity(installApk);
	}

	@Override
	public void refresh(Task task) {
		switch (task.getTaskType()) {
		case TaskType.DOWN_PROGRESS:
			int curDownLen = Integer.valueOf(task.getTaskData().toString());
			progressBar.setProgress(curDownLen);
			int curDownRate = 100 * curDownLen / progressBar.getMax();
			// labSchedule.setText(getString(R.string.schedule)+" "+curDownRate+"%");
			break;
		case TaskType.DOWN_FAIL:// 提示下载失败
			Toast.makeText(MESUpdateActivity.this, R.string.download_fail, 1500)
					.show();
			delDownFiles();
			break;
		case TaskType.DOWNFILE_LENGTH:
			progressBar.setMax(Integer.valueOf(task.getTaskData().toString()));
			break;
		case TaskType.DOWNLOAD_FINISHED:
			Toast.makeText(MESUpdateActivity.this, R.string.download_finish,
					1500).show();
			progressBar.setProgress(Integer.valueOf(task.getTaskData()
					.toString()));
			// /labSchedule.setText(getString(R.string.schedule)+" "+100+"%");
			installApk();
			// delDownFiles();
			break;
		case TaskType.DOWNLOADSTOP_USER:
			Toast.makeText(MESUpdateActivity.this, R.string.update_cancel, 1500)
					.show();// 由于采用的是非断点 删除下载的临时文件
			delDownFiles();
			break;
		}

	}

	/**
	 * @description 对磁盘上的更新文件进行情理
	 */
	private void delDownFiles() {
		if (!StringUtils.isEmpty(filePath)) {
			File downFile = new File(filePath);
			if (downFile.exists())
				downFile.delete();
		}

	}

	// @Override
	// protected void onStop()
	// {
	// // TODO Auto-generated method stub
	// super.onStop();
	// // Log.i(TAG, "COME IN");
	// }

}
