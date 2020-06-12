# PermissionGrant
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg)](https://android-arsenal.com/api?level=16)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://img.shields.io/github/release/mehayou/permissiongrant.svg?color=red)](https://github.com/mehayou/PermissionGrant/releases)

#### 介绍
Android6.0 简单的动态申请权限授权管理工具。

## 权限申请
* 在AndroidManifest.xml中添加需要申明权限，示例如下：
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 使用方法
实例化请求方法，如下：
```java
// 实例化
PermissionGrant mPermissionGrant = new PermissionGrant(this);
// 申请授权
mPermissionGrant.request(requestCode,
        new PermissionGrant.Callback() {
            @Override
            public void onPermissionGranted(int requestCode, List<String> granted) {
                // TODO 权限授权成功，执行授权后的操作
            }

            @Override
            public void onPermissionDenied(int requestCode, List<String> granted, List<String> denied) {
                // TODO 权限授权失败，可弹窗提示，或忽略
            }
        },
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
);
```
或者忽略授权失败，只回调授权成功
```java
// 实例化
PermissionGrant mPermissionGrant = new PermissionGrant(this);
// 申请授权
mPermissionGrant.request(requestCode,
        new PermissionGrant.GrantedCallback() {
            @Override
            public void onPermissionGranted(int requestCode, List<String> granted) {
                // TODO 权限授权成功，执行授权后的操作
            }
        },
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
);
```
注：重复申请授权方法，系统只会响应第一个。

#### 重写Activity/Fragment回调方法
```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    mPermissionGrant.onRequestPermissionsResult(requestCode, permissions, grantResults);
}
```
