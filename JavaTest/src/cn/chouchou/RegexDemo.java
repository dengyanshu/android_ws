package cn.chouchou;

import org.junit.Test;

/*
 * �����
 * 1��ƥ��  �����ַ�ƥ�� ƥ����� �򷵻�
 * 2���и� �滻  ��ȡ ��ע�������е�Ĭ����̰����  ��ʱ�����ҵ����Ҫ��ֹ̰���ã���
 * */

public class RegexDemo {
   
	
	    @Test
	   public   void  splitDemo(){
		  String  str="dadddlkljoooolpo";
		  String[] ss= str.split("(.)\\1+"); //��ĸ��� ǰһ�������ַ�  ������������������һ������
		  //\1��������һ����  ���Ҫ��ȡ�������  ��$1
		   for(String s:ss){
			   System.out.println(s);
		   }
		   
	   }
}
