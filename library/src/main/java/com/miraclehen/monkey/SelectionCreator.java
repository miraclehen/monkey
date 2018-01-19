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
import com.miraclehen.monkey.listener.CatchSpePositionCallback;
import com.miraclehen.monkey.listener.OnExtraFileCheckListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * Fluent API for building media select specification.
 */
@SuppressWarnings("unused")
public final class SelectionCreator {
    private final Matisse mMatisse;
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
     * @param mimeTypes MIME type set to select.
     */
    SelectionCreator(Matisse matisse, @NonNull Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        mMatisse = matisse;
        mSelectionSpec = SelectionSpec.getCleanInstance();
        mSelectionSpec.mimeTypeSet = mimeTypes;
        mSelectionSpec.mediaTypeExclusive = mediaTypeExclusive;
        mSelectionSpec.orientation = SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * Whether to show only one media type if choosing medias are only images or videos.
     *
     * @param showSingleMediaType whether to show only one media type, either images or videos.
     * @return {@link SelectionCreator} for fluent API.
     * @see SelectionSpec#onlyShowImages()
     * @see SelectionSpec#onlyShowVideos()
     */
    public SelectionCreator showSingleMediaType(boolean showSingleMediaType) {
        mSelectionSpec.showSingleMediaType = showSingleMediaType;
        return this;
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
        if (filter == null) throw new IllegalArgumentException("filter cannot be null");
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

    public SelectionCreator captureType(CaptureType captureType) {
        mSelectionSpec.captureType = captureType;
        return this;
    }

    /**
     * 拍摄后是否直接结束返回
     * @param finishBack
     * @return
     */
    public SelectionCreator finishBack(boolean finishBack) {
        mSelectionSpec.finishBack = finishBack;
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
     * This will be ignored when {@link #gridExpectedSize(int)} is set.
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
     * Set expected length for media grid to adapt to different screen sizes. This won't necessarily
     * be applied cause the media grid should fill the view container. The measured media grid's
     * length will be as close to this value as possible.
     *
     * @param size Expected media grid length in pixel.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator gridExpectedSize(int size) {
        mSelectionSpec.gridExpectedSize = size;
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

        Intent intent = new Intent(activity, MatisseActivity.class);

        Fragment fragment = mMatisse.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
        activity.overridePendingTransition(R.anim.slide_in_bottom, 0);
    }

    /**
     * Determines whether the photo list is group by date.
     *
     * @param groupByDate true for group by date list and false for nothing
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator groupByDate(boolean groupByDate) {
        mSelectionSpec.groupByDate = groupByDate;
        //it should not enable groupBy and capture as same time
//        mSelectionSpec.capture = false;
//        if (groupByDate) {
//            mSelectionSpec.capture = false;
//        }
        return this;
    }

    public SelectionCreator selectedMediaItem(List<MediaItem> list) {
        if (list == null) {
            return this;
        }
        mSelectionSpec.selectedDataList.addAll(list);
        return this;
    }

    public SelectionCreator checkListener(OnExtraFileCheckListener listener) {
        mSelectionSpec.checkListener = listener;
        return this;
    }

    /**
     * 是否启动单一结果模式。
     * 如果为true，那么当选择其中一个item适合，直接结束选择。
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
     * @param layout
     * @return
     */
    public SelectionCreator toolbarLayoutId(@LayoutRes int layout, int backViewId, int anchorViewId) {
        mSelectionSpec.toolbarLayoutId = layout;
        mSelectionSpec.backViewId = backViewId;
        mSelectionSpec.anchorViewId = anchorViewId;
        return this;
    }

    /**
     * 外部传入点击出现Toast的Uri
     *
     * @return
     */
    public SelectionCreator extraIdMap(final Map<Long, Long> map) {
        if (map == null) {
            return this;
        }
        mSelectionSpec.extraIdMap.putAll(map);
        return this;
    }

    /**
     * 获取指定的位置MediaItem数据
     * 数据为倒序。为0时候为最新一条数据。
     *
     * @param position
     * @param callback
     * @return
     */
    public SelectionCreator catchSpecPosition(int position, CatchSpePositionCallback callback) {
        if (position < 0 || callback == null) {
            return this;
        }
        mSelectionSpec.catchSpecPosition = position;
        mSelectionSpec.catchSpecPositionCallback = callback;
        return this;
    }


}
