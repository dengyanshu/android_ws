package com.deng.observer;

public class Student implements WeatherReceive_interface {

	@Override
	public void after_notify(String weather) {
		// TODO Auto-generated method stub
		if("����".equalsIgnoreCase(weather))
			System.out.println("ѧ������ȥ�Ͽ�");
		if("����".equalsIgnoreCase(weather))
			System.out.println("ѧ������ȥ�Ͽ�");
	}

}
