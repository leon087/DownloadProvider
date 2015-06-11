# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#避免使用泛型的位置混淆后出现类型转换错误:
-keepattributes Signature

 #混淆时是否记录日志
-verbose

#保留源码的行号、源文件信息
-renamesourcefileattribute Source
-keepattributes SourceFile,LineNumberTable

#忽略警告：
-ignorewarnings

#不优化输入的类文件
-dontoptimize

#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

#优化：设置混淆的压缩比率 0 ~ 7 
-optimizationpasses 5

-allowaccessmodification

#不预校验
-dontpreverify

#不使用混合的类名:混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames

#不要跳过非公共类库:如果应用程序引入的有jar包,并且想混淆jar包里面的class
-dontskipnonpubliclibraryclasses

#保留注解:
-keepattributes *Annotation*

#所有activity的子类不要去混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keepclassmembers class * extends android.content.Context {  
   public void *(android.view.View);  
   public void *(android.view.MenuItem);  
}

# For nativeimpl methods, see http://proguard.sourceforge.net/manual/examples.html#nativeimpl
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
#保留枚举类型成员的方法：
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留资源文件
-keepclassmembers class **.R$* {  
    public static <fields>;  
}

#保留View子类读取XML的构造方法：
-keep public class * extends android.view.View {  
    public <init>(android.content.Context);  
    public <init>(android.content.Context, android.util.AttributeSet);  
    public <init>(android.content.Context, android.util.AttributeSet, int);  
    public void set*(...);  
}
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留WebView中定义的与JS交互的类:
-keepattributes JavascriptInterface
-keep public class com.mypackage.MyClass$MyJavaScriptInterface
-keep public class * implements com.mypackage.MyClass$MyJavaScriptInterface
-keepclassmembers class com.mypackage.MyClass$MyJavaScriptInterface { 
    <methods>; 
}

# 保留WebView中定义的与JS交互的类:
-keepattributes JavascriptInterface
-keep public class com.mypackage.MyClass$MyJavaScriptInterface
-keep public class * implements com.mypackage.MyClass$MyJavaScriptInterface
-keepclassmembers class com.mypackage.MyClass$MyJavaScriptInterface { 
    <methods>; 
}

#保留JSON、Parcelable、Serailizable相关API:
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {  
    static final long serialVersionUID;  
    private static final java.io.ObjectStreamField[] serialPersistentFields;  
    private void writeObject(java.io.ObjectOutputStream);  
    private void readObject(java.io.ObjectInputStream);  
    java.lang.Object writeReplace();  
    java.lang.Object readResolve();  
} 

#去除调试日志，将所有Log.d()改为Log.i():
#-assumenosideeffects class android.util.Log{
#  public static *** d(...); 
#  public static *** i(...);
#}

#================================框架======================================

# Keep our interfaces so they can be used by other ProGuard rules.
-keep,allowobfuscation @interface cm.java.proguard.annotations.Keep
-keep,allowobfuscation @interface cm.java.proguard.annotations.KeepAll
-keep,allowobfuscation @interface cm.java.proguard.annotations.KeepGettersAndSetters

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @cm.java.proguard.annotations.Keep class *

-keep @cm.java.proguard.annotations.KeepAll class * { *; }

-keepclassmembers class * {
    @cm.java.proguard.annotations.Keep *;
}

-keepclassmembers @cm.java.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

#保留class名字的时候同时混淆该class
-keepnames class cm.android.thread.ThreadPool

-dontwarn android.support.**
-keep class * extends android.support.**
-keep class * extends android.app.**
-keep class android.support.** { *; }

-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }
-dontwarn ch.qos.logback.core.net.*

#================================框架======================================