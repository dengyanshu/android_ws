package com.zowee.mes.utils;

/**
 * @author Administrator
 * @description ������������������
 */
public class ArrayUtils {

	/**
	 * @return
	 * @description ��һ������������Ѱ���ض�������
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
