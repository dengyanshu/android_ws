package com.deng.observer;


public class observerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
      
         
         WeatherStation  ws=new WeatherStation();
         ws.addreceieve(new Worker());
         ws.addreceieve(new Student());
         ws.wsWork();
//         
	}

}
