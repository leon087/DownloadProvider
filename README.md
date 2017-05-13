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
//jitpack默认group为:com.github.{username}
compile '{group}:DownloadProvider:{latest_version}'
```

3. 添加其他依赖  
```groovy  
compile 'com.google.guava:guava:{latest_version}'  
//log  
compile "org.slf4j:slf4j-api:{latest_version}"  
compile "com.android.support:support-v13:{latest_version}"  
```
4. 修改Provider  
```xml  
<manifest xmlns:tools="http://schemas.android.com/tools">

    <application>
        <provider
            android:name="cm.android.download.providers.downloads.DownloadProvider"
            android:authorities="${applicationId}.download.provider"
            tools:replace="android:authorities"/>
    </application>
</manifest>
```

