package com.deng.saxparse;

public class Sex {
	private String  sex;
    private Sex(String sex){
	 this.sex=sex;
	   
   }
  
   public  static  final  Sex MAN=new Sex("ÄÐ");
   public  static  final  Sex FAMALE=new Sex("Å®");
  

 @Override
	public String toString() {
		// TODO Auto-generated method stub
	 
		return this.sex;
	}

   
}
