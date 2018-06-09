package com.deng.observer;

public class Student implements WeatherReceive_interface {

	@Override
	public void after_notify(String weather) {
		// TODO Auto-generated method stub
		if("晴天".equalsIgnoreCase(weather))
			System.out.println("学生晴天去上课");
		if("下雨".equalsIgnoreCase(weather))
			System.out.println("学生下雨去上课");
	}

}
