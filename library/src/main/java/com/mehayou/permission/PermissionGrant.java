package com.mehayou.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Android 6.0
 * <p>
 * Manifest.permission.WRITE_CONTACTS
 * Manifest.permission.GET_ACCOUNTS
 * Manifest.permission.READ_CONTACTS
 * <p>
 * Manifest.permission.READ_CALL_LOG
 * Manifest.permission.READ_PHONE_STATE
 * Manifest.permission.CALL_PHONE
 * Manifest.permission.WRITE_CALL_LOG
 * Manifest.permission.USE_SIP
 * Manifest.permission.PROCESS_OUTGOING_CALLS
 * Manifest.permission.ADD_VOICEMAIL
 * <p>
 * Manifest.permission.READ_CALENDAR
 * Manifest.permission.WRITE_CALENDAR
 * <p>
 * Manifest.permission.CAMERA
 * <p>
 * Manifest.permission.BODY_SENSORS
 * <p>
 * Manifest.permission.ACCESS_FINE_LOCATION
 * Manifest.permission.ACCESS_COARSE_LOCATION
 * <p>
 * Manifest.permission.READ_EXTERNAL_STORAGE
 * Manifest.permission.WRITE_EXTERNAL_STORAGE
 * <p>
 * Manifest.permission.RECORD_AUDIO
 * <p>
 * Manifest.permission.READ_SMS
 * Manifest.permission.RECEIVE_WAP_PUSH
 * Manifest.permission.RECEIVE_MMS
 * Manifest.permission.RECEIVE_SMS
 * Manifest.permission.SEND_SMS
 */
public class PermissionGrant {

    private Context context;
    private Activity activity;
    private Fragment fragment;

    public PermissionGrant(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.context = fragment.getContext();
    }

    public PermissionGrant(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    private SparseArray<GrantedCallback> array = new SparseArray<>();

    public void request(int requestCode, GrantedCallback callback, String... permissions) {
        if (permissions.length > 0) {
            Context context = this.context;
            if (context != null) {
                boolean hasSelfPermissions = true;

                for (String permission : permissions) {
                    int check = PermissionChecker.checkSelfPermission(context, permission);
                    if (check != PackageManager.PERMISSION_GRANTED) {
                        hasSelfPermissions = false;
                        break;
                    }
                }

                if (!hasSelfPermissions) {
                    if (this.fragment != null) {
                        this.fragment.requestPermissions(permissions, requestCode);
                        this.array.append(requestCode, callback);
                    } else if (this.activity != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            this.activity.requestPermissions(permissions, requestCode);
                        } else {
                            ActivityCompat.requestPermissions(this.activity, permissions, requestCode);
                        }
                        this.array.append(requestCode, callback);
                    }
                } else {
                    callback.onPermissionGranted(requestCode, new ArrayList<>(Arrays.asList(permissions)));
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || permissions.length <= 0 || grantResults == null || grantResults.length <= 0) {
            return;
        }
        if (this.array.indexOfKey(requestCode) != -1) {
            GrantedCallback callback = this.array.get(requestCode);
            this.array.delete(requestCode);
            if (callback != null) {
                List<String> deniedPermissionList = null;
                List<String> grantedPermissionList = null;
                for (int i = 0; i < Math.min(permissions.length, grantResults.length); i++) {
                    int result = grantResults[i];
                    String permission = permissions[i];
                    if (PackageManager.PERMISSION_GRANTED != result) {
                        if (deniedPermissionList == null) {
                            deniedPermissionList = new ArrayList<>();
                        }
                        deniedPermissionList.add(permission);
                    } else {
                        if (grantedPermissionList == null) {
                            grantedPermissionList = new ArrayList<>();
                        }
                        grantedPermissionList.add(permission);
                    }
                }
                if (deniedPermissionList != null && !deniedPermissionList.isEmpty()) {
                    if (callback instanceof Callback) {
                        ((Callback) callback).onPermissionDenied(requestCode, grantedPermissionList, deniedPermissionList);
                    }
                } else {
                    callback.onPermissionGranted(requestCode, grantedPermissionList);
                }
            }
        }
    }

    public interface Callback extends GrantedCallback {
        void onPermissionDenied(int requestCode, List<String> granted, List<String> denied);
    }

    public interface GrantedCallback {
        void onPermissionGranted(int requestCode, List<String> granted);
    }
}
