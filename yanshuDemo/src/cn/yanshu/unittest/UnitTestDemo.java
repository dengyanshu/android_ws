package cn.yanshu.unittest;

import android.test.AndroidTestCase;
import junit.framework.Assert;


public class UnitTestDemo extends  AndroidTestCase{
	
     public void  testSum(){
    	int res =sumFunction(5,3);
    	Assert.assertEquals(8, res);
     }
     
     public  int  sumFunction(int  a ,int b){
    	 return  a-b;
     }
}
