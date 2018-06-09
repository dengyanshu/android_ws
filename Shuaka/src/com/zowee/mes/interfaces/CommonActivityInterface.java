package com.zowee.mes.interfaces;

import com.zowee.mes.service.BackgroundService.Task;

/**
 * @author Administrator
 * @description 通用的activity接口
 */
public interface CommonActivityInterface {

	/**
	 * 
	 * @description 数据的初始化
	 */
	public void init();

	/**
	 * 
	 * @description UI界面的更新
	 */
	public void refresh(Task task);

}
