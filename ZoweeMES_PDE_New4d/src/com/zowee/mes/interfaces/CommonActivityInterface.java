package com.zowee.mes.interfaces;

import com.zowee.mes.service.BackgroundService.Task;

/**
 * @author Administrator
 * @description ͨ�õ�activity�ӿ�
 */
public interface CommonActivityInterface 
{
	
	/**
	 * 
	 * @description ���ݵĳ�ʼ�� 
	 */
	public abstract void init();
	
	/**
	 * 
	 * @description  UI����ĸ��� 
	 */
	public abstract void refresh(Task task);
	
	
}
