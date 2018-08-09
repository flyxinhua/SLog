一个日志库，
- 类似 logger ，但是精简掉无用的线。
- 支持json,和xml。
- 支持显示方法名，和线程名。
- 默认打印log所在的文件和行数，支持直接点击行数跳转到文件中。
- 如果不提供TAG,默认使用类名来做TAG。
- 支持打印数组
- 支持直接打印对象，默认调用对象的toString 方法。 
使用方式 

```java 
                Builder builder = new Builder(
        //                this
                        new FilePrinter(Log.INFO,path,2,false)  // 一个写文件的Printer ,可以自己实现。
                        ,true,"",false);

	SLog.init(builder);
        SLog.i(“this is a log utils.”);
        SLog.w(“this is a log utils.”);
        SLog.e(“this is a log utils.”);
       
```
