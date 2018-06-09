package com.zowee.mes.utils;

public class HexStrConver {

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	/**
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * @param varifyParas
	 *            字节数组验证参数
	 * @param varifiedBytes
	 *            被验证的字节数组
	 * @return
	 * @description 验证被验证的字节数组中是否包含任意一个字节数组验证参数 如果包含 返回true
	 */
	public static boolean isContainByte(byte[] varifyParas, byte[] varifiedBytes) {
		if (null == varifyParas || null == varifiedBytes)
			throw new NullPointerException(
					"varifyParas,varifiedBytescan not null and ");
		for (int i = 0; i < varifyParas.length; i++) {
			byte temp = varifyParas[i];
			for (int j = 0; j < varifiedBytes.length; j++) {
				if (temp == varifiedBytes[j])
					return true;
			}

		}

		return false;
	}

	static int hexCharToInt(char c) {
		if (c >= '0' && c <= '9')
			return (c - '0');
		if (c >= 'A' && c <= 'F')
			return (c - 'A' + 10);
		if (c >= 'a' && c <= 'f')
			return (c - 'a' + 10);

		throw new RuntimeException("invalid hex char '" + c + "'");
	}

	/**
	 * Converts a hex String to a byte array.
	 * 
	 * @param s
	 *            A string of hexadecimal characters, must be an even number of
	 *            chars long
	 * 
	 * @return byte array representation
	 * 
	 * @throws RuntimeException
	 *             on invalid format
	 */
	public static byte[] hexStringToBytes(String s) {
		byte[] ret;

		if (s == null)
			return null;

		int sz = s.length();

		ret = new byte[sz / 2];

		for (int i = 0; i < sz; i += 2) {
			ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4) | hexCharToInt(s
					.charAt(i + 1)));
		}

		return ret;
	}

	/**
	 * Converts a byte array into a String of hexadecimal characters.
	 * 
	 * @param bytes
	 *            an array of bytes
	 * 
	 * @return hex string representation of bytes array
	 */
	public static String bytesToHexString(byte[] bytes) {
		if (bytes == null)
			return null;

		StringBuilder ret = new StringBuilder(2 * bytes.length);

		for (int i = 0; i < bytes.length; i++) {
			int b;

			b = 0x0f & (bytes[i] >> 4);

			ret.append("0123456789abcdef".charAt(b));

			b = 0x0f & bytes[i];

			ret.append("0123456789abcdef".charAt(b));
		}

		return ret.toString();
	}

}
