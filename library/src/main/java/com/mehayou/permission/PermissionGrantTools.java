package com.mehayou.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

class PermissionGrantTools {

    static boolean hasSelfPermissions(Context context, String[] permissions) {
        boolean hasSelfPermissions = true;
        if (context != null && permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                int check = PermissionChecker.checkSelfPermission(context, permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    hasSelfPermissions = false;
                }
            }
        }
        return hasSelfPermissions;
    }

    static boolean requestPermissions(Object obj, String[] permissions, int requestCode) {
        if (obj != null && permissions != null && permissions.length > 0) {
            if (obj instanceof Fragment) {
                ((Fragment) obj).requestPermissions(permissions, requestCode);
                return true;
            } else if (obj instanceof Activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) obj).requestPermissions(permissions, requestCode);
                } else {
                    ActivityCompat.requestPermissions((Activity) obj, permissions, requestCode);
                }
                return true;
            }
        }
        return false;
    }

    static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> list = null;
        if (context != null && permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                int check = PermissionChecker.checkSelfPermission(context, permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(permission);
                }
            }
        }
        return list;
    }

    static List<String> getUnRationalePermissions(Activity activity, String[] permissions) {
        List<String> list = null;
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                int check = PermissionChecker.checkSelfPermission(activity, permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                        // 不再询问
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(permission);
                    }
                }
            }
        }
        return list;
    }

    static void toSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }
}
