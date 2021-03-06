package com.deng.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * @description
 */
public class StringUtils {

	/*
	 * 检测目标字符串是否空 空的化 返回true 否则返回false
	 */
	public static boolean isEmpty(String str) {
		if (null == str || "".equals(str.trim()))
			return true;

		return false;
	}

	/**
	 * @param scanned
	 *            bar code
	 * @description 检查条码类型,返回 0：没有对应的类型； 1：MAC；2：数字;3:数字和字母(不包括I与O);4:数字和字母
	 */
	public static int checkBarcodeType(String Str) // Add by ybj 2014-03-07
	{
		String regex = "^[A-F0-9]{12}$"; // 扫描的条码是否为MAC
		if (Str.trim().toUpperCase().matches(regex)) {
			return 1;
		}
		regex = "^\\d{8,40}$";
		if (Str.trim().toUpperCase().matches(regex)) {
			return 2;
		}
		regex = "^([A-H0-9]|[J-N]|[P-Z]){8,40}$";
		if (Str.trim().toUpperCase().matches(regex)) {
			return 3;
		}
		regex = "^[A-Z0-9]{8,40}$";
		if (Str.trim().toUpperCase().matches(regex)) {
			return 4;
		}
		return 0;

	}

	/*
	 * 通过正则表达式 获取匹配的所有下标值
	 */
	public static List<int[]> getMatStrIndex(String regex, String str) {
		if (StringUtils.isEmpty(regex) || StringUtils.isEmpty(str))
			return null;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		List<int[]> matchIndexs = new ArrayList<int[]>();
		while (matcher.find()) {
			int[] indItem = new int[2];
			indItem[0] = matcher.start();
			indItem[1] = matcher.end();
			matchIndexs.add(indItem);
		}

		return matchIndexs;
	}

	public static boolean isScannedMONumber(String str) {
		String regex = "^MO[A-Z0-9]\\d{10,}"; // 工单条码的正则表达式。
		if (str.trim().toUpperCase().matches(regex)) {
			return true;
		}
		return false;
	}

	public static boolean isScannedSMTLadcardErrorCode(String str) {
		String regex = "^SMT\\d{1}-\\d{4}";
		if (str.trim().toUpperCase().matches(regex)) {
			return true;
		}
		return false;
	}

	public static boolean isScannedStorageSlot(String Str) // Add by ybj
															// 2014-02-18
	{
		String regex = "\\w{3,5}-\\w{1}-\\d{2}-\\d{3}$"; // 存位条码的正则表达式。
		if (Str.trim().toUpperCase().matches(regex)) {
			return true;
		}
		return false;
	}

	/**
	 * @param str
	 * @return
	 * @description 判断一个字符串是否是数字类型
	 */
	public static boolean isNumberal(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		String regex = "^[-+]?(([0-9]+)([.]([0-9]+))?)$";
		str = str.trim();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);

		return matcher.matches();
	}

	/**
	 * @param str
	 * @param regex
	 *            例如 String regex = "^[-+]?(([0-9]+)([.]([0-9]{1}))?)";
	 * @return
	 * @description 把一个带有小数位的数字字符串 按照 正则表达 对它的小数为进行截取
	 */
	public static String conDeciStr(String str, int decimalNum) {

		if (StringUtils.isEmpty(str) | 0 > decimalNum || !isNumberal(str))
			return null;
		str = str.trim();
		int dotIndex = str.indexOf(".");
		if (-1 == dotIndex)
			return str;
		int end = 0;
		if (0 != decimalNum)
			end = dotIndex + 1;
		else
			end = dotIndex;
		end = end + decimalNum;
		if (str.length() < end)
			end = str.length();

		return str.substring(0, end);
	}

	/**
	 * @param str
	 * @param regex
	 * @return
	 * @description 查找str中是否包含正则表达式表述的内容
	 */
	public static boolean findStr(String str, String regex) {
		boolean isFinding = false;
		if (null == str || null == regex)
			return isFinding;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		isFinding = matcher.find();

		return isFinding;
	}

}
