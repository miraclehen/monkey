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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.miraclehen.monkey.MatisseActivity;
import com.miraclehen.monkey.loader.AlbumLoader;
import com.miraclehen.monkey.loader.CaptureLoader;

import java.lang.ref.WeakReference;

public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private static final int LOADER_ID_FOR_CAPTURE = 2;
    private static final String STATE_CURRENT_SELECTION = "state_current_selection";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumCallbacks mCallbacks;
    private int mCurrentSelection;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        if (id == LOADER_ID_FOR_CAPTURE) {
            //拍照回来
            Uri uri = args.getParcelable(MatisseActivity.EXTRA_CONTENT_URI);
            String path = args.getString(MatisseActivity.EXTRA_CONTENT_PATH);
            return CaptureLoader.newInstance(context, uri, path);
        } else {
            return AlbumLoader.newInstance(context);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumReset();
    }

    public void onCreate(FragmentActivity activity, AlbumCallbacks callbacks) {
        mContext = new WeakReference<Context>(activity);
        mLoaderManager = activity.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        mCurrentSelection = savedInstanceState.getInt(STATE_CURRENT_SELECTION);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_SELECTION, mCurrentSelection);
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    public void loadAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public void reloadAlbums(){
        mLoaderManager.restartLoader(LOADER_ID, null, this);
    }

    public void loadCapture(Bundle args) {
        mLoaderManager.initLoader(LOADER_ID_FOR_CAPTURE, args, this);
    }

    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    public void setStateCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
    }

    public interface AlbumCallbacks {
        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}
