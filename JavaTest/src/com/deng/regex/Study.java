package com.deng.regex;

public class Study {
    public static  void main(String[]  args){
    	
    	demo("hkjkkyuiqqjkyy45","(.)\\1");
    }
    
    /**
     * ������ʽ ���ѵĲ���  ������   \1\2   ������������
     * 1��ƥ����ƥ�������ַ��� ��һλһλ��ƥ��   []����ƥ�䵥���ַ�
     * 2��ȡ�� �и�  ���� ����������� ȥ�ҷ�����������ĳ�ν����и� ����ȡ�� ���߲��ҵ�
     * 3����.����ĸ���\n ʵ����Ҫд\\n �������������  ȡ���ֵ������$1
     * 
     * */
    public static void demo(String str,String regex){
    	String[] ss=str.split(regex);
    	for(String s:ss)
    	System.out.println(s);
    }
    
}
