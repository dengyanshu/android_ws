package com.deng.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * ���ģʽ���۲���ģʽ��
 * 1�����붨��һ��ͨ�ýӿ� �����й۲��� ʵ�֣�
 * 2���۲���ʵ�ֽӿ�
 * 3�������Ҫ�����Ĺ۲���
 * 4�������֪ͨ�ӿڶ��� ����
 * 
 * */
public class WeatherStation {
	
	private  static  final String[]  weathers=new  String[]{"����","����"};
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
