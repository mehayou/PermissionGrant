package com.mehayou.permission;

import android.util.SparseArray;

class RequestArray {

    private SparseArray<PermissionGrant.Callback> array = new SparseArray<>();

    void append(int requestCode, PermissionGrant.Callback callback) {
        this.array.append(requestCode, callback);
    }

    PermissionGrant.Callback delete(int requestCode) {
        PermissionGrant.Callback callback = this.array.get(requestCode);
        if (contains(requestCode)) {
            this.array.delete(requestCode);
        }
        return callback;
    }

    boolean contains(int requestCode) {
        return this.array.indexOfKey(requestCode) != -1;
    }
}
