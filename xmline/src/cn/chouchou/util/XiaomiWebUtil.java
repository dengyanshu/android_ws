package cn.chouchou.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class XiaomiWebUtil {
	public static String getLineInfo(String day1,String day2,String h1,String h2) throws Exception{
		// TODO Auto-generated method stub
        if(day1==null||day1.length()!=8||day2==null||day2.length()!=8||h1==null||h1.length()!=2||h2==null||h2.length()!=2){
        	return  "input parameter invalid";
        }
		CloseableHttpClient httpclient = HttpClients.createDefault();;  
        String msg="";
        
        //-------------------------------------------login-------------------------------------------
        // 创建http请求(get方式) 
        System.out.println("login begin..................................................................."); 
        String url="http://192.168.10.1:8080/lws/Login.do?userid=pfsc&password=1qaz2wsx&btnLogin=LogIn&operation=0&icuserid=";
        //String url="http://192.168.10.1:8080/lws/Login.do?userid=pfsc&password=1qaz2wsx";
        HttpGet httpget = new HttpGet(url);  
        CloseableHttpResponse response = httpclient.execute(httpget);  
        HttpEntity entity = response.getEntity();  
        if (entity != null) {  
            System.out.println("Response content length: " + entity.getContentLength());  
            System.out.println(EntityUtils.toString(entity));  
            EntityUtils.consume(entity);  
        } 
        System.out.println("login success...................................................................");  
        
        
        System.out.println("select begin..................................................................."); 
        url="http://192.168.10.1:8080/lws/DispatchProductManageReportAction.do?" +
        		"startdate="+day1+"&starthour="+h1+"&startminute=00&startsecond=00&enddate="+day2+"&endhour="+h2+"&endminute=00&endsecond=00&command=ShowReport";
        httpget = new HttpGet(url);  
        response = httpclient.execute(httpget);  
        entity = response.getEntity();  
        if (entity != null) {  
            System.out.println("Response content length: " + entity.getContentLength());  
            System.out.println(EntityUtils.toString(entity));  
            EntityUtils.consume(entity);  
        } 
        System.out.println("select success..................................................................."); 
       
        
        
        Thread.sleep(1000*60);
        System.out.println("thrid begin..................................................................."); 
        url="http://192.168.10.1:8080/lws/ReferProductManageReportProgressAction.do?mode=1";
        httpget = new HttpGet(url);  
        response = httpclient.execute(httpget);  
        entity = response.getEntity();  
        if (entity != null) {  
            System.out.println("Response content length: " + entity.getContentLength());
            msg=EntityUtils.toString(entity);
             if(msg.indexOf("Line In")!=-1){
                  msg=msg.substring(msg.indexOf("Line In"), msg.indexOf("Production Information"));
             }
            System.out.println(msg); 
            EntityUtils.consume(entity);  
        } 
        System.out.println("thrid end..................................................................."); 
        
        
        httpclient.close();  

		return  msg;
	}
}
