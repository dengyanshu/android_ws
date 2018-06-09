package com.zowee.mes.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * @author Administrator
 * @description 提供XML解析器 
 */
public class XMLParsers 
{

	/*
	 * 获取一个Pull的XML解析器
	 */
	public static XmlPullParser getPullParser() throws Exception
	{
				
		return Xml.newPullParser();
	}
	
	/*
	 * 获取一个DOM XML解析器
	 */
	public static DocumentBuilder getDocumentBuilder() throws Exception
	{
		DocumentBuilderFactory documentBuildFactory = DocumentBuilderFactory.newInstance();
		
		return documentBuildFactory.newDocumentBuilder();
	}
	
	/*
	 * 获取一个saxXML解析器
	 */
	public static SAXParser getSaxXmlParser()
	{
		SAXParserFactory  saxParserFactory = SAXParserFactory.newInstance();
		SAXParser parser = null;
		try 
		{
			parser = saxParserFactory.newSAXParser();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parser;
	}
	
	
	
}
