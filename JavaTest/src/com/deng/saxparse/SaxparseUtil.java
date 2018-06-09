package com.deng.saxparse;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxparseUtil {
	
	
	
	public  static List<Person> getDatafromxml(String xml) throws Exception{
		List<Person> list=new ArrayList<Person>();
		PersonHandler myhandler=new PersonHandler(list);
		SAXParserFactory saxparserfactory=SAXParserFactory.newInstance();
		SAXParser saxparser=saxparserfactory.newSAXParser();
		saxparser.parse(new File(xml), myhandler );
		
		return myhandler.getList();
	}

	/**
	 * 通过字符串路径 读取文件内容 按UTF-8解码
	 * 
	 * **/
	public  static  String getStrFromPathName(String pathname) throws Exception{
		FileInputStream  fis=new FileInputStream(pathname);
		byte[] buffer=new byte[1024];
		int len=fis.read(buffer);
		return new String(buffer,0,len,"utf-8");
		
	}
}





 class PersonHandler extends DefaultHandler{
    private  List<Person> list;
    private  Person  person;
    private  String tag;
    
    
   
	PersonHandler(List<Person> list){
   	    this.list=list;
    }
	public List<Person> getList() {
		return list;
	}
	public void setList(List<Person> list) {
		this.list = list;
	}
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		list=new  ArrayList<Person>();
	}
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
	}
	
	 @Override
	 public void startElement(String uri, String localName, String qName,
			 Attributes attributes) throws SAXException {
		 // TODO Auto-generated method stub
		 super.startElement(uri, localName, qName, attributes);
		 tag=qName;
		 if("Person".equals(qName)){
			 person=new  Person();
			 person.setId(Integer.parseInt(attributes.getValue(0)));
		 }
	 }
	 @Override
	 public void endElement(String uri, String localName, String qName)
			 throws SAXException {
		 // TODO Auto-generated method stub
		 super.endElement(uri, localName, qName);
		 tag=null;
		 if("Person".equals(qName)){
		    list.add(person);
		 }
	 }
   
	

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		String content=new String(ch, start, length);
		if(tag!=null){
			if("name".equals(tag)){
				person.setName(content);
			}
			if("sex".equals(tag)){
				Sex sex=null;
				if("MAN".equals(content))
					 sex=Sex.MAN;
				else
					sex=Sex.FAMALE;
				person.setSex(sex);
			}
		}
	}
}
