package cn.yanshu.multidownload;

import java.io.File;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import cn.yanshu.R;

/**
 *  开源xutils 完成多线程 断点续传
 *  自己实现多线程下载原理（非常复杂）:
 *     1、URL.openConnection获取conn对象 读取inputstream的contentlength
 *     2、本地用randomFile  创建temp.exe文件 设置文件长度等于上面
 *     3、写一个循环
 *     for(int x=0;x<threadcount;x++){
 *         a、a算出每个thread的起始位置 开启线程进行下载  线程起始位置 作为构造函数进行传递
 *         b、该自定义线程  再连接服务器  conn.sethead("range","")  把原文件进行分块
 *         c、每个线程开始下载即可
 *     
 *     }
 * 
 * */

public class MultiDownloadActivity extends Activity {
	private  EditText  urledit;
	private  ProgressBar  progressbar;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multidownload_activity);
		
		urledit=(EditText) findViewById(R.id.multidownload_edit);
		progressbar=(ProgressBar) findViewById(R.id.multidownload_progressbar);
	}
	
	
    public void  click(View view){
    	String path=urledit.getText().toString();
    	String tragetpath=new File(Environment.getExternalStorageDirectory(),"temp.rar").getAbsolutePath();
    	HttpUtils  httpUtils=new  HttpUtils();
    	HttpHandler<File> handler = httpUtils.download(
    			path, 
    			tragetpath,
    			true, 
    			true, 
    			new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						// TODO Auto-generated method stub
						System.out.println(responseInfo);
						System.out.println(responseInfo.result.getName());
					}
					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						System.out.println(error.toString()+"msg:"+msg);
					}
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						// TODO Auto-generated method stub
						super.onLoading(total, current, isUploading);
						System.out.println(current+"/"+total);
						progressbar.setMax((int) total);//设置进度条最大值
						progressbar.setProgress((int) current);//设置进度条当前值
					}
				
				});
    }
}
