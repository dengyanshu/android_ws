1��URL url=new URL("") 
  url.openConnection 
  conn.getInputStream

  
  2��android  bitmap
    Bitmap bitmap=BitmapFactory.decodeStream(inputstream)
 
 
 3����׿���߳� ���̵߳Ľ��� handler
     private Handler myHandler=new Handler(){
          handleMessage(Message msg){
          
          }
     
     }
     
         ���߳� д����
      a��Message.obtain(myhandler,what,msg).sendTraget();
      b��myhandler.post(runnable)
      c��runonmainthread(runnable)
      b��cд��һ�� ������ӵ�ҵ�����ʹ��aд��
      
  4��cache  ���ڰ�׿
     this.getCacheDir() ��ȡ��ǰӦ�õĻ���Ŀ¼ ����filesĿ¼  /data/data/cn.yanshu
      