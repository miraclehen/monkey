package com.jackson.monkey.engine.impl;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.jackson.monkey.engine.ImageLoadEngine;

/**
 * author: hd
 * since: 2017/12/15
 */
public class GlideEngine implements ImageLoadEngine {


    /**
     * 加载缩略图
     *
     * @param context
     * @param uri
     * @param target
     */
    @Override
    public void loadThumbnail(Context context, Uri uri, ImageView target) {
        Glide.with(context)
                .load(uri)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(target);
    }

    /**
     * 加载大图
     *
     * @param context
     * @param uri
     * @param target
     */
    @Override
    public void loadImage(Context context, Uri uri, ImageView target) {
        Glide.with(context)
                .load(uri)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(target);
    }
}
