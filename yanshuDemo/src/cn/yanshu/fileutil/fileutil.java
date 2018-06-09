package cn.yanshu.fileutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * @author dengfeng
 * 该类 主要演示android 文件保存：
 * 1、/data/data/cn.yanshu files
 * 2、sdcard
 * 3、sharedpreferences演示
 */
public class fileutil {

	/**
	 * 示例 文件读写的简单封装
	 * */

	public static void writeFile(Context context, String username, String pwd) {
		try {
			FileOutputStream fos = context.openFileOutput("user.txt",
					Context.MODE_APPEND);
			fos.write((username + "##" + pwd).getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String readFile(Context context, String filename)
			throws Exception {
		byte[] buff = null;
		int len = 0;
		// 文件名字是/data/data/cn.yanshu 应用下的文件 如果是调用context api保存的 会自动生成一级file目录
		FileInputStream fis = context.openFileInput(filename);
		buff = new byte[1024];
		len = fis.read(buff);
		return new String(buff, 0, len);

	}

	/**
	 * 文件读写sd卡 用environment api 做 设备的挂载 和可读写判断 还可以获取sd卡目录
	 * 
	 * */

	public static void writeSDCard(Context context, String username, String pwd) {
		String externalStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
			if (Environment.MEDIA_MOUNTED_READ_ONLY != externalStorageState) {
				try {
					FileOutputStream fos = new FileOutputStream(new File(
							Environment.getExternalStorageDirectory(),
							"user.txt"));
					fos.write((username + "##" + pwd).getBytes());
					fos.flush();
					fos.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				Toast.makeText(context, "SD卡不能写", 1).show();
			}
		} else {
			Toast.makeText(context, "SD卡没有被挂载", 1).show();
		}

	}
    /*
     * 计算sdcard  size
     * */
	public void sdcartSize(Context context) {
		Environment.getExternalStorageDirectory().getTotalSpace();
		//该方法可以获取总的空间大小

	}
}
