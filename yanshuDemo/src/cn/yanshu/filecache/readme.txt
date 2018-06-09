1、URL url=new URL("") 
  url.openConnection 
  conn.getInputStream

  
  2、android  bitmap
    Bitmap bitmap=BitmapFactory.decodeStream(inputstream)
 
 
 3、安卓主线程 子线程的交互 handler
     private Handler myHandler=new Handler(){
          handleMessage(Message msg){
          
          }
     
     }
     
         子线程 写法：
      a、Message.obtain(myhandler,what,msg).sendTraget();
      b、myhandler.post(runnable)
      c、runonmainthread(runnable)
      b与c写法一样 如果复杂的业务必须使用a写法
      
  4、cache  用于安卓
     this.getCacheDir() 获取当前应用的缓存目录 类似files目录  /data/data/cn.yanshu
      