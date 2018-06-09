package com.deng.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {
	private  Object  target;
    
	public ProxyFactory(Object target) {
		super();
		this.target = target;
	}

	public  Object  getInstance(){
		return 
				Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), 
				 new InvocationHandler (){
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						// TODO Auto-generated method stub
						System.out.println("hibernate开启事务");
						Object obj=method.invoke(target, args);
						System.out.println("hibernate提交事务");
						return  obj;
					}
			
		    });
		
		
	}
	

}
