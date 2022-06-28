# Signal
Android信号处理，兼容native crash 与anr，提供安全气囊方案与监控方案的基础设施，开发你的脑洞吧！
## 详细介绍
https://juejin.cn/post/7114181318644072479

## 使用说明
### 本地使用
该项目可以用于本地配置使用，只需拷贝lib_signal这个module到自己的项目即可，请按照以下条件使用

1.拷贝lib_signal这个module到自己的项目

2.调用SignalController(context).initWithSignals 初始化想要监听的信号，参数是int数值，内容为具体的信号值，比如

```
SignalController(this).initWithSignals(intArrayOf(SignalConst.SIGQUIT,SignalConst.SIGABRT))

```
3.创建一个实现CallOnCatchSignal接口的类，重写onCatchSignal方法，里面是自定义的信号处理逻辑

4.创建一个路径为resources/META-INF/services的目录，并在目录下创建一个文件，名称为com.example.lib_signal.CallOnCatchSignal，内容为继承了
CallOnCatchSignal接口的实现类路径名称，如app例子项目所示。




## 项目层级介绍
* **app下是使用例子**
* **lib_signal 是Signal的封装实现**

## 环境准备
建议直接用最新的稳定版本Android Studio打开工程。目前项目已适配`Android Studio Arctic Fox | 2020.3.1`
### 
