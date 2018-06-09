package com.zowee.mes.test;

import com.zowee.mes.app.MyApplication;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * @author Administrator
 * @description 和mes-app对象有关的测试
 */
public class MyApplicationTest extends AndroidTestCase {
	private static final String TAG = "MyApplication";

	public void testGetAppVersion() throws Exception {
		Log.i(TAG, "" + MyApplication.getCurAppVersion());
	}

}
