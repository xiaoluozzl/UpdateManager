package com.xiaoluo.updatelib;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

/**
 * 更新管理类
 *
 * author: xiaoluo
 * date: 2017/8/15 10:09
 */
public class UpdateManager {

    public static final int COMPARE_VERSION_NAME = 110;                    // 版本名比较
    public static final int COMPARE_VERSION_CODE = 120;                    // 版本号比较
    public static final String RESULT_LASTEST = "Lastest Version";         // 最新版本
    public static final String RESULT_FORCE = "Force update";              // 强制更新
    public static final String RESULT_NOT_LASTEST = "Not Lastest Version"; // 低于最新

    public static String mApkPath = "";
    public static long mApkId = -1;
    public static boolean isUpdating = false;  // 是否更新中
    private static UpdateManager mInstance;

    private Context mContext;

    private String mLastestVerName = "";                // 最新版本名
    private int mLastestVerCode = -1;                   // 最新版本号
    private String mMinVerName = "";                    // 最低版本名
    private int mMinVerCode = -1;                       // 最低版本号
    private String mCurrentVerName = "";                // 当前版本名
    private int mCurrentVerCode = -1;                   // 当前版本号
    private boolean isForce = false;                    // 是否强制更新
    private String mDownloadUrl = "";                   // 下载地址
    private String mDownloadTitle = "下载新版本中...";   // 下载标题
    private int mCompare = COMPARE_VERSION_CODE;        // 默认使用版本号检测
    private UpdateListener mListener;

    private UpdateManager() {

    }

    /**
     * 单例创建,必备
     */
    public static UpdateManager getInstance() {
        if (mInstance == null) {
            mInstance = new UpdateManager();
        }
        return mInstance;
    }

    /**
     * 初始化,必备
     */
    public UpdateManager init(Context context) {
        this.mContext = context;
        mCurrentVerName = LibUtils.getVersionName(mContext);
        mCurrentVerCode = LibUtils.getVersionCode(mContext);
        return this;
    }

    /**
     * 最新版本名
     */
    public UpdateManager lastestVerName(String versionName) {
        this.mLastestVerName = versionName;
        return this;
    }

    /**
     * 最新版本号
     */
    public UpdateManager lastestVerCode(int versionCode) {
        this.mLastestVerCode = versionCode;
        return this;
    }

    /**
     * 最低版本名
     */
    public UpdateManager minVerName(String versionName) {
        this.mMinVerName = versionName;
        return this;
    }

    /**
     * 最低版本号
     */
    public UpdateManager minVerCode(int versionCode) {
        this.mMinVerCode = versionCode;
        return this;
    }

    /**
     * 忽略版本名和版本号,强制更新
     */
    public UpdateManager isForce(boolean isForce) {
        this.isForce = isForce;
        return this;
    }

    /**
     * 通过版本名或版本号比较更新版本
     * 默认通过版本号
     */
    public UpdateManager compare(int compare) {
        this.mCompare = compare;
        return this;
    }

    /**
     * 下载地址,必备
     */
    public UpdateManager downloadUrl(String url) {
        this.mDownloadUrl = url;
        return this;
    }

    /**
     * 下载通知栏标题
     */
    public UpdateManager downloadTitle(String title) {
        this.mDownloadTitle = title;
        return this;
    }

    /**
     * 开始逻辑,必备
     */
    public UpdateManager update() {
        if (isUpdating) {
            Toast.makeText(mContext, "正在更新中...", Toast.LENGTH_SHORT).show();
            return this;
        } else {
            checkUpdate();
            return this;
        }
    }

    /**
     * 设置更新检测回调
     */
    public UpdateManager setListener(UpdateListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        // 跳过版本对比，强制更新
        if (isForce) {
            beginUpdate(true);
            if (mListener != null) {
                mListener.onCheckResult(RESULT_FORCE);
            }
            return;
        }
        switch (mCompare) {
            case COMPARE_VERSION_CODE:
                compareVerCode();
                break;
            case COMPARE_VERSION_NAME:
                compareVerName();
                break;
            default:
                compareVerCode();
                break;
        }
    }

    /**
     * 比较版本号
     */
    private void compareVerCode() {
        if (mCurrentVerCode < mMinVerCode) {
            beginUpdate(true);
            if (mListener != null) {
                mListener.onCheckResult(RESULT_FORCE);
            }
            return;
        }
        if (mCurrentVerCode < mLastestVerCode) {
            beginUpdate(false);
            if (mListener != null) {
                mListener.onCheckResult(RESULT_NOT_LASTEST);
            }
        } else {
            if (mListener != null) {
                mListener.onCheckResult(RESULT_LASTEST);
            }
        }
    }

    /**
     * 比较版本名
     */
    private void compareVerName() {
        // 版本名小于最低,强制更新
        if (!TextUtils.isEmpty(mMinVerName)) {
            int min = LibUtils.compareVersion(mCurrentVerName, mMinVerName);
            if (min < 0) {
                beginUpdate(true);
                if (mListener != null) {
                    mListener.onCheckResult(RESULT_FORCE);
                }
                return;
            }
        }

        // 版本名小于最新,提示更新
        int last = LibUtils.compareVersion(mCurrentVerName, mLastestVerName);
        if (last < 0) {
            beginUpdate(false);
            if (mListener != null) {
                mListener.onCheckResult(RESULT_NOT_LASTEST);
            }
        } else {
            if (mListener != null) {
                mListener.onCheckResult(RESULT_LASTEST);
            }
        }
    }

    /**
     * 开始更新
     * @param forceUpdate 是否强制更新
     */
    private void beginUpdate(final boolean forceUpdate) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "请申请读写SD卡权限", Toast.LENGTH_SHORT).show();
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog(mContext);
        if (forceUpdate) {
            dialog.setMessage("您的版本过低,请更新")
                    .setLeftText("退出程序")
                    .setRightText("立即更新")
                    .setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        } else {
            dialog.setMessage("发现新版本\n是否现在更新?")
                    .setLeftText("稍后更新")
                    .setRightText("立即更新");
        }
        dialog.setOnSelectListener(new ConfirmDialog.OnSelectListener() {
            @Override
            public void onLeftSelect() {
                if (forceUpdate) {
                    ((Activity) mContext).finish();
                    System.exit(0);
                }
            }

            @Override
            public void onRightSelect() {
                download();
            }
        }).show();
    }

    /**
     * 下载
     */
    private void download() {
        if (TextUtils.isEmpty(mDownloadUrl)) {
            return;
        }

        String filePath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//外部存储卡
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            Toast.makeText(mContext, "没有SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
        mApkPath = filePath + File.separator + "update.apk";
        File file = new File(mApkPath);
        if (file.exists()) {
            file.delete();
        }
        Uri fileUri = Uri.parse("file://" + mApkPath);

        Uri uri = Uri.parse(mDownloadUrl);
        DownloadManager downloadManager = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(mDownloadTitle);
        request.setDestinationUri(fileUri);
        mApkId = downloadManager.enqueue(request);
        isUpdating = true;
    }

    /**
     * 更新检测回调
     */
    public interface UpdateListener {
        void onCheckResult(String result);
    }
}
