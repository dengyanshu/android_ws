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
		System.out.println("ģ��hibernate��������");
		target.save();//���Ŀ�����  ͨ�����캯������ ����ͬʵ��һ�ӿ�  ���ж��б��淽��  �ڴ�������е���Ŀ�����ı��淽��
		System.out.println("ģ��hibernate�ύ����");
	}

}
