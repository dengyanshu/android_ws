package com.deng.benlei;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.deng.util.WSutil;

public class Test20160811 {

	/**
	 * @param args
	 */
	
	Test20160811(){
	}
		@PostConstruct
	private void init()throws Exception{
		ParameterizedType  pt=(ParameterizedType) this.getClass().getGenericSuperclass();
		Class clazz=(Class) pt.getActualTypeArguments()[0];
		String supplier=clazz.getSimpleName();
		Field supplier_field=this.getClass().getSuperclass().getDeclaredField("supplier");
		Field basemapper_field=this.getClass().getSuperclass().getDeclaredField("baseMapper");
		basemapper_field.set(this, supplier_field.get(this));
	}
	public static void  main(String[] args){
		String s="$usercode$ daldaldadlk";
		s.replace("$usercode$", null);
		Thread  thread=new Thread(){
			public void run() {
				
			};
			
		};
		thread.start();
		
	}
		
	
	

}
