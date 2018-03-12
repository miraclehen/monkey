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
package com.miraclehen.monkey;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

import com.miraclehen.monkey.engine.ImageEngine;
import com.miraclehen.monkey.entity.CaptureStrategy;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.entity.SelectionSpec;
import com.miraclehen.monkey.filter.Filter;
import com.miraclehen.monkey.listener.CatchSpecMediaItemCallback;
import com.miraclehen.monkey.listener.InflateItemViewCallback;
import com.miraclehen.monkey.listener.OnItemCheckChangeListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_BEHIND;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LOCKED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;

/**
 * 选择偏好创建器
 * Fluent API for building media select specification.
 */
@SuppressWarnings("unused")
public final class SelectionCreator {
    private final Monkey mMatisse;
    private final SelectionSpec mSelectionSpec;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntDef({
            SCREEN_ORIENTATION_UNSPECIFIED,
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_PORTRAIT,
            SCREEN_ORIENTATION_USER,
            SCREEN_ORIENTATION_BEHIND,
            SCREEN_ORIENTATION_SENSOR,
            SCREEN_ORIENTATION_NOSENSOR,
            SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            SCREEN_ORIENTATION_FULL_SENSOR,
            SCREEN_ORIENTATION_USER_LANDSCAPE,
            SCREEN_ORIENTATION_USER_PORTRAIT,
            SCREEN_ORIENTATION_FULL_USER,
            SCREEN_ORIENTATION_LOCKED
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ScreenOrientation {
    }

    /**
     * Constructs a new specification builder on the context.
     *
     * @param matisse   a requester context wrapper.
     * @param mimeTypes MIME type set to select. 如果为true，那么在mineType显示图片和视频的情况下，用户可以同时选择图片或者视频。
     *                  false则只能选择图片或者视频其中之一
     */
    SelectionCreator(Monkey matisse, @NonNull Set<MimeType> mimeTypes) {
        mMatisse = matisse;
        mSelectionSpec = SelectionSpec.getCleanInstance();
        mSelectionSpec.mimeTypeSet = mimeTypes;
        mSelectionSpec.orientation = SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * Theme for media selecting Activity.
     * <p>
     * There are two built-in themes:
     * 1. com.zhihu.matisse.R.style.Matisse_Zhihu;
     * 2. com.zhihu.matisse.R.style.Matisse_Dracula
     * you can define a custom theme derived from the above ones or other themes.
     *
     * @param themeId theme resource id. Default value is com.zhihu.matisse.R.style.Matisse_Zhihu.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator theme(@StyleRes int themeId) {
        mSelectionSpec.themeId = themeId;
        return this;
    }

    /**
     * Show a auto-increased number or a check mark when user select media.
     *
     * @param countable true for a auto-increased number from 1, false for a check mark. Default
     *                  value is false.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator countable(boolean countable) {
        mSelectionSpec.countable = countable;
        return this;
    }

    /**
     * Maximum selectable count.
     *
     * @param maxSelectable Maximum selectable count. Default value is 1.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator maxSelectable(int maxSelectable) {
        if (maxSelectable < 1)
            throw new IllegalArgumentException("maxSelectable must be greater than or equal to one");
        mSelectionSpec.maxSelectable = maxSelectable;
        return this;
    }

    /**
     * Add filter to filter each selecting item.
     *
     * @param filter {@link Filter}
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator addFilter(@NonNull Filter filter) {
        if (mSelectionSpec.filters == null) {
            mSelectionSpec.filters = new ArrayList<>();
        }
        mSelectionSpec.filters.add(filter);
        return this;
    }


    /**
     * Capture strategy provided for the location to save photos including internal and external
     * storage and also a authority for {@link android.support.v4.content.FileProvider}.
     *
     * @param captureStrategy {@link CaptureStrategy}, needed only when capturing is enabled.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator captureStrategy(CaptureStrategy captureStrategy) {
        mSelectionSpec.captureStrategy = captureStrategy;
        return this;
    }

    /**
     * 拍摄类型
     * 默认值为{#link CaptureType.None}
     *
     * @param captureType
     * @return
     */
    public SelectionCreator captureType(CaptureType captureType) {
        mSelectionSpec.captureType = captureType;
        return this;
    }

    /**
     * 拍摄后是否直接结束返回
     * 默认值为true
     *
     * @param finishBack
     * @return
     */
    public SelectionCreator captureFinishBack(boolean finishBack) {
        mSelectionSpec.captureFinishBack = finishBack;
        return this;
    }

    /**
     * Set the desired orientation of this activity.
     *
     * @param orientation An orientation constant as used in {@link ScreenOrientation}.
     *                    Default value is {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}.
     * @return {@link SelectionCreator} for fluent API.
     * @see Activity#setRequestedOrientation(int)
     */
    public SelectionCreator restrictOrientation(@ScreenOrientation int orientation) {
        mSelectionSpec.orientation = orientation;
        return this;
    }

    /**
     * Set a fixed span count for the media grid. Same for different screen orientations.
     * <p>
     *
     * @param spanCount Requested span count.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator spanCount(int spanCount) {
        if (spanCount < 1) throw new IllegalArgumentException("spanCount cannot be less than 1");
        mSelectionSpec.spanCount = spanCount;
        return this;
    }

    /**
     * Photo thumbnail's scale compared to the View's length. It should be a float value in (0.0,
     * 1.0].
     *
     * @param scale Thumbnail's scale in (0.0, 1.0]. Default value is 0.5.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator thumbnailScale(float scale) {
        if (scale <= 0f || scale > 1f)
            throw new IllegalArgumentException("Thumbnail scale must be between (0.0, 1.0]");
        mSelectionSpec.thumbnailScale = scale;
        return this;
    }

    /**
     * Provide an image engine.
     * <p>
     * There are two built-in image engines:
     * 1. {@link com.miraclehen.monkey.engine.impl.GlideEngine}
     * 2. {@link com.miraclehen.monkey.engine.impl.PicassoEngine}
     * And you can implement your own image engine.
     *
     * @param imageEngine {@link ImageEngine}
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator imageEngine(ImageEngine imageEngine) {
        mSelectionSpec.imageEngine = imageEngine;
        return this;
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    public void forResult(int requestCode) {
        Activity activity = mMatisse.getActivity();
        if (activity == null) {
            return;
        }

        Intent intent = new Intent(activity, MonkeyActivity.class);

        Fragment fragment = mMatisse.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
        activity.overridePendingTransition(R.anim.slide_in_bottom, 0);
    }

    /**
     * 根据日期分组
     * 默认为true
     *
     * @param groupByDate true for group by date list and false for nothing
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator groupByDate(boolean groupByDate) {
        mSelectionSpec.groupByDate = groupByDate;
        return this;
    }

    /**
     * 初始化已选中的MediaItem列表
     *
     * @param list
     * @return
     */
    public SelectionCreator selectedMediaItem(List<MediaItem> list) {
        if (list == null) {
            return this;
        }
        mSelectionSpec.selectedDataList.addAll(list);
        return this;
    }

    /**
     * Item被勾选或者反勾选监听器
     *
     * @param listener 监听器
     * @return SelectionCreator
     */
    public SelectionCreator checkListener(OnItemCheckChangeListener listener) {
        mSelectionSpec.checkListener = listener;
        return this;
    }

    /**
     * 是否启动单一结果模式。
     * 如果为true，那么当选择其中一个item适合，直接结束选择。
     * 当为true的时候，{@link #captureFinishBack(boolean)}将为被设置为true，也就是拍照之后直接返回数据，不停留在MatisseActivity
     *
     * @param b
     * @return
     */
    public SelectionCreator singleResultModel(Boolean b) {
        mSelectionSpec.singleResultModel = b;
        return this;
    }

    /**
     * 顶部头部布局Id
     *
     * @param toolbarLayout
     * @return
     */
    public SelectionCreator toolbarLayoutId(@LayoutRes int toolbarLayout) {
        mSelectionSpec.toolbarLayoutId = toolbarLayout;
        return this;
    }

    /**
     * 获取日期最新的一条数据MediaItem之后回调相应的方法
     *
     * @param callback CatchSpecMediaItemCallback.newestCallback
     * @return SelectionCreator
     */
    public SelectionCreator catchNewestCallback(CatchSpecMediaItemCallback.newestCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("CatchSpecMediaItemCallback.newestCallback not be null ");
        }
        mSelectionSpec.catchNewestSpecCallback = callback;
        return this;
    }

    /**
     * 获取指定日期区间的数据MediaItem之后回调相应的方法
     *
     * @param callback CatchSpecMediaItemCallback.dateCallback
     * @return SelectionCreator
     */
    public SelectionCreator catchSpecDateCallback(CatchSpecMediaItemCallback.dateCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("CatchSpecMediaItemCallback.dateCallback not be null ");
        }
        mSelectionSpec.catchDateSpecCallback = callback;
        return this;
    }

    /**
     * 生成item布局时候回调
     * 可以对布局进行调整
     *
     * @param callback
     * @return
     */
    public SelectionCreator inflateItemViewCallback(InflateItemViewCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("InflateItemViewCallback not be null ");
        }
        mSelectionSpec.inflateItemViewCallback = callback;
        return this;
    }

    /**
     * 第一次显示数据的时候，自动滚动到相应日期
     * 如果没有匹配对应的值，那么会滚动到最近的日期值
     *
     * @param millisecond
     * @return
     */
    public SelectionCreator autoScrollToDate(long millisecond) {
        if (millisecond < 0) {
            throw new IllegalArgumentException("scrollToDate < 0");
        }
        mSelectionSpec.autoScrollDate = millisecond;
        return this;
    }
}
