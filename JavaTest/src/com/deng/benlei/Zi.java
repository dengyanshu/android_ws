package com.deng.benlei;

public class Zi extends  Fu{
       public Zi(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		
	   }

      public void say(){
	 
	   super.say();
	   System.out.println(this);
	   System.out.println(this.getName());
      }
}
