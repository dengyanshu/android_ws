package com.zowee.mes.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.activity.MESUpdateActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.interfaces.CommonActivityInterface;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class DownLoader {

	private String downPath;
	private String storePath;
	private boolean isStop = true;// 下载是否停止 默认为是
	// private Handler handler;
	private int fileLength = -1;// 下载文件的长du
	private int curLen = -1;// 当前已下载的文件长度
	private final static String TAG = "DownLoader";
	public static final int DOWNLOADING = 2;// 处于下载状态
	public static final int DOWNLOADPAUSE = 3;// 下载处于暂停状态
	public static final int DOWNLOADSTOP = 4;// 下载处于停止状态 处于正常的停止状态 非用户指定的
	private boolean isFinished = false;// 下载是否完成
	private MESUpdateActivity activity;

	private int downState = DOWNLOADSTOP;// 默认为停止状态

	public DownLoader(String downPath, String storePath,
			MESUpdateActivity activity) {
		super();
		this.downPath = downPath;
		this.storePath = storePath;
		this.activity = activity;
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	/**
	 * 
	 * @description 文件下载处理方法
	 */
	private void downFile() {
		InputStream inStream = null;
		OutputStream outStream = null;
		HttpURLConnection conn = null;
		try {
			if (StringUtils.isEmpty(downPath) || StringUtils.isEmpty(storePath)
					|| isStop || null == activity) {
				Task notifyErrror = new Task(activity,
						TaskType.DOWNCONFIG_ERROR, null);
				BackgroundService.addTask(notifyErrror);
			}
			// Log.i(TAG, ""+downPath+ " "+storePath+" ");
			URL url = new URL(downPath);
			conn = (HttpURLConnection) url.openConnection();
			isStop = false;// 表明了 维持下载状态
			conn.setConnectTimeout(5 * 1000);
			conn.setDoInput(true);
			if (200 == conn.getResponseCode()) {
				downState = DOWNLOADING;
				fileLength = conn.getContentLength();
				if (0 >= fileLength) {
					downState = TaskType.DOWN_FAIL;
					Task downFail = new Task(activity, TaskType.DOWN_FAIL, null);
					BackgroundService.addTask(downFail);
					return;
				}
				Task downFileLen = new Task(activity, TaskType.DOWNFILE_LENGTH,
						fileLength);
				BackgroundService.addTask(downFileLen);// 通知调用者下载文件的长度
				inStream = conn.getInputStream();
				byte[] bytesItem = new byte[10 * 1024];// 缓存的大小为10K
				int len = -1;
				File file = new File(storePath);
				if (file.exists())
					file.delete();// 如果文件存在的话 删除该文件
				// outStream = new FileOutputStream(file);
				outStream = activity.openFileOutput(storePath,
						Context.MODE_WORLD_READABLE);
				while (-1 != (len = inStream.read(bytesItem))) {
					if (isStop) {
						downState = TaskType.DOWNLOADSTOP_USER;
						Task downStop = new Task(activity,
								TaskType.DOWNLOADSTOP_USER, null);
						BackgroundService.addTask(downStop);// 响应上层应用 下载停止
						break;
					}
					curLen = curLen + len;
					outStream.write(bytesItem, 0, len);
					Task downProgress = new Task(activity,
							TaskType.DOWN_PROGRESS, curLen);
					BackgroundService.addTask(downProgress);// 将当前的下载进度只会上层应用的调用者
				}
				if (DOWNLOADING == downState) {
					Log.i(TAG, "I come in");
					// isFinished = true;
					downState = DOWNLOADSTOP;// 通过正常的下载流程关闭的 一般是文件下载完成后
					Task downFinished = new Task(activity,
							TaskType.DOWNLOAD_FINISHED, curLen);
					BackgroundService.addTask(downFinished);// 通知上层 下载已完毕
				}
			}

		} catch (Exception e) {
			Log.i(TAG, e.toString());
			downState = TaskType.DOWN_FAIL;//
			Task downfail = new Task(activity, TaskType.DOWN_FAIL, null);
			BackgroundService.addTask(downfail);// 通知上层调用者 文件下载调度失败
		} finally {
			try {
				if (null != inStream)
					inStream.close();
				if (null != outStream)
					outStream.flush();
				if (null != outStream)
					outStream.close();
				if (null != conn)
					conn.disconnect();
			} catch (Exception e2) {
				// TODO: handle exception
				Log.i(TAG, e2.toString());
				downState = TaskType.RECYCLE_RESOURCE_ERROR;
				Task downState = new Task(activity,
						TaskType.RECYCLE_RESOURCE_ERROR, null);
				BackgroundService.addTask(downState);
				isStop = true;
			}
		}

	}

	public void startDown() {
		new Thread(runnable).start();

	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			downFile();
		}
	};

	/**
	 * @return
	 * @description 文件是否下载完成
	 */
	public boolean isFinished() {

		return isFinished;
	}

}
