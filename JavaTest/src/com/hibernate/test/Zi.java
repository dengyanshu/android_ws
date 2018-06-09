package com.hibernate.test;

public class Zi  extends  Fu {
    private  SessionFactory  sessionFactory;
//	    public final void setSessionFactory(SessionFactory sessionFactory) {
//			this.sessionFactory = sessionFactory;
//		}
//	    public SessionFactory getSessionFactory() {
//			return sessionFactory;
//		}

	public static void main(String[] args) {
	Zi zi=	new  Zi();
	zi.setSessionFactory(new SessionFactory());
	zi.find();
	}
}
