package com.zowee.mes.update;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * @author Administrator
 *
 */
public class XMLParser 
{
	/**
	 * @return 
	 * @decription 获取DOM解析的操作对象
	 */
	public static DocumentBuilderFactory getDocBuiFac()
	{
		DocumentBuilderFactory documentBuilderFactory  = DocumentBuilderFactory.newInstance();
		
		return documentBuilderFactory;
	}
	
	
}
