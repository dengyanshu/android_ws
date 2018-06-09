listview 是安卓里面最重要的组件 没有之一

listview 需要数据设配器


继承自baseadapter  需要重写getview 和getcount方法
其中getview 方法 用于展现每一行数据：
convertview 是安卓提供的缓存 
if(convertview==null){
   view=new TextView();
}else{
   view=convertview
}
return view


常用的arrayadapter  和simpleadapter  是封装好的 有需要可以使用 用户自定义可以继承baseadaper

View.inflater
LayoutInflater
context.getSystemService()
三种方式可以把一个布局 转化成一个view对象