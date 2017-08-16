# UpdateManager

- 1.在app的build.gradle中添加依赖
```
compile 'cn.xiaoluo:update-manager:1.1.0'
```
- 2.参数介绍
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
- 3.简单使用
```
 UpdateManager.getInstance().init(this)
                .downloadUrl("http://aaa.apk")
                .lastestVerCode(2)
                .update();
```