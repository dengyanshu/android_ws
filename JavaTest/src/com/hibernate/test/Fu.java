package com.hibernate.test;

public class Fu {
    private  SessionFactory  sessionFactory;
    public  void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
    public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
    public void find(){
    	System.out.println(sessionFactory.toString());
    	
    }
}
