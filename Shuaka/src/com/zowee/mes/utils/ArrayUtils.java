package com.zowee.mes.utils;

/**
 * @author Administrator
 * @description 数据容器操作辅助类
 */
public class ArrayUtils {

	/**
	 * @return
	 * @description 在一个数字数组中寻找特定的数字
	 */
	public static boolean findValInIntArr(int special, int[] intArr) {
		if (null == intArr || 0 == intArr.length)
			throw new NullPointerException("intArr can not be null");
		for (int i = 0; i < intArr.length; i++) {
			if (special == intArr[i])
				return true;
		}

		return false;
	}

}
