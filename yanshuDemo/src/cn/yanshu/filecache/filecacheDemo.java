package cn.yanshu.filecache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import cn.yanshu.R;

public class filecacheDemo extends Activity {
	private  EditText  urlEdit;
	private  ImageView  imgview;
	
	
	private  Bitmap   bitmap=null;//���߳� ͨ��urlconnection��ȡ��  Ȼ��bitmapfactory  ������bitmap����
	
	
	private  Handler myHandler=new Handler(){
		 public void handleMessage(android.os.Message msg) {
			 imgview.setImageBitmap(BitmapFactory.decodeStream((InputStream) msg.obj));
		 };
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.filecache_activity);
		urlEdit=(EditText) findViewById(R.id.filecache_edit);
		imgview=(ImageView) findViewById(R.id.filecache_img);
	}
	  //�����ȡͼƬ
	  public  void  click(View  view) throws FileNotFoundException{
		  //1 ������һ���ͼƬ
		  if(new File(this.getCacheDir(),"demo.jpg").exists()){
			  //exists ֱ�Ӵӻ����� ����bitmap����
			 final Bitmap bitmap= BitmapFactory.decodeStream
					 (new FileInputStream(new File(this.getCacheDir(), "demo.jpg")));
			 imgview.setImageBitmap(bitmap);
			 System.out.println("������ȡ...");
		  }
		  else{
			  //�������ͼƬ����
			  new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// TODO Auto-generated method stub
						HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(
								urlEdit.getText().toString()).openConnection();
						InputStream is = httpUrlConnection.getInputStream();
						OutputStream os = new FileOutputStream(new File(
								filecacheDemo.this.getCacheDir(), "demo.jpg"));
						byte[] buff = new byte[1024];
						int len = 0;
						while ((len = is.read(buff)) != -1) {
							os.write(buff, 0, len);
							os.flush();
						}
						is.close();
						os.close();
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					try {
						bitmap = BitmapFactory.decodeStream
								 (new FileInputStream(new File(getApplicationContext().getCacheDir(),"demo.jpg")));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							 imgview.setImageBitmap(bitmap);
						}
					});
					 System.out.println("�ȱ����ڻ������� Ȼ������ʾ..."); 
				}
			}).start();
		  }
		 // networkImgtoImgview();
	  }
	  
	  private  void  networkImgtoImgview(){
		  new  Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					 
					  try {
						InputStream  is=null;
						URL url = new URL(urlEdit.getText().toString());
						HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
						urlconnection.setConnectTimeout(5000);
						urlconnection.setRequestMethod("GET");
						if(urlconnection.getResponseCode()==200){
							is = urlconnection.getInputStream();
						}
						else{
							is=urlconnection.getErrorStream();
						}
						bitmap=BitmapFactory.decodeStream(is);
						 /*
						   * ������handler.post(new Runnable(){});
						   * ����ҵ�����ʹ��handler
						   */
						  //Message.obtain(myHandler, 1, is).sendToTarget();
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									imgview.setImageBitmap(bitmap);
								}
						 });
					  } catch (Exception e) {
						// TODO: handle exception
						  e.printStackTrace();
					  }
				}
			  }).start();
	  }
	  
	  

}
