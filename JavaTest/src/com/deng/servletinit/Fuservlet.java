package com.deng.servletinit;

public class Fuservlet {
	
	
	public  void  init(String s){
		System.out.println("fu--daican"+s);
		this.init();
	}
    public  void  init(){
    	System.out.println("fu--budaican");
	}
}
