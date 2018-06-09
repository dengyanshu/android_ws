package com.zowee.mes.utils;

import java.io.Serializable;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author Administrator
 * @description android 屏幕显示设备的辅助类
 */

public class DisplayDeviceUtils 
{

	/**
	 * @param windowManager
	 * @return
	 * @description  获取显示设备的信息
	 */
	public static DisplayMetrics  getDisplayMetrics(Context context)
	{
		if(null==context) return null;
		
		return context.getResources().getDisplayMetrics();
	}
	
	/**
	 * @author Administrator
	 * @description  设备的显示信息
	 */
	public static class DisplayInfor implements Serializable
	{
		public DisplayMetrics displayMetrics;
		public int widthPx;
		public int widthDp;
		public int widthSp;
		public int heightPx;
		public int heightDp;
		public int heightSp;
		
	}
	
	/**
	* dp -> px
	*/
	public static float dpToPx(Context context, float dpValue) 
	{
		if(null==context||0>=dpValue) return -1;
		float scale = getDisplayMetrics(context).density;
		
		return (dpValue * scale + 0.5f);
	}

	/**
	* px -> dp
	*/
	public static float pxToDp(Context context, float pxValue)
	{
		if(null==context||0>=pxValue) return -1;
		float scale = getDisplayMetrics(context).density;
		
		return  (pxValue / scale + 0.5f);
	}
	
	/**
	 * px -> sp
	 */
	public static float pxToSp(float pxValue,float fontScale) 
	{
		
		  return (pxValue/fontScale + 0.5f);
	}

	/**
	 * @description  sp -> px 
	 */
	public static float spToPx(float spValue,float fontScale)
	{
		
		return (spValue*fontScale+0.5f);
	}
	
	/**
	 * @param context
	 * @return
	 * @description  获取显示设备的信息 
	 */
	public static DisplayInfor  getDisplayInfor(Context context)
	{
		if(null==context) return null;
		DisplayInfor displayInfor = new DisplayInfor();
		DisplayMetrics displayMetrics = getDisplayMetrics(context);
		int widthPx = displayMetrics.widthPixels;
		int heightPx = displayMetrics.heightPixels;
		int widthDp = (int)pxToDp(context, widthPx);
		int heightDp = (int)pxToDp(context, heightPx);
		
		displayInfor.displayMetrics = displayMetrics;
		displayInfor.heightPx = heightPx;
		displayInfor.heightDp = heightDp;
		displayInfor.widthPx = widthPx;
		displayInfor.widthDp = widthDp;
		
		return displayInfor;
	}
	
	
}
