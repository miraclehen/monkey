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
package com.miraclehen.monkey.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.miraclehen.monkey.engine.ImageEngine;

/**
 * {@link ImageEngine} implementation using Glide.
 */

public class GlideEngine implements ImageEngine {
    private RequestOptions thumbnailOptions;
    private RequestOptions gifThumbnailOptions;

    private RequestOptions imageOptions;
    private RequestOptions gifImageOptions;

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        if (thumbnailOptions == null) {
            thumbnailOptions = new RequestOptions()
                    .placeholder(placeholder)
                    .override(resize, resize)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop();
        }
        Glide.with(context)
                .load(uri)
                .apply(thumbnailOptions)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView,
                                 Uri uri) {
        if (gifThumbnailOptions == null) {
            gifThumbnailOptions = new RequestOptions()
                    .placeholder(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(resize, resize)
                    .centerCrop();
        }
        Glide.with(context)
                .load(uri)
                .apply(gifThumbnailOptions)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, Uri uri) {
        if (imageOptions == null) {
            imageOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        }
        Glide.with(context)
                .load(uri)
                .apply(imageOptions)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, ImageView imageView, Uri uri) {
        if (gifImageOptions == null) {
            gifImageOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        }
        Glide.with(context)
                .load(uri)
                .apply(gifImageOptions)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}
