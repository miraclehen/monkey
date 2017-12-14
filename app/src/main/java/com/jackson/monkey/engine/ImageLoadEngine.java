package com.jackson.monkey.engine;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

/**
 * author: hd
 * since: 2017/12/15
 *
 * 图片加载引擎
 */
public interface ImageLoadEngine {

    /**
     * 加载缩略图
     * @param context
     * @param uri
     */
    void loadThumbnail(Context context, Uri uri, ImageView target);

    /**
     * 加载大图
     * @param context
     * @param uri
     */
    void loadImage(Context context,Uri uri,ImageView target);
}
