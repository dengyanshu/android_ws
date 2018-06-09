package com.zowee.mes.utils;


public class SnRuleUtil {
	
	
	private  SnRuleUtil(){}
	
	public static String getLotsn(int rule,String lotsn){
			if(rule==1){
				if(lotsn.indexOf(",")!=-1){
				       return  lotsn.substring(0, lotsn.indexOf(","));
				}
				return lotsn;
			}
			else if(rule==2){
				//[)>06SMJ1630M00100061P03011BBG2PB
				if(lotsn.indexOf("1P")==20){
				      return  lotsn.substring(lotsn.indexOf("S")+1,lotsn.indexOf("S")+15);
				}
				return lotsn;
			}
			else{
			        return lotsn;
			}
	}
}
	
