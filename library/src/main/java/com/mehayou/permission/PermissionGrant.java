package com.mehayou.permission;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.List;

public class PermissionGrant {

    private Context context;
    private Activity activity;
    private Fragment fragment;
    private RequestArray array;

    public PermissionGrant(Fragment fragment) {
        this(fragment.getActivity());
        this.fragment = fragment;
    }

    public PermissionGrant(Activity activity) {
        this.context = activity;
        this.activity = activity;
        this.array = new RequestArray();
    }

    public void request(int requestCode, Callback callback, String... permissions) {
        this.array.delete(requestCode);
        if (permissions.length > 0) {
            Context context = this.context;
            if (context != null) {
                boolean hasSelfPermissions = PermissionGrantTools.hasSelfPermissions(context, permissions);
                if (hasSelfPermissions) {
                    callback.onPermissionGranted(requestCode);
                } else {
                    Object obj = this.fragment != null ? this.fragment : this.activity;
                    boolean request = PermissionGrantTools.requestPermissions(obj, permissions, requestCode);
                    if (request) {
                        this.array.append(requestCode, callback);
                    }
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || permissions.length <= 0 || grantResults == null || grantResults.length <= 0) {
            return;
        }
        if (this.array.contains(requestCode)) {
            Callback callback = this.array.delete(requestCode);
            if (callback != null) {
                List<String> denied = PermissionGrantTools.getDeniedPermissions(this.context, permissions);
                if (denied != null && !denied.isEmpty()) {
                    List<String> rationale = PermissionGrantTools.getUnRationalePermissions(this.activity, permissions);
                    callback.onPermissionDenied(requestCode, denied,
                            rationale != null && rationale.size() == denied.size() ? rationale : null);
                } else {
                    callback.onPermissionGranted(requestCode);
                }
            }
        }
    }

    /**
     * 前往应用设置页面
     */
    public void toSettings() {
        if (this.context != null) {
            PermissionGrantTools.toSettings(this.context);
        }
    }

    public interface Callback {
        /**
         * 授权成功
         *
         * @param requestCode 请求码
         */
        void onPermissionGranted(int requestCode);

        /**
         * 授权失败
         *
         * @param requestCode 请求码
         * @param denied      授权被拒的权限（包含被拒且不再询问的权限）
         * @param rationale   当且仅当权限授权被拒且不再询问
         */
        void onPermissionDenied(int requestCode, List<String> denied, List<String> rationale);
    }
}
