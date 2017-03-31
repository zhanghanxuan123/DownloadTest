package com.zhx.downloadtest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zhx.downloadtest.AppUtil.sApp;

public class MainActivity extends AppCompatActivity{
    private static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
    Button mButton;
    public static final String PACKAGE_URL = "http://on2ekkj4q.bkt.clouddn.com/download_demo.apk";
    private DownloadingDialog mDownloadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadApkFile(PACKAGE_URL);
            }
        });
        String appName = null;
        try {
            appName = sApp.getPackageManager().getPackageInfo(sApp.getPackageName(), 0).applicationInfo.loadLabel(sApp.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("zhang22",appName);
        RxUtil.getDownloadEventObservable()
                .subscribe(new Action1<DownloadProgressEvent>() {
                    @Override
                    public void call(DownloadProgressEvent downloadProgressEvent) {
                        if (mDownloadingDialog != null && mDownloadingDialog.isShowing() && downloadProgressEvent.isNotDownloadFinished()) {
                            mDownloadingDialog.setProgress(downloadProgressEvent.getProgress(), downloadProgressEvent.getTotal());
                        }
                    }
                });
    }
    public void downloadApkFile(final String url) {
        Observable<ResponseBody>observable = DownloadHttp.getInstance().getDownloadApi().downloadFile(url);
        observable.map(new Func1<ResponseBody, File>() {
                    @Override
                    public File call(ResponseBody responseBody) {
                        return StorageUtil.saveApk(responseBody.byteStream());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onStart() {
                        showDownloadingDialog();
                    }
                    @Override
                    public void onCompleted() {
                        dismissDownloadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissDownloadingDialog();
                        Log.d("zhang",e.toString());
                    }

                    @Override
                    public void onNext(File file) {
                        installApk(file);
                        Log.d("zhang",file.getName());
                    }
                });
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadingDialog() {
        if (mDownloadingDialog == null) {
            mDownloadingDialog = new DownloadingDialog(this);
        }
        mDownloadingDialog.show();
    }

    /**
     * 隐藏下载对话框
     */
    private void dismissDownloadingDialog() {
        if (mDownloadingDialog != null) {
            mDownloadingDialog.dismiss();
        }
    }

    /**
     * 安装 apk 文件
     *
     * @param apkFile
     */
    public static void installApk(File apkFile) {
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installApkIntent.setDataAndType(Uri.fromFile(apkFile), MIME_TYPE_APK);

        if (sApp.getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
            sApp.startActivity(installApkIntent);
        }
    }
}
