package com.zowee.mes.utils;

import android.app.Activity;

import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

/**
 * @author Administrator
 * @description ���ں�MES������ص�ͨ�ø�����
 */
public class MesUtils
{

	/**
	 * @param task
	 * @description MES�����Toast֪ͨ��
	 */
	public static void notifyToastMsg(Task task)
	{
		if(null==task||null==task.getActivity())
			return;
		
		BackgroundService.addTask(task);
	}
	
	/**
	 * @param target
	 * @description ��������ʧ��֪ͨ �� 
	 */
	public static void netConnFail(Activity target)
	{
		Task netConFail = new Task(target,TaskType.NET_CONNECT_FAIL,null);
		notifyToastMsg(netConFail);
	}
	
	
}
