package com.deng.fanxing;

import com.deng.saxparse.Person;

public class FanxingMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        PersonDao  personDao=new PersonDao();
        Person p=personDao.findbyId(2, Person.class);
	}

}
