package com.zhx.downloadtest;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.widget.ProgressBar;

/**
 * Author   :zhx
 * Create at 2017/3/28
 * Description:
 */
public class DownloadingDialog extends AppCompatDialog {
    private ProgressBar mProgressBar;

    public DownloadingDialog(Context context) {
        super(context, R.style.AppDialogTheme);
        setContentView(R.layout.dialog_downloading);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        setCancelable(false);
    }

    public void setProgress(long progress, long maxProgress) {
        mProgressBar.setMax((int) maxProgress);
        mProgressBar.setProgress((int) progress);
    }

    @Override
    public void show() {
        super.show();
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
    }
}
