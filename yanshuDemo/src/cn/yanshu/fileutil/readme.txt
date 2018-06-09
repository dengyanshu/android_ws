安卓文件的读写：
1、安卓文本身是linux系统  安卓rom是不分盘符的 
2、安卓外置sd卡是在mnt/sdcard（不同手机可能不同）
3、我们自己开发的应用安装后  会在/data/data/cn.yanshu 生成一个应用文件夹
4、调用上下文提供的 fileopen 文件保存 会再此应用目录下再生成一个/data/data/cn.yanshu/file/file.txt  
         封装的非常好 不是 硬编码的路径
    sd卡 用environment的API  常量状态 和方法 非常方便的可以获取sd卡目录 和sd卡size大小等
5、sharedprefaceed 会在此应用目录下生成一个目录sharedpreferences 然后 文件以文件名+xml后缀保存  所以 我们不需要写文件.txt
   读 直接用sharedpreferences读  如果要写 需要用sharedpreferences获取editor接口实例
6、linux的文件权限
     第一位文件类型 
  2-4  当前用户（rwx  可读可写可执行）
  5-7  当前组
  8-10 其他用户 （其他用户 比如新建一个android工程中 直接使用硬编码路径 去读 不能读的文件 会报权限错误）
  - ---   ---   ---
     常用的Context.MODE_APPEND  是当前用户 当前用户组  是可读可写不可执行 其他用户 不可读 不可写 不可执行