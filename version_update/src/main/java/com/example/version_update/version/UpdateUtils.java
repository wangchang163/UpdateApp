package com.example.version_update.version;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.example.version_update.R;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import static android.content.Context.NOTIFICATION_SERVICE;
import java.io.File;

/**
 * Created by Administrator on 2017/8/30.
 */

public class UpdateUtils {
    private static long lastClickTime = 0;
    private static UpdateDialog updateDialog;
    private static NotificationManager notificationManager;
    private static NotificationCompat.Builder builder;

    public static void startDownload(@NonNull String url, final Context context) {
        if (System.currentTimeMillis() - lastClickTime < 500) {
            return;
        }
        lastClickTime = System.currentTimeMillis();
        initBuilder(context);
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
        if (dirPath.length() > 0) {
            int index = url.lastIndexOf("/") + 1;
            if (index > -1 && index < url.length() - 1) {
                final String fileName = url.substring(index);
                if (fileName.length() > 0) {
                    final String filePath = dirPath + fileName;
                    final File file = new File(filePath);
                    if (file.exists()) {
                        openFile(filePath, context);
                        if (context instanceof Activity) {
                            ((Activity) context).finish();
                        }
                        return;
                    }
                    FileDownloader.setup(context);
                    FileDownloader.getImpl().create(url).setTag(fileName).setPath(filePath).setListener(new FileDownloadListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            builder.setContentText(String.format(context.getString(R.string.download_progress), 0));
                            notificationManager.notify(0, builder.build());
                            updateDialog.showHorizontal();
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                            int progress=(int)(soFarBytes * 100 / totalBytes);
                            builder.setContentText(String.format(context.getString(R.string.download_progress), progress));
                            builder.setProgress(100, progress, false);
                            notificationManager.notify(0, builder.build());
                            updateDialog.setProgress(progress);
                        }

                        @Override
                        protected void completed(final BaseDownloadTask task) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            notificationInstall(task.getPath(),context);
                            openFile(task.getPath(), context);
                            if (context instanceof Activity) {
                                ((Activity) context).finish();
                            }
                        }

                        @Override
                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            updateDialog.dismissDialog();
                            notificationManager.cancel(0);
                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            File file = new File(task.getPath());
                            if (file.exists()) {
                                file.delete();
                            }
                            notificationManager.cancel(0);
                            updateDialog.dismissDialog();
                            e.printStackTrace();
                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {
                            updateDialog.dismissDialog();
                            notificationManager.cancel(0);
                        }
                    }).start();
                }
            }

        }
    }

    /**
     * 初始化builder
     * @param context
     */
    private static void initBuilder(Context context) {
        updateDialog = new UpdateDialog(context);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setTicker(context.getString(R.string.downloading));
    }

    /**
     * 点击通知栏安装
     * @param filePath
     * @param context
     */
    private static void notificationInstall(String filePath,Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        File file = new File(filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider7.getUriForFile(context, file);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        i.setDataAndType(uri,
                "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        builder.setContentIntent(pendingIntent);
        builder.setContentText(context.getString(R.string.download_finish));
        builder.setProgress(100, 100, false);
        builder.setAutoCancel(true);
        notificationManager.notify(0, builder.build());
        updateDialog.dismissDialog();
    }

    /**
     * 打开应用
     *
     * @param filePath 文件路径
     */
    private static void openFile(String filePath, Context context) {
        if (filePath != null && filePath.length() > 0) {
            int index = filePath.lastIndexOf(".") + 1;
            if (index > -1 && index < filePath.length()) {
                String suffix = filePath.substring(index).toLowerCase();
                switch (suffix) {
                    case "apk":
                        File file = new File(filePath);
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //判断是否是AndroidN以及更高的版本
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            FileProvider7.setIntentDataAndType(context,
                                    intent, "application/vnd.android.package-archive", file, true);
                        } else {
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        }
                        context.startActivity(intent);
                        break;
                }
            }

        }
    }
}
