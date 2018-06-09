package com.deng.proxy;

public class UserDaoProxy implements IUserDao {
   private  IUserDao  target;
   
	
	public UserDaoProxy(IUserDao target) {
	   super();
	   this.target = target;
    }


	@Override
	public void save() {
		// TODO Auto-generated method stub
		System.out.println("模拟hibernate开启事务");
		target.save();//这个目标对象  通过构造函数传入 由于同实现一接口  所有都有保存方法  在代理对象中调用目标对象的保存方法
		System.out.println("模拟hibernate提交事务");
	}

}
