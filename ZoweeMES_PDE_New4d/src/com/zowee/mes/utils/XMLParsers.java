package com.zowee.mes.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * @author Administrator
 * @description �ṩXML������ 
 */
public class XMLParsers 
{

	/*
	 * ��ȡһ��Pull��XML������
	 */
	public static XmlPullParser getPullParser() throws Exception
	{
				
		return Xml.newPullParser();
	}
	
	/*
	 * ��ȡһ��DOM XML������
	 */
	public static DocumentBuilder getDocumentBuilder() throws Exception
	{
		DocumentBuilderFactory documentBuildFactory = DocumentBuilderFactory.newInstance();
		
		return documentBuildFactory.newDocumentBuilder();
	}
	
	/*
	 * ��ȡһ��saxXML������
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
