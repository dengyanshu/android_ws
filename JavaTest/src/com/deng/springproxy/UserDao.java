package com.deng.springproxy;


/**
 * cglib解决JDK动态代理无法解决的 必须实现接口的要求
 * spring框架内部会根据需要自动转换代理模式
 * */
public class UserDao {

	
	public void save() {
		// TODO Auto-generated method stub
       System.out.println("保存用户");
	}

}
