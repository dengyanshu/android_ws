package com.deng.fanxing;

public class BaseDao {
	
  @Author(name="xiaoxiao",age=22,sex="ÄÐ")
   public  <T> T  findbyId(int id,Class<T> calzz){
	   System.out.println("exec  ......");
	   T t=null;
	try {
		t = calzz.newInstance();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
	   return  t;
   }
}
