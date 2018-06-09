package com.deng.observer;

public class Worker implements WeatherReceive_interface{

	@Override
	public void after_notify(String weather) {
		// TODO Auto-generated method stub
		if("晴天".equalsIgnoreCase(weather))
			System.out.println("工人高高兴兴去上班");
		if("下雨".equalsIgnoreCase(weather))
			System.out.println("工人下雨不用去上班");
	}

}
