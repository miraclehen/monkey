/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jackson.monkey.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;


import com.jackson.monkey.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaStoreCompat {

    private static final String BUNDLE_KEY_MEDIA_STORE_COMPAT_URI = "bundle_key_media_store_compat";
    private static final String BUNDLE_KEY_MEDIA_STORE_COMPAT_PATH = "bundle_key_media_store_compat_path";

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private CaptureStrategy mCaptureStrategy;
    private Uri mCurrentPhotoUri;
    private String mCurrentPhotoPath;



    public enum CaptureType {
        Image,
        Video,
    }

    public MediaStoreCompat(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    public MediaStoreCompat(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Checks whether the device has a camera feature or not.
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    public static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void setCaptureStrategy(CaptureStrategy strategy) {
        mCaptureStrategy = strategy;
    }

    /**
     * 启动拍照或者录像功能
     *
     * @param context
     * @param requestCode
     */
    public void dispatchCaptureIntent(Context context, int requestCode, CaptureType type) {
        Intent captureIntent = type == CaptureType.Image
                ? new Intent(MediaStore.ACTION_IMAGE_CAPTURE) : new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
            File targetFile = null;
            try {
                targetFile = createFile(type);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (targetFile != null) {
                //获取文件绝对路径
                mCurrentPhotoPath = targetFile.getAbsolutePath();
                //获取文件对应的Uri
                mCurrentPhotoUri = FileProvider.getUriForFile(mContext.get(),
                        mCaptureStrategy.authority, targetFile);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    List<ResolveInfo> resInfoList = context.getPackageManager()
                            .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        context.grantUriPermission(packageName, mCurrentPhotoUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                if (mFragment != null) {
                    mFragment.get().startActivityForResult(captureIntent, requestCode);
                } else {
                    mContext.get().startActivityForResult(captureIntent, requestCode);
                }
            }
        }
    }

    /**
     * 创建文件
     *
     * @return
     * @throws IOException
     */
    private File createFile(CaptureType type) throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "";
        if (type == CaptureType.Image) {
            fileName = String.format("JPEG_%s.jpg", timeStamp);
        } else {
            fileName = String.format("VIDEO_%s.mp4", timeStamp);
        }
        File storageDir;
        if (mCaptureStrategy.isPublic) {
            if (type == CaptureType.Image) {
                storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
            }else {
                storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES);
            }

        } else {
            storageDir = mContext.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }

        // Avoid joining path components manually
        File tempFile = new File(storageDir, fileName);

        // Handle the situation that user's external storage is not ready
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }

        return tempFile;
    }


    public Uri getCurrentPhotoUri() {
        return mCurrentPhotoUri;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_KEY_MEDIA_STORE_COMPAT_URI,mCurrentPhotoUri);
        outState.putString(BUNDLE_KEY_MEDIA_STORE_COMPAT_PATH,mCurrentPhotoPath);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        if (savedInstanceState.getParcelable(BUNDLE_KEY_MEDIA_STORE_COMPAT_URI) != null) {
            mCurrentPhotoUri = savedInstanceState.getParcelable(BUNDLE_KEY_MEDIA_STORE_COMPAT_URI);
        }
        if (!TextUtils.isEmpty(savedInstanceState.getString(BUNDLE_KEY_MEDIA_STORE_COMPAT_PATH))) {
            mCurrentPhotoPath = savedInstanceState.getString(BUNDLE_KEY_MEDIA_STORE_COMPAT_PATH);
        }
    }
}
