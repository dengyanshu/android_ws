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
 * ���� ��Ҫ��ʾandroid �ļ����棺
 * 1��/data/data/cn.yanshu files
 * 2��sdcard
 * 3��sharedpreferences��ʾ
 */
public class fileutil {

	/**
	 * ʾ�� �ļ���д�ļ򵥷�װ
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
		// �ļ�������/data/data/cn.yanshu Ӧ���µ��ļ� ����ǵ���context api����� ���Զ�����һ��fileĿ¼
		FileInputStream fis = context.openFileInput(filename);
		buff = new byte[1024];
		len = fis.read(buff);
		return new String(buff, 0, len);

	}

	/**
	 * �ļ���дsd�� ��environment api �� �豸�Ĺ��� �Ϳɶ�д�ж� �����Ի�ȡsd��Ŀ¼
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
				Toast.makeText(context, "SD������д", 1).show();
			}
		} else {
			Toast.makeText(context, "SD��û�б�����", 1).show();
		}

	}
    /*
     * ����sdcard  size
     * */
	public void sdcartSize(Context context) {
		Environment.getExternalStorageDirectory().getTotalSpace();
		//�÷������Ի�ȡ�ܵĿռ��С

	}
}
