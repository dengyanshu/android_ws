package cn.chouchou.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Test;

public class Maintest {
       
	
	
	@Test
	public  void  test()throws Exception{
		 InputStream  is=Maintest.class.getClassLoader().getResourceAsStream("src");
		 ByteArrayOutputStream baos=new ByteArrayOutputStream();
		 int  len=0;
		 byte[] b=new byte[1024];
		 while((len=is.read(b))!=-1){
			 baos.write(b, 0, len);
		 }
		 String msg=baos.toString();
		 msg=msg.substring(msg.lastIndexOf("Production Information"));
		 msg=msg.substring(0, msg.indexOf("Stop information per machine"));
		 msg=msg.replace("</tr></tr>", "</tr>").replace("\"", "");
		 msg=msg.substring(msg.indexOf("<table"), msg.lastIndexOf("</table>")+8);
		 System.out.println(msg);
		//Production Information
		//Stop information per machine
	}
}
