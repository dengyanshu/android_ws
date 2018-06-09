package com.zowee.mes.test;

import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.test.AndroidTestCase;
import android.util.Log;
/**
 * @author Administrator
 * @description 后台服务测试
 */
public class BackgroundServiceTest extends AndroidTestCase 
{

	private static final String TAG = "BackgroundTest";
	
	public void testIsEqual()throws Exception
	{
		Task task = new Task(null,TaskType.REFLESHUI,null);
	//	Task task1 = new Task(null,TaskType.TEST,null);
//		Log.i(TAG,""+(task==task1));
//		Log.i(TAG,""+task.getTaskType());
//		Log.i(TAG,""+task1.getTaskType());
	}
	
	
	
	
}
