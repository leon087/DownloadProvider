DownloadProvider
========
基于android4.4版本的DownloadProvider构建，可作为library直接供其他项目使用。

library说明:
-----
DownloadProvider-lib：基于android源码的下载模块  

使用
-----
1. Add the JitPack repository to your build file  
```groovy
allprojects {
	repositories {
		maven { url 'https://www.jitpack.io' }
	}
}
```
2. 添加依赖  
```groovy
    compile 'com.github.leon087:DownloadProvider:+'
```

3. 添加其他依赖  
```groovy
    compile 'com.google.guava:guava:19.0'
    //log
    compile "org.slf4j:slf4j-api:1.7.22"
    compile "com.android.support:support-v13:+"
```


