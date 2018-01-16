package com.miraclehen.monkey;

import android.media.MediaScannerConnection;
import android.net.Uri;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author: hd
 * since: 2018/1/10
 */
public class OnScanCompletedListenerImpl implements MediaScannerConnection.OnScanCompletedListener {

    private Consumer<String> acceptCallback;

    public OnScanCompletedListenerImpl(Consumer<String> acceptCallback) {
        this.acceptCallback = acceptCallback;
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        Observable.just(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (acceptCallback != null) {
                            acceptCallback.accept(s);
                        }
                    }
                });
    }

    public void clearCallback() {
        acceptCallback = null;
    }
}
