package com.mehayou.permission.demo;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mehayou.permission.PermissionGrant;

import java.util.List;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PermissionGrant mPermissionGrant;

    private ScrollView mScrollView;
    private TextView mTextView;
    private StringBuilder mStringBuilder;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionGrant = new PermissionGrant(this);

        mScrollView = findViewById(R.id.scroll_view);
        mTextView = findViewById(R.id.text_view);

        findViewById(R.id.button_storage).setOnClickListener(this);
        findViewById(R.id.button_camera).setOnClickListener(this);
        findViewById(R.id.button_all).setOnClickListener(this);
    }

    private void appendTxt(String msg) {
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.append(msg);
        mStringBuilder.append("\n");
        mTextView.setText(mStringBuilder.toString());

        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    int offset = mTextView.getMeasuredHeight() - mScrollView.getMeasuredHeight();
                    if (offset > 0) {
                        mScrollView.scrollTo(0, offset);
                    }
                }
            };
        }
        mTextView.post(mRunnable);
    }

    private String getString(List<String> list) {
        return list == null ? "Null" : list.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                .replace(",", "\n");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_storage:
                mPermissionGrant.request(1, new PermissionGrant.Callback() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        appendTxt("--------------------");
                        appendTxt("-> Granted storage, requestCode=" + requestCode);
                    }

                    @Override
                    public void onPermissionDenied(int requestCode, List<String> denied, List<String> rationale) {
                        showDialog(rationale);
                        appendTxt("--------------------");
                        appendTxt("-> Denied storage");
                        appendTxt("-> denied : \n" + getString(denied));
                        appendTxt("-> rationale : \n" + getString(rationale));
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.button_camera:
                mPermissionGrant.request(2, new PermissionGrant.Callback() {
                    @Override
                    public void onPermissionGranted(int requestCode) {
                        appendTxt("--------------------");
                        appendTxt("-> Granted camera, requestCode=" + requestCode);
                    }

                    @Override
                    public void onPermissionDenied(int requestCode, List<String> denied, List<String> rationale) {
                        showDialog(rationale);
                        appendTxt("--------------------");
                        appendTxt("-> Denied camera");
                        appendTxt("-> denied : \n" + getString(denied));
                        appendTxt("-> rationale : \n" + getString(rationale));
                    }
                }, Manifest.permission.CAMERA);
                break;
            case R.id.button_all:
                mPermissionGrant.request(3, new PermissionGrant.Callback() {
                            @Override
                            public void onPermissionGranted(int requestCode) {
                                appendTxt("--------------------");
                                appendTxt("-> Granted all, requestCode=" + requestCode);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, List<String> denied, List<String> rationale) {
                                showDialog(rationale);
                                appendTxt("--------------------");
                                appendTxt("-> Denied all");
                                appendTxt("-> denied : \n" + getString(denied));
                                appendTxt("-> rationale : \n" + getString(rationale));
                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionGrant.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialog(List<String> rationale) {
        if (rationale != null) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle("授权失败");
            dialog.setMessage("权限被禁用，是否前往应用设置打开权限？");
            dialog.setButton(BUTTON_NEGATIVE, getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            dialog.setButton(BUTTON_POSITIVE, getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionGrant.toSettings();
                        }
                    });
            dialog.show();
        }
    }
}