package com.zowee.mes.utils;

import android.app.Activity;

import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

/**
 * @author Administrator
 * @description 用于和MES程序相关的通用辅助类
 */
public class MesUtils
{

	/**
	 * @param task
	 * @description MES程序的Toast通知类
	 */
	public static void notifyToastMsg(Task task)
	{
		if(null==task||null==task.getActivity())
			return;
		
		BackgroundService.addTask(task);
	}
	
	/**
	 * @param target
	 * @description 网络连接失败通知 ！ 
	 */
	public static void netConnFail(Activity target)
	{
		Task netConFail = new Task(target,TaskType.NET_CONNECT_FAIL,null);
		notifyToastMsg(netConFail);
	}
	
	
}
