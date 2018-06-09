package cn.chouchou;

import org.junit.Test;

/*
 * 正则表达：
 * 1、匹配  单个字符匹配 匹配错误 则返回
 * 2、切割 替换  获取 （注意正则中的默认是贪婪的  有时候根据业务需要防止贪婪用？）
 * */

public class RegexDemo {
   
	
	    @Test
	   public   void  splitDemo(){
		  String  str="dadddlkljoooolpo";
		  String[] ss= str.split("(.)\\1+"); //组的概念 前一个任意字符  用括号括起来代表是一个规则
		  //\1反向引用一个组  如果要获取组的内容  用$1
		   for(String s:ss){
			   System.out.println(s);
		   }
		   
	   }
}
