package com.deng.benlei;

public class Fu {
 // protected  Fu p;
  public  String name;
  public Fu(String name){
	  this.name=name;
  }
  public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
  public  void say(){
	  System.out.println(this);
	  System.out.println(this.getName());
  }
	
  public  static void staitc_getname(){
	  Fu f=new Fu("dandy");
	  System.out.println(f.name);
	  
	
  }
}

