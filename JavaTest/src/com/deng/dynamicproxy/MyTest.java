package com.deng.dynamicproxy;

import org.junit.Test;

public class MyTest {
   @Test
   public void  test1(){
	   
	   IUserDao  userDao=(IUserDao) new  ProxyFactory(new  UserDao()).getInstance();
	   
	 
	   userDao.save();
   }
}
