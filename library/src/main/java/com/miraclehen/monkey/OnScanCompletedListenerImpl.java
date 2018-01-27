package com.miraclehen.monkey;

import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * author: hd
 * since: 2018/1/10
 */
public class OnScanCompletedListenerImpl implements MediaScannerConnection.OnScanCompletedListener {

    private ScanCompleteCallback acceptCallback;

    public interface ScanCompleteCallback{
        void callback(String path, Uri uri);
    }

    public OnScanCompletedListenerImpl(ScanCompleteCallback callback) {
        this.acceptCallback = callback;
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        if (acceptCallback != null) {
            acceptCallback.callback(path,uri);
        }

    }

    public void clearCallback() {
        acceptCallback = null;
    }
}
