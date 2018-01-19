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
package com.miraclehen.monkey.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.miraclehen.monkey.CaptureType;
import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.entity.SelectionSpec;
import com.miraclehen.monkey.utils.MediaStoreCompat;


/**
 * 加载一本相册的内容
 * Load images and videos into a single cursor.
 */
public class AlbumMediaLoader extends CursorLoader {


    private CaptureType captureType;

    private AlbumMediaLoader(Context context, String selection, String[] selectionArgs,CaptureType capture) {
        super(context, AlbumLoaderContants.QUERY_URI, AlbumLoaderContants.PROJECTION, selection, selectionArgs, AlbumLoaderContants.ORDER_BY);
        captureType = capture;
    }

    public static CursorLoader newInstance(Context context, Album album, CaptureType captureType) {
        String selection;
        String[] selectionArgs;
        CaptureType capture = CaptureType.None;
        //所有相册
        if (album.isAll()) {
            if (SelectionSpec.getInstance().onlyShowImages()) {
                //只有图片
                selection = AlbumLoaderContants.SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = AlbumLoaderContants.getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                //只有视频
                selection = AlbumLoaderContants.SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = AlbumLoaderContants.getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else {
                //包括图片和视频
                selection = AlbumLoaderContants.SELECTION_ALL;
                selectionArgs = AlbumLoaderContants.SELECTION_ALL_ARGS;
            }
            //如果是所有相册，可以出现拍摄图标功能
            capture = captureType;
        } else {
            //单一相册
            if (SelectionSpec.getInstance().onlyShowImages()) {
                //单一相册中只有图片
                selection = AlbumLoaderContants.SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = AlbumLoaderContants.getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        album.getId());
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                //单一相册中只有视频
                selection = AlbumLoaderContants.SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = AlbumLoaderContants.getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                        album.getId());
            } else {
                //单一相册中包括图片和视频
                selection = AlbumLoaderContants.SELECTION_ALBUM;
                selectionArgs = AlbumLoaderContants.getSelectionAlbumArgs(album.getId());
            }
        }
        return new AlbumMediaLoader(context, selection, selectionArgs, capture);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result = super.loadInBackground();
        //手机没有拍照功能
        if (!MediaStoreCompat.hasCameraFeature(getContext())) {
            return result;
        }
        if (captureType==CaptureType.None) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(AlbumLoaderContants.PROJECTION);
        if (captureType == CaptureType.Image) {
            //添加拍照视图
            dummy.addRow(new Object[]{MediaItem.ITEM_ID_CAPTURE,
                    MediaItem.ITEM_DISPLAY_NAME_CAPTURE, "", 0, System.currentTimeMillis(), System.currentTimeMillis(), 0D, 0D, 0L, 0L, "", 0});
        } else {
            //添加录像视图
            dummy.addRow(new Object[]{MediaItem.ITEM_ID_RECORD,
                    MediaItem.ITEM_DISPLAY_NAME_RECORD, "", 0, System.currentTimeMillis(), System.currentTimeMillis(), 0D, 0D, 0L, 0L, "", 0});
        }
        return new MergeCursor(new Cursor[]{dummy, result});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
