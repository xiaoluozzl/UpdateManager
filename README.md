# UpdateManager

- 1.在app的build.gradle中添加依赖
```
compile 'cn.xiaoluo:update-manager:1.2.0'
```
- 2.AndroidManifest里进行注册Receiver和FileProvider，Receiver不修改，FileProvider需要把android:authorities中的[包名]替换成应用包名
```
        <receiver
            android:name="com.xiaoluo.updatelib.UpdateReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.DOWNLOAD_COMPLETE"/>
                <action android:name="android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED"/>
            </intent-filter>
        </receiver>

        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="[包名].fileprovider"
            android:grantUriPermissions="true"
            android:exported="false">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
```
- 参数介绍
```
        UpdateManager.getInstance().init(this)               // 获取实例并初始化,必要
                .compare(UpdateManager.COMPARE_VERSION_NAME) // 通过版本号或版本名比较,默认版本号
                .downloadUrl("http://aaa.apk")               // 下载地址,必要
                .downloadTitle("我在下载xxxb了")              // 下载标题
                .lastestVerName("1.0")                       // 最新版本名
                .lastestVerCode(0)                           // 最新版本号
                .minVerName("1.0")                           // 最低版本名
                .minVerCode(1)                               // 最低版本号
                .isForce(true)                               // 是否强制更新,true无视版本直接更新
                .update()                                    // 开始更新
                // 设置版本对比回调
                .setListener(new UpdateManager.UpdateListener() {
                    @Override
                    public void onCheckResult(String result) {
                        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                    }
                });
```
- 简单使用
```
 UpdateManager.getInstance().init(this)
                .downloadUrl("http://aaa.apk")
                .lastestVerCode(2)
                .update();
```

- 4.版本对比：

版本名：采用版本管理格式(如1.0.0)，依次对比各个数字，越往前决定权越高，如2.0.1 > 1.9.9
当前前面各数字均一致时，以更长的为高版本，如1.2.2.1 > 1.2.2

版本号：数值高的为高版本

- 5.具体实现可参考[一行代码实现Android app内部更新](http://www.jianshu.com/p/e8449ea77280)