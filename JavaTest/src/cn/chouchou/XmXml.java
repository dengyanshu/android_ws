package cn.chouchou;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XmXml {

	 /**
	  * 解析一段xml
	  * */
	 public List<Keybox> parserUtil(String filepath)throws Exception{
		 SAXParser newSAXParser = SAXParserFactory.newInstance().newSAXParser(); 
		// InputStream is= this.getClass().getClassLoader().getResourceAsStream("test.xml");
		 InputStream  is=new FileInputStream(filepath);
		 final List<Keybox>  keyboxs =new ArrayList<Keybox>();
		 
		 newSAXParser.parse(is,  new DefaultHandler(){
			   private int  count=0;//keybox num count;
			   
			    private String   tagName;
			    
			    private  Keybox keybox;
			    
			    private List<Key>  keys;
			    private  Key key;
			    
			    private  CertificateChain  certchain;
			    private  List<String> certs;
			    
			    private  String  sb="";
			    //发现出现\n无法解析的情况
			    @Override
		    	public void startDocument() throws SAXException {
		    		// TODO Auto-generated method stub
		    		super.startDocument();
		    		//System.out.println("begin ..document");
		    	}
		    	@Override
		    	public void endDocument() throws SAXException {
		    		// TODO Auto-generated method stub
		    		super.endDocument();
		    		//System.out.println("end ..document");
		    	}
		    	@Override
		    	public void startElement(String uri, String localName,
		    			String qName, Attributes attributes) throws SAXException {
		    	    
		    	     tagName=qName;
		    	     if(tagName.equals("AndroidAttestation")){
		    	    	
		    	    	 
		    	     }
		    	     else if(tagName.equals("NumberOfKeyboxes")){
		    	    	 
		    	    	 
		    	     }
		    	     else if(tagName.equals("Keybox")){
		    	    	 count++;
		    	    	 keybox=new Keybox();
		    	    	 keybox.setKeyboxNum(count);
		    	    	 keybox.setDeviceId(attributes.getValue(0));
		    	    	 keys=new ArrayList<Key>();
		    	     }
		    	     else if(tagName.equals("Key")){
		    	    	 
		    	    	key=new Key();
		    	    	key.setName(attributes.getValue(0));
		    	     }
		    	     else if(tagName.equals("PrivateKey")){
			    	    	
			    	    	
			    	  }
		    	     else if(tagName.equals("CertificateChain")){
		    	    	 certchain=new CertificateChain();
		    	    	 
			    	    	
			    	  }
		    	     else if(tagName.equals("NumberOfCertificates")){
		    	    	  //chain num
		    	    	 certs=new ArrayList<String>();
			    	  }
		    	     //
		    	     else if(tagName.equals("Certificate")){
		    	    	  //chain num
		    	    	
			    	  }
		    		
		    	}
		    	@Override
		    	public void endElement(String uri, String localName, String qName)
		    			throws SAXException {
		    		 if(qName.equals("CertificateChain")){
		    			 certchain.setCerts(certs);
		    	    	 key.setCertchain(certchain);
		    	     }
		    		 else if(qName.equals("Key")){
			    	    	keys.add(key);
			    	 }
		    		 else if(qName.equals("Keybox")){
		    			    keybox.setKeys(keys);
			    	    	keyboxs.add(keybox);
			    	    	//1000个的话  需要清理下堆内存
			    	    	if(keyboxs.size()>=1000){
			    	    		try {
									writeTodb(keyboxs);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			    	    		keyboxs.clear();
			    	    	}
			    	 }
		    		 else if(qName.equals("PrivateKey")){
		    			   key.setPrivateKey(sb.toString()) ;
		    			   sb="";
		    			   //sb.delete(0, sb.length());
			    	 }
		    		 else if(qName.equals("Certificate")){
		    			   certs.add(sb) ;
		    			   sb="";
		    			   //sb.delete(0, sb.length());
			    	 }
		    		 //
		    		 tagName=null;
		    	}
		    	@Override
		    	public void characters(char[] ch, int start, int length)
		    			throws SAXException {
		    		String chars=new String(ch,start,length);
		    		if(tagName!=null){
		    			 if(tagName.equals("NumberOfKeyboxes")){
		    				 //keybox.setKeyboxNum(Integer.parseInt(chars));
			    	     }
		    			 else  if(tagName.equals("Keybox")){
		    				 
		    			 }
		    			 else if(tagName.equals("Key")){
		    				 
		    			 }
		    			 //出现回车换行的时候 sax解析器还在字符节点
		    			 else if(tagName.equals("PrivateKey")){
				    	    	sb=sb+chars;
				    	    	
				    	 }
		    			 else if(tagName.equals("NumberOfCertificates")){
				    	    	certchain.setNum(Integer.parseInt(chars));
				    	  }
		    			  else if(tagName.equals("Certificate")){
		    				    sb=sb+chars;
			    	    	
				    	  }
		    		}
		    	}
		 });
		 return  keyboxs;
	 }
	 
	
	 
	 /**
	  * 写入数据库
	  * */
	 
	 public  void  writeTodb(List<Keybox> keyboxs)throws Exception{
		 QueryRunner  qr=JDBCUtil.getQr();
		 //分三个表写入数据
		 String sql="";
		 for(Keybox  keybox:keyboxs){
			 //第一张表插入完成
			 sql="INSERT INTO  dbo.keybox (keybox_num ,deviceId) VALUES  (?,?)";
			 String deviceid=keybox.getDeviceId();
			 qr.update(sql,keybox.getKeyboxNum(),deviceid);
			 
			 
			 for(Key key:keybox.getKeys()){
				 sql="INSERT INTO  dbo.xmkey (keybox_deviceId ,keyname ,privatekey ,certchain_num ) VALUES  (?,?,?,?)";
			     qr.update(sql,  new Object[]{deviceid
			    	,key.getName()
			    	,key.getPrivateKey()
			    	,key.getCertchain().getNum()
			     });
			     
			     for(int x=1;x<=key.getCertchain().getCerts().size();x++){
			    	 sql="INSERT INTO  dbo.certchain (keybox_deviceId,keyname , cert_seq ,cert_content ) VALUES  (?,?,?,?)";
			    	qr.update(sql,  new Object[]{
			    			deviceid
						    	,key.getName()
						    	,x
						    	,key.getCertchain().getCerts().get(x-1)
						     });
			     }
			 }
			 
		 }
	 }
	 
	 
	 @Test
	 public  void   testparse(){
		 try {
			List<Keybox> keyboxes=parserUtil("d://test.xml");
			writeTodb(keyboxes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

}
