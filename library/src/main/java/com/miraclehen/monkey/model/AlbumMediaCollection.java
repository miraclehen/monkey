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

import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.loader.AlbumMediaLoader;

import java.lang.ref.WeakReference;

public class AlbumMediaCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "args_album";
    private static final String ARGS_ENABLE_CAPTURE = "args_enable_capture";
    private static final String ARGS_ENABLE_RECORD = "args_enable_record";
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

        return AlbumMediaLoader.newInstance(context, album,
                album.isAll() && args.getBoolean(ARGS_ENABLE_CAPTURE, false),
                args.getBoolean(ARGS_ENABLE_RECORD, false));
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

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

    public void load(@Nullable Album target) {
        load(target, false, false);
    }

    public void load(@Nullable Album target, boolean enableCapture, boolean enableRecord) {
        mLoaderManager.initLoader(LOADER_ID, makeArgument(target, enableCapture, enableRecord), this);
    }

    private Bundle makeArgument(Album target, boolean enableCapture, boolean enableRecord) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putBoolean(ARGS_ENABLE_RECORD, enableRecord);
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture);
        return args;
    }

    public void reloadForCapture(@Nullable Album target, boolean enableCapture, boolean enableRecord) {
        mLoaderManager.restartLoader(LOADER_ID, makeArgument(target, enableCapture, enableRecord), this);
    }


    public interface AlbumMediaCallbacks {

        void onAlbumMediaLoad(Cursor cursor);

        void onAlbumMediaReset();
    }
}
