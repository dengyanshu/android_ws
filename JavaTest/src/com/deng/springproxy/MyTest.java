package com.deng.springproxy;

import org.junit.Test;

public class MyTest {
   @Test
   public void  test1(){
	   
	  UserDao userdao_paorxy=(UserDao) new SpringProxy(new UserDao()).getProxyInstance();
	   
	 
	  userdao_paorxy.save();
   }
}
