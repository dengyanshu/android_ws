package com.deng.springproxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class SpringProxy implements  MethodInterceptor{
	private  Object  target;	
	public SpringProxy(Object target) {
		super();
		this.target = target;
	}
    
	public Object getProxyInstance(){
		Enhancer enhancer=new Enhancer();
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(this);
		return  enhancer.create();
	}


	@Override
	public Object intercept(Object arg0, Method method, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("hibernate  spring 开启事务");
		Object  returnVaule=method.invoke(target, arg2);
		System.out.println("hibernate  spring 提交事务");
		return returnVaule;
	}

}
