package com.deng.benlei;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.deng.instance.People;

public class Benlei {

	/**
	 * @param args
	 */
//	  public static String encryptBASE(byte[] key) throws Exception { 
//		    return (new BASEEncoder()).encodeBuffer(key); 
//		} 
		
	
	  /**
	   * MD5 加密算法
	   * 得到加密后的字符串
	   * */
		
		  public static String getResult(String inputStr)
		  {
			 
			  String bodyIn="GetDataSetWithSQLStringResponse{GetDataSetWithSQLStringResult=anyType{schema=anyType{" +
			  		"element=anyType{complexType=anyType{choice=anyType{element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; element=anyType{}; }; }; }; element=anyType{complexType=anyType{sequence=anyType{element=anyType{}; element=anyType{}; }; }; }; }; }; }; }; diffgram=anyType{SQLDataSet=anyType{SQLDataSet=anyType{Qty1=10; Qty2=90; Stock=PACK3-A-04-006; StockUserCode=S-37; ProductName=120001-0059-01; VendorName=卓翼第一事业部; DateCode=1615; LotCode=236055; ProductDesc=电话线/双端RJ11-6P2C-铜包铝-冷灰3U-2.5*5.0mm-1500±10mm-平行包扎120±10mm-无印字-端子镀金Fu"-无PE袋-环保-永钜(ZY03-331)(华为编码:96460109;96460122); FactoryCode=第一事业部（智造）; PoName=POJIT20150520; LotSN1=MZD3168BNBZY000O; LotSN2=MZ0JIT1501072303; Month=5; }; SQLDataSet1=anyType{I_ReturnValue=0; I_ReturnMessage=ServerMessage:Code:000017-010 批号【mz0jit1501072303】成功将数量10拆分至新批号【MZD3168BNBZY000O】中!; }; }; }; }; }";
			  
			  
			  SQLDataSet=anyType{OutReturn=-1; }; 
			            SQLDataSet1=anyType{I_ReturnValue=-1; I_ReturnMessage=ServerMessage:Code:000017-008  拆分的数量大于等于条码的实际数量，不能拆分。; }; 
			  
			            SQLDataSet=anyType{Qty1=10; Qty2=90; Stock=PACK3-A-04-006; StockUserCode=S-37; ProductName=120001-0059-01; VendorName=卓翼第一事业部; DateCode=1615; LotCode=236055; ProductDesc=电话线/双端RJ11-6P2C-铜包铝-冷灰3U-2.5*5.0mm-1500±10mm-平行包扎120±10mm-无印字-端子镀金Fu"-无PE袋-环保-永钜(ZY03-331)(华为编码:96460109;96460122); FactoryCode=第一事业部（智造）; PoName=POJIT20150520; LotSN1=MZD3168BNBZY000O; LotSN2=MZ0JIT1501072303; Month=5; }; " +
			            		"SQLDataSet1=anyType{I_ReturnValue=0; I_ReturnMessage=ServerMessage:Code:000017-010 批号【mz0jit1501072303】成功将数量10拆分至新批号【MZD3168BNBZY000O】中!; }; 
			
			  char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       

		        try {
		            byte[] btInput = inputStr.getBytes();
		            // 获得MD5摘要算法的 MessageDigest 对象
		            MessageDigest mdInst = MessageDigest.getInstance("MD5");
		            // 使用指定的字节更新摘要
		            mdInst.update(btInput);
		            // 获得密文
		            byte[] md = mdInst.digest();
		            // 把密文转换成十六进制的字符串形式
		            int j = md.length;
		            char str[] = new char[j * 2];
		            int k = 0;
		            for (int i = 0; i < j; i++) {
		                byte byte0 = md[i];
		                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
		                str[k++] = hexDigits[byte0 & 0xf];
		            }
		            return new String(str);
		        } catch (Exception e) {
		            e.printStackTrace();
		            return null;
		        }


		  }
		  /***
		   * test
		   * 
		   * ***/
		  
			private static String getStr(String msg){
				
			
//				msg=msg.replace("$Stock$", Stock).replace("$StockUserCode$", StockUserCode).replace("$ProductName$", ProductName).replace("$VendorName$", VendorName)
//						.replace("$ProductDesc1$", ProductDesc).replace("$PoName$", PoName).replace("$DateCode$", DateCode).replace("$LotCode$", LotCode).replace("$FactoryCode$", FactoryCode)
//						.replace("$Month$", Month).replace("$LotSN$", LotSN).replace("$Qty$", Qty);
				
				//String ProductDesc="'40*20mm-印有“01-5043-101344)- 40*20-2013-04-01- GSM固定无线电话机-ETS3125i-中国大陆地区专用-终端专用”-华为料号：29060955-环保-华为客供'";
				String ProductDesc="华为客供 GSM固定无线电话机-ETS3125i-中国大陆地区专用-终端专用”-华为料号：";
				
				msg="TEXT 270,10,\"3\",90,1,1,\"PO:$PoName$\"\n"+
						"TEXT 270,330,\"TSS24.BF2\",90,1,1,\"数量:\"\n"+
						"TEXT 270,390,\"3\",90,1,1,\"$Qty$\"\n"+
						"TEXT 240,10,\"TSS24.BF2\",90,1,1,\"供应商：$VendorName$\"\n"+
						"TEXT 210,10,\"3\",90,1,1,\"D/C:$DateCode$\"\n"+
						"TEXT 210,300,\"3\",90,1,1,\"L/C:$LotCode$\"\n"+
						"BARCODE 180,90,\"128\",60,1,90,2,1,\"$LotSN$\"\n"+
						"TEXT 120,10,\"3\",90,1,1,\"S/N:\"\n"+
						"TEXT 90,10,\"TSS24.BF2\",90,1,1,\"品名/规格：\"\n"+
						"TEXT 90,140,\"TSS24.BF2\",90,1,1,\"$ProductDesc1$\"\n"+
						"TEXT 60,20,\"TSS24.BF2\",90,1,1,\"$ProductDesc2$\"\n"+
						"TEXT 30,20,\"TSS24.BF2\",90,1,1,\"$ProductDesc3$\"\n"+
						"PRINT 1,1\n";
				for(int n=1;n<=3;n++){
					if(ProductDesc.length()>=30){
						msg=msg.replace("$ProductDesc"+n+"$", ProductDesc.substring(0, 30));
						ProductDesc=ProductDesc.substring(30);
					}else{
						msg=msg.replace("$ProductDesc"+n+"$", ProductDesc.substring(0, ProductDesc.length()));
						if(n==1)msg=msg.replace("$ProductDesc2$", "").replace("$ProductDesc3$", "");
						if(n==2)msg=msg.replace("$ProductDesc3$", "");
						break;
					}
				}
				
				
				
				return msg;
			}
	  
	public static void main(String[] args){
        String msg= getStr("");
		System.out.println(msg);
	   System.out.println("hello \"nihao\"");
		String mail="hdakjhdak@sa.com";
		System.out.println("该字符串是邮箱吗："+mail.matches("\\w{3,}@\\w{3,}.com"));
	
	
	  //System.out.println(getResult("123456"));
	  //区块选中：alt+shift+A
		
		
		
		//匹配每行^(.*)$  取代\1取到这个组+“；
		
		
		String lotsn="7S00001";
		String userid="SUR00889891";
		String sql="INSERT  INTO HW_Uncover_Material(lotsn ,userid,Createdate,Appearance) VALUES('"+lotsn+"','"+userid+"',getdate(),'true')";
		 System.out.println(sql);
		// TODO Auto-generated method stub
         //Fu.staitc_getname();
		  System.out.println(" OPPO重复装车！单板条码[7S3AVD154E000330]已装车!".contains("OPPO重复装车"));
		  System.out.println("".equals(null));
		   try {
			System.out.println(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			int max=0;
			int[] arr={1,3,5};
			for(int x=0;x<arr.length;x++){
				if(arr[x]>max){
					max=arr[x];
				}
			}
			System.out.println(max);
		
		
		Properties  properties=new Properties();
		properties.put("name", "zhansan");
		properties.put("pwd", "123456");
		
		try {
			properties.store(new FileOutputStream("d://1.properties"), "user");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(Math.ceil(15/(4*1.0)));
		
		System.out.println("-----------------------------------------");
//		userID = Params[0];
//		StickType = Params[1];
//		userName = MyApplication.getMseUser().getUserName(); 
//		owner = Params[2];
//		scanSN = Params[3].trim();
//		railName = Params[4];
//		side = Params[5];
//		ScanSNEnd = Params[6];
//		OnepanelQTy = Params[7];
//		result.put("Rail", railName); // 返回结果中添加轨道信息
//		String Sql = "declare @ReturnMessage nvarchar(max) = ' '  declare @Return_value int declare @ExceptionFieldName nvarchar(100) declare @Return_FinishQty int='0' exec @Return_value = Txn_SMTIssueExeNew_JM4d @I_ExceptionFieldName = @ExceptionFieldName out,@I_ReturnMessage=@ReturnMessage out,@Returnint =@Return_FinishQty out,@I_OrBitUserName='"
//				+ userName
//				+ "',@I_PlugInCommand='',@I_OrBitUserId='"
//				+ userID
//				+ "',@I_ResourceName='"
//				+ owner
//				+ "',@LotSN='"
//				+ scanSN
//				+ "',@paol ="
//				+ OnepanelQTy+",@rail='"+railName+"',@xiaob=1,"
//				+ "',@ABSide='"
//				+ side
//				+ "' select @ReturnMessage as I_ReturnMessage,@Return_value as I_ReturnValue,@Return_FinishQty as FinishQty";//
//		
		
		
		HashMap<String, String> map=new HashMap<String,String>();
		map.put("qty", "2000");
		
		 List<HashMap<String, String>> qty = new ArrayList<HashMap<String,String>>();
		 
		 qty.add(map);
		System.out.println(qty);
		
		for(HashMap<String, String>  ma:qty){
		System.out.println(ma.get("qty"));
		}
		
		String qty_va=qty.get(0).get(qty);
		System.out.println(qty_va);
		 
		
		
		
		
		//String  str="anyType{SQLDataSet=anyType{moid=MOD100003MDI; moname=_MOF060115040021-1; }; SQLDataSet=anyType{moid=MOD1000031UV; moname=ISHBE-W140808002; }; }";
		String  str="anyType23{anyType3{aQLDataSet=anyType{moid=MOD100003MDI; moname=_MOF060115040021-1; }; SQLDataSet=anyType{moid=MOD1000031UV; moname=ISHBE-W140808002; }; }";
		str=str.replaceAll("^(anyType\\d+\\{)+", "");
		System.out.println(str);
		
	    String   s="a==d";
	    String[] ss=s.split("=");
	    System.out.println(ss.length);
	    for(String sss :ss){
	    	System.out.println(sss);
	    }
	    
	    
	  
	}
	


}


class  Car{
	private static Car  c=new Car();
	private Car(){
		
	}
	public static Car  getInstance(){
		return c;
	}
	
	
	
}
//javap -c -package -public -private HelloWorld