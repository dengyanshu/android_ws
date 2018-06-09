package com.deng.fanxing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/*
 *�Զ���ע���� *
 *��׶�
 */
@Target({TYPE,FIELD , METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
@Retention(RetentionPolicy.CLASS)
public @interface Author {
   
	String name()  default  "dengfeng";
	int  age()  default  25;
	String sex();
}
