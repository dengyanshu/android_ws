package com.deng.observer;

public class Worker implements WeatherReceive_interface{

	@Override
	public void after_notify(String weather) {
		// TODO Auto-generated method stub
		if("����".equalsIgnoreCase(weather))
			System.out.println("���˸߸�����ȥ�ϰ�");
		if("����".equalsIgnoreCase(weather))
			System.out.println("�������겻��ȥ�ϰ�");
	}

}
