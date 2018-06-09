package cn.yanshu.service2;

/*
 * 
 * 想暴露给外部的方式
 * 
 *办证demo 中的banzheng()暴露 而sangna()隐藏在service的内部 
 *这种设计模式非常合理
 * */
public interface IMusicServiceBinder {
   
    public  void callbeginMusic();	
    public  void calkujinMusic();	
    public  void callhoutuiMusic();	
    public  void callendMusic();	
   
	
}
