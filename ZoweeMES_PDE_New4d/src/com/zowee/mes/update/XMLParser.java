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
	 * @decription ��ȡDOM�����Ĳ�������
	 */
	public static DocumentBuilderFactory getDocBuiFac()
	{
		DocumentBuilderFactory documentBuilderFactory  = DocumentBuilderFactory.newInstance();
		
		return documentBuilderFactory;
	}
	
	
}
