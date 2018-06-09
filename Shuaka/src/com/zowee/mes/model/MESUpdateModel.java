package com.zowee.mes.model;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.update.MESUpdate;
import com.zowee.mes.update.MesUpdateParse;

/**
 * @author Administrator
 * @description 更新的业务逻辑类
 */
public class MESUpdateModel extends CommonModel {

	public static final String EXISTNEWEDI = "existNewEdi";// 是否有更新的版本
	public static final String MESUPDATE = "mesUpdate";// 更新版本的信息

	/**
	 * 获取更新服务器上 MES-PDA的最新版本信息
	 */
	public MESUpdate getLastestMesUpdate() throws Exception {
		// String updateSource = MesUpdateParse.getLocalUpdateSource();
		String updateSource = MesUpdateParse.getUserDefUpdateSource();
		InputStream inStream = null;
		Log.i(TAG, updateSource + "");
		URL url = new URL(updateSource);//
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		if (200 == conn.getResponseCode())
			inStream = conn.getInputStream();
		else
			return null;
		MESUpdate lastestEdition = MesUpdateParse
				.getParseMesUpdateObj(inStream);

		return lastestEdition;
	}

	/**
	 * @return
	 * @description 是否存在新的版本
	 */
	public void existNewEdition(Task task) {
		if (null == task || null == task.getActivity())
			return;
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				try {
					Object[] reses = new Object[2];
					boolean existNewEdi = false;
					MESUpdate mesUpdate = getLastestMesUpdate();
					int curAppVersion = MyApplication.getCurAppVersion();
					if (null == mesUpdate || 0 >= curAppVersion)
						existNewEdi = false;
					existNewEdi = (mesUpdate.getAppVersion() > curAppVersion);
					reses[0] = existNewEdi;
					reses[1] = mesUpdate;
					task.setTaskResult(reses);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
					e.printStackTrace();
					task.setTaskOperator(null);
					task.setTaskType(TaskType.MES_CHECKUPDATE_FAIL);
					task.setTaskResult(task.getActivity().getString(
							R.string.mesupdate_fail));
					notifyUpdateFail(task);
				}

				return task;
			}
		};
		task.setTaskOperator(taskOperator);
		BackgroundService.addTask(task);
	}

	/**
	 * 通知更新失败
	 */
	private void notifyUpdateFail(Task task) {
		if (null == task || null == task.getActivity()
				|| null == task.getTaskResult())
			return;
		BackgroundService.addTask(task);

	}

}
