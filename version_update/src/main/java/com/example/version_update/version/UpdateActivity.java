package com.example.version_update.version;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.version_update.R;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UpdateActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSION_WRITE = 1001;
    private VersionModel versionModel;

    public static void launch(Context context, VersionModel model) {
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra(Config.VERSION, model);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        versionModel = (VersionModel) getIntent().getSerializableExtra(Config.VERSION);
        checkVersion();

    }

    private void checkVersion() {
        if (versionModel != null) {
            //对比版本号，更新，versionCode自行网络获取
            if (VersionUtils.getVersionCode(this) < versionModel.versionCode) {
                checkPermission();
            } else {
                ToastUtils.with(this).show("已经是最新版本");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == PERMISSION_WRITE) {
            Log.i("permission", "申请写入权限成功");
            downloadApk();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PERMISSION_WRITE) {
            Log.i("permission", "申请写入权限失败");
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this, "请设置权限")
                        .setTitle("设置对话框")
                        .setPositiveButton("设置")
                        .setNegativeButton("取消", null)
                        .build()
                        .show();
            }
        }
    }

    private void checkPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            Log.i("permission", "已经拥有写入权限");
            downloadApk();
        } else {
            Log.i("permission", "申请写入权限");
            EasyPermissions.requestPermissions(this, "申请写入权限", PERMISSION_WRITE, permissions);
        }
    }

    //下载更新包
    private void downloadApk() {
        //判断是否强制更新
        if (versionModel.forced == 1) {
            showForcedDialog();
        } else {
            showDownloadDialog();
        }
    }

    private void showDownloadDialog() {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.update_check)).setMessage(versionModel.desc).setPositiveButton(getResources().getString(R.string.update_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkWifi();
            }
        }).setNegativeButton(getResources().getString(R.string.update_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setCancelable(false).show();
    }

    private void showForcedDialog() {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.update_check)).setMessage(versionModel.desc).setPositiveButton(getResources().getString(R.string.update_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkWifi();

            }
        }).setCancelable(false).show();
    }

    //启动下载
    private void startDownload() {
        String url = versionModel.url;
        if (TextUtils.isEmpty(url)) {
            ToastUtils.with(this).show("无效的下载地址");
            finish();
        } else {
            UpdateUtils.startDownload(url, UpdateActivity.this);
        }
    }

    private void checkWifi() {
        boolean isWifi = NetUtil.getNetworkState(this) == 1 ? true : false;
        if (isWifi) {
            startDownload();
        } else {
            showWifiDialog();
        }
    }

    private void showWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("是否要用流量进行下载更新");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startDownload();
            }
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        //设置不可取消对话框
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


}
