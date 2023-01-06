# Signal
Android信号处理，兼容native crash 与anr，提供安全气囊方案与监控方案的基础设施，目前已添加native/java堆栈日志的回调，既可以当crash监控，又可以当安全气囊，开发你的脑洞吧！

更新：目前采取了新写法，去除复杂的spi调用，直接传入实现接口即可，同时核心代码迁移至c语言
## 详细介绍
https://juejin.cn/post/7114181318644072479
https://juejin.cn/post/7118609781832548383

## 使用说明
已发布到mavencenter仓库
gradle 导入
```
1.0.0-beta 暂时废弃，之后上传新版本
implementation 'io.github.TestPlanB:signal:1.0.0-beta'
```
### 本地使用
该项目可以用于本地配置使用，只需拷贝lib_signal这个module到自己的项目即可，请按照以下条件使用

1.拷贝lib_signal这个module到自己的项目

2.初始化SignalController对象，然后调用initWithSignals 初始化想要监听的信号，参数是int数组，内容为具体的信号值（如果想要监听anr，则初始化需要设置监听SIGQUIT），比如SIGQUIT，
MyHandler是一个实现CallOnCatchSignal的类，可看第3点

```
 SignalController.initSignal(intArrayOf(
            SignalConst.SIGQUIT,
            SignalConst.SIGABRT,
            SignalConst.SIGSEGV),this,MyHandler())

```
3.创建一个实现CallOnCatchSignal接口的类，如项目的MyHandler()，重写如下三个方法，里面是自定义的信号处理逻辑
```
    // 收到sigquit之后，anr的逻辑，true就是进入anr判定
    fun checkIsAnr():Boolean
    // 处理anr的逻辑 logcat是当前logcat日志
    fun handleAnr(context: Context,logcat: String)
    // 处理crash的逻辑 logcat是当前logcat日志
    fun handleCrash(context: Context,signal: Int,logcat:String)
```





## 项目层级介绍
* **app下是使用例子**
* **lib_signal 是Signal的封装实现**

## 环境准备
建议直接用最新的稳定版本Android Studio打开工程。目前项目已适配`Android Studio Arctic Fox | 2020.3.1`
### 
