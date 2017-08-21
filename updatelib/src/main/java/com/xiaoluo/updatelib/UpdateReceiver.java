package com.xiaoluo.updatelib;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 * 更新完成广播
 *
 * author: xiaoluo
 * date: 2017/8/15 11:22
 */
public class UpdateReceiver extends BroadcastReceiver {
    public UpdateReceiver() {

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            return;
        }
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(UpdateManager.mApkId);
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    break;
                case DownloadManager.STATUS_PENDING:
                    UpdateManager.isUpdating = true;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    UpdateManager.isUpdating = true;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    UpdateManager.isUpdating = false;
                    installApk(context);
                    break;
                case DownloadManager.STATUS_FAILED:
                    UpdateManager.isUpdating = false;
                    downloadManager.remove(UpdateManager.mApkId);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 安装软件
     */
    private void installApk(Context context) {
        if (TextUtils.isEmpty(UpdateManager.mApkPath)) {
            return;
        }

        Uri uri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider",
                    new File(UpdateManager.mApkPath));
        } else {
            uri = Uri.fromFile(new File(UpdateManager.mApkPath));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
        // 是否关闭app
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
