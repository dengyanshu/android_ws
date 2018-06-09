package com.deng.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * 设计模式：观察者模式：
 * 1、必须定义一个通用接口 让所有观察者 实现！
 * 2、观察者实现接口
 * 3、添加需要监听的观察者
 * 4、变更后通知接口对象 方法
 * 
 * */
public class WeatherStation {
	
	private  static  final String[]  weathers=new  String[]{"晴天","下雨"};
	private  String weather;
	
	private  static  List<WeatherReceive_interface> list;
	
	static{
		 list=new ArrayList<WeatherReceive_interface>();
	}
	static void  addreceieve(WeatherReceive_interface receive){
		 list.add(receive);
	}
	
	void wsWork(){	
		while(true){
			
		  setWeather(weathers[(int)Math.round(Math.random())]);
		  for(WeatherReceive_interface receiver:list){
		    	receiver.after_notify(weather);
		  }
			Random  random=new Random();
			int num=random.nextInt(500)+1000;
		    try {
				Thread.sleep(num);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		}
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	
	
	 
}
