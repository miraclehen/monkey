package com.miraclehen.monkey.loader;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.miraclehen.monkey.CaptureType;
import com.miraclehen.monkey.entity.Album;

/**
 * 加载单一张的图片或者视频文件
 * 用在拍照或者录制视频之后
 * <p>
 * author: miraclehen
 * since: 2018/1/18
 */
public class CaptureAlbumMediaLoader extends CursorLoader {

    public CaptureAlbumMediaLoader(Context context, String[] selectionArgs) {
        super(context, AlbumLoaderContants.QUERY_URI, AlbumLoaderContants.PROJECTION, AlbumLoaderContants.SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
                , selectionArgs, AlbumLoaderContants.ORDER_BY);
    }

    public static CursorLoader newInstance(Context context, Album album, CaptureType captureType) {
        String[] selectionArgs;
        if (captureType == CaptureType.Image) {
            selectionArgs = AlbumLoaderContants.getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                    album.getId());
        } else {
            selectionArgs = AlbumLoaderContants.getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                    album.getId());
        }
        return new CaptureAlbumMediaLoader(context,selectionArgs);
    }

}
