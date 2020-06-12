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
            /**
             * 授权成功
             * @param requestCode 请求码
             */
            @Override
            public void onPermissionGranted(int requestCode) {
                // TODO 权限授权成功，执行授权后的操作
            }

            /**
             * 授权失败
             * @param requestCode 请求码
             * @param denied      授权被拒的权限（包含被拒且不再询问的权限）
             * @param rationale   当且仅当权限授权被拒且不再询问时返回
             */
            @Override
            public void onPermissionDenied(int requestCode, List<String> denied, List<String> rationale) {
                // TODO 权限授权失败。当且仅当授权被拒且不再询问时，需要弹窗提示
                if (rationale != null) {
                    // TODO 弹窗提示被拒，需要前往应用设置手动打开相应权限，示例如下，自行完善：
                    Context context = MainActivity.this;
                    AlertDialog dialog = new AlertDialog.Builder(context).create();
                    dialog.setTitle("授权失败");
                    dialog.setMessage("权限被禁用，是否前往应用设置打开权限！");
                    dialog.setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    dialog.setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 前往应用设置页面
                                    mPermissionGrant.toSettings();
                                }
                            });
                    dialog.show();
                }
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
