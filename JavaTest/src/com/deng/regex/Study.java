package com.deng.regex;

public class Study {
    public static  void main(String[]  args){
    	
    	demo("hkjkkyuiqqjkyy45","(.)\\1");
    }
    
    /**
     * 正则表达式 最难的部分  （）组   \1\2   代表反向引用组
     * 1、匹配是匹配整个字符串 ，一位一位的匹配   []代表匹配单个字符
     * 2、取代 切割  查找 都是正则规则 去找符合正则规则的某段进行切割 或者取代 或者查找到
     * 3、（.）组的概念\n 实际中要写\\n 反向引用这个组  取组的值可以用$1
     * 
     * */
    public static void demo(String str,String regex){
    	String[] ss=str.split(regex);
    	for(String s:ss)
    	System.out.println(s);
    }
    
}
