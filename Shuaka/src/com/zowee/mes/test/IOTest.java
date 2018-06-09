package com.zowee.mes.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;

import android.content.res.AssetManager;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * @author Administrator
 * @description …Êº∞I/0µƒ≤‚ ‘¿‡
 */
public class IOTest extends AndroidTestCase {

	private static final String TAG = "IOTest";

	public void testAssetsIO() throws Exception {
		AssetManager assetManager = this.getContext().getAssets();
		InputStream inStream = assetManager.open("update_config.properties",
				AssetManager.ACCESS_BUFFER);
		String filePath = "file:///android_asset/serialPortConfig.properties";
		URI uri = URI.create(filePath);
		File outFile = new File(uri);
		Log.i(TAG, "" + outFile.exists());
		// FileOutputStream fileOutputStream = new FileOutputStream(outFile);

	}

}
