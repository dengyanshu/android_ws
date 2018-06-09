1、安卓单元测试不同于java
    需要模拟出上下文对象等等
2、步骤：
   清单文件目录下
   <instrumentation android:name="android.test.InstrumentationTestRunner"
               android:targetPackage="cn.yanshu">
      </instrumentation>
      
      application里面
      <uses-library android:name="android.test.runner"></uses-library>
      
      
      写一个测试类继承自androidtestcase