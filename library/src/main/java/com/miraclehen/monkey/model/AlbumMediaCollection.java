/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.miraclehen.monkey.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.miraclehen.monkey.CaptureType;
import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.loader.AlbumMediaLoader;

import java.lang.ref.WeakReference;

public class AlbumMediaCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "ARGS_ALBUM";
    private static final String ARGS_CAPTURE_TYPE = "ARGS_CAPTURE_TYPE";
    private static final String ARGS_CAPTURE_VALID = "ARGS_CAPTURE_VALID";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumMediaCallbacks mCallbacks;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }

        Album album = args.getParcelable(ARGS_ALBUM);
        if (album == null) {
            return null;
        }

        CaptureType captureType = CaptureType.valueOf(args.getString(ARGS_CAPTURE_TYPE, CaptureType.None.name()));
        return AlbumMediaLoader.newInstance(context, album, album.isAll() ? captureType : CaptureType.None);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallbacks.onAlbumMediaLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumMediaReset();
    }

    public void onCreate(@NonNull FragmentActivity context, @NonNull AlbumMediaCallbacks callbacks) {
        mContext = new WeakReference<Context>(context);
        mLoaderManager = context.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    //========================>>>>>加载部分==========

    /**
     * 加载一个Album的内容
     *
     * @param target
     */
    public void load(@Nullable Album target) {
        load(target, CaptureType.None);
    }

    /**
     * 加载一个Album的内容
     *
     * @param target
     * @param captureType
     */
    public void load(@Nullable Album target, CaptureType captureType) {
        mLoaderManager.initLoader(LOADER_ID, makeArgument(target, captureType), this);
    }

    public void restart(@Nullable Album target, CaptureType captureType){
        mLoaderManager.restartLoader(LOADER_ID, makeArgument(target, captureType), this);
    }

    private Bundle makeArgument(Album target, CaptureType captureType) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putString(ARGS_CAPTURE_TYPE, captureType.name());
        return args;
    }


    public interface AlbumMediaCallbacks {

        void onAlbumMediaLoad(Cursor cursor);

        void onAlbumMediaReset();
    }
}
