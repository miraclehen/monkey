package com.miraclehen.monkey.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * author: miraclehen
 * since: 2018/3/16
 */
public class ContentProviderUtil {

    public static String getVideoPath(Uri uri, Context context){
        ContentResolver resolver=context.getContentResolver();
        Uri imageUri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String path=MediaStore.Video.Media.DATA;
        Cursor cursor=resolver.query(uri,
                new String[]{path},null,null,
                null,null);
        if (cursor.moveToNext()) {
            return cursor.getString(cursor.getColumnIndex(path));
        }else {
            return null;
        }
    }

    private static String getThumbnail(Context context,String id){
        ContentResolver resolver=context.getContentResolver();
        Uri imageUri=MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String path=MediaStore.Images.Thumbnails.DATA;
        String image_id=MediaStore.Images.Thumbnails.IMAGE_ID;
        Cursor cursor=resolver.query(imageUri,new String[]{path},image_id+"=?",new String[]{id},null);
        String str;
        while(cursor.moveToNext()){
            str=cursor.getString(cursor.getColumnIndex(path));
            return str;
        }
        return null;
    }

}
