package com.deng.proxy;

import org.junit.Test;

public class MyTest {
   @Test
   public void  test1(){
	   IUserDao  userDao=new  UserDaoProxy(new  UserDao()) ;
	   userDao.save();
   }
}
