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
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.entity.SelectionSpec;
import com.miraclehen.monkey.utils.MediaStoreCompat;


/**
 * Load images and videos into a single cursor.
 */
public class AlbumMediaLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DATA,
            "duration"};

    // === params for album ALL && showSingleMediaType: false ===
    private static final String SELECTION_ALL =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    // ===========================================================

    // === params for album ALL && showSingleMediaType: true ===
    private static final String SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }
    // =========================================================

    // === params for ordinary album && showSingleMediaType: false ===
    private static final String SELECTION_ALBUM =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionAlbumArgs(String albumId) {
        return new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                albumId
        };
    }
    // ===============================================================

    // === params for ordinary album && showSingleMediaType: true ===
    private static final String SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionAlbumArgsForSingleMediaType(int mediaType, String albumId) {
        return new String[]{String.valueOf(mediaType), albumId};
    }
    // ===============================================================

    //    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    private static final String ORDER_BY = " CASE WHEN " + MediaStore.Images.Media.DATE_TAKEN + " == 0 "
            + " THEN " + MediaStore.Images.Media.DATE_ADDED + " ELSE " + MediaStore.Images.Media.DATE_TAKEN + " END DESC ";

    //允许拍照
    private final boolean mEnableCapture;
    //允许录像
    private final boolean mEnableRecord;

    private AlbumMediaLoader(Context context, String selection, String[] selectionArgs, boolean capture, boolean record) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
        mEnableCapture = capture;
        mEnableRecord = record;
    }

    public static CursorLoader newInstance(Context context, Album album, boolean capture, boolean record) {
        String selection;
        String[] selectionArgs;
        boolean enableCapture;
        boolean enableRecord;

        if (capture && record) {
            throw new IllegalArgumentException("cannot capture with record as same time");
        }

        //所有相册
        if (album.isAll()) {
            if (SelectionSpec.getInstance().onlyShowImages()) {
                //只有图片
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                //只有视频
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else {
                //包括图片和视频
                selection = SELECTION_ALL;
                selectionArgs = SELECTION_ALL_ARGS;
            }
            enableCapture = capture;
            enableRecord = record;
        } else {
            //单一相册
            if (SelectionSpec.getInstance().onlyShowImages()) {
                //单一相册中只有图片
                selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        album.getId());
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                //单一相册中只有视频
                selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                        album.getId());
            } else {
                //单一相册中包括图片和视频
                selection = SELECTION_ALBUM;
                selectionArgs = getSelectionAlbumArgs(album.getId());
            }
            enableCapture = false;
            enableRecord = false;
        }
        return new AlbumMediaLoader(context, selection, selectionArgs, enableCapture, enableRecord);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result = super.loadInBackground();
        //破手机没有拍照功能
        if (!MediaStoreCompat.hasCameraFeature(getContext())) {
            return result;
        }
        if (!mEnableCapture && !mEnableRecord) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(PROJECTION);
        if (mEnableCapture) {
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
