package cn.chouchou;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class Util {
   
	
	/**
	 * 递归文件夹获取源代码
	 * 修改文件后缀
	 * */
	
	public static  void  copyFile(File src, File to)throws Exception{
		  to.mkdir();
		  File[] listFiles = src.listFiles();
		  for(File f:listFiles){
			  if(f.isDirectory()){
				  copyFile(f,new File(to,f.getName()));
			  }
			  else{
				  //是文件需要copy 过去
				  String filename=f.getName();
				  String tofilename=filename;
				  if(filename.endsWith(".java")){
					  tofilename=filename.replace("java", "jtxt");
				  }
				 /* if(filename.contains("csproj")){
					  tofilename=filename.replace("csproj", "tt");
				  }*/
				  copyf(f,new File(to,tofilename));
			  }
		  }
	}

	private static void copyf(File f, File file) throws Exception{
		// TODOO Auto-generated method stub
		FileInputStream fis=new FileInputStream(f);
		FileOutputStream  fos=new FileOutputStream(file);
		
		byte[] buffer=new byte[1024];
		int len=0;
		while((len=fis.read(buffer))!=-1){
			fos.write(buffer, 0, len);
			fos.flush();
		}
		fis.close();
		fos.close();
	}
	
	/*@Test
	public  void   testCopy()throws Exception{
		copyFile(new File("D:/产品部数据导入系统(XMINP)(201710101449)"), new File("D:/XXX"));
	}*/
	
	
    public static void main(String[] args)throws Exception {
    	copyFile(new File("D:/chouchou"), new File("D:/chouchou_new"));
	}
	
}
