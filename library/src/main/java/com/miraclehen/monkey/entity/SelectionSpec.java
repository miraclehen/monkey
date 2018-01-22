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
package com.miraclehen.monkey.entity;

import android.content.pm.ActivityInfo;
import android.support.annotation.StyleRes;

import com.miraclehen.monkey.CaptureType;
import com.miraclehen.monkey.MimeType;
import com.miraclehen.monkey.R;
import com.miraclehen.monkey.engine.ImageEngine;
import com.miraclehen.monkey.engine.impl.GlideEngine;
import com.miraclehen.monkey.filter.Filter;
import com.miraclehen.monkey.listener.CatchSpecMediaItemCallback;
import com.miraclehen.monkey.listener.OnItemCheckChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 选择偏好
 */
public final class SelectionSpec {

    /**
     * 数据MimeType集合
     */
    public Set<MimeType> mimeTypeSet;

    /**
     * 当数据资源为视频和图片混合时候，是否能同时选中它们
     */
    public boolean mediaTypeExclusive;

    /**
     * 主题资源id
     */
    @StyleRes
    public int themeId;

    /**
     * 显示数据屏幕方向
     */
    public int orientation;

    /**
     * 是否可数
     */
    public boolean countable;

    /**
     * 最大可选中数量
     */
    public int maxSelectable;

    /**
     * 数据过滤器
     */
    public List<Filter> filters;

    /**
     * 拍摄类型，包括照片，视频，或者都不
     */
    public CaptureType captureType;

    /**
     * 拍摄策略
     */
    public CaptureStrategy captureStrategy;

    /**
     * 拍摄后是否直接退出Monkey
     */
    public boolean captureFinishBack;

    /**
     * 缩略图缩放比例
     */
    public float thumbnailScale;

    /**
     * 图片加载引擎
     */
    public ImageEngine imageEngine;

    /**
     * 是否支持日期分组
     */
    public boolean groupByDate;

    /**
     * 一行的数量
     */
    public int spanCount;

    /**
     * 是否启动单一结果模式。
     * 如果为true，那么当选择其中一个item适合，直接结束选择。
     * 当为true的时候，{@link #captureFinishBack}将为被设置为true，也就是拍照之后直接返回数据，不停留在MatisseActivity
     */
    public boolean singleResultModel;

    //======待修改
    public int toolbarLayoutId;
    public int backViewId;
    public int anchorViewId;

    /**
     * 已选中的MediaItem
     */
    public final List<MediaItem> selectedDataList = new ArrayList<>();

    /**
     * 当某个Item被勾选或者取消勾选
     */
    public OnItemCheckChangeListener checkListener;

    /**
     * 获取指定日期区间的数据MediaItem之后回调相应的方法
     */
    public CatchSpecMediaItemCallback.dateCallback catchDateSpecCallback;

    /**
     * 获取日期最新的一条数据MediaItem之后回调相应的方法
     */
    public CatchSpecMediaItemCallback.newestCallback catchNewestSpecCallback;


    private SelectionSpec() {
    }

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectionSpec getCleanInstance() {
        SelectionSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private void reset() {
        mimeTypeSet = null;
        mediaTypeExclusive = true;
        themeId = R.style.Matisse_Zhihu;
        orientation = 0;
        countable = false;
        maxSelectable = 1;
        filters = null;
        captureType = CaptureType.None;
        captureStrategy = null;
        captureFinishBack = false;
        spanCount = 3;
        thumbnailScale = 1f;
        imageEngine = new GlideEngine();
        groupByDate = false;
        singleResultModel = false;
        toolbarLayoutId = -1;
        selectedDataList.clear();

        checkListener = null;
        catchDateSpecCallback = null;
        catchNewestSpecCallback = null;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && maxSelectable == 1;
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * 仅仅显示图片
     *
     * @return
     */
    public boolean onlyShowImages() {
        return MimeType.ofImage().containsAll(mimeTypeSet);
    }

    /**
     * 仅仅显示视频
     *
     * @return
     */
    public boolean onlyShowVideos() {
        return MimeType.ofVideo().containsAll(mimeTypeSet);
    }

    private static final class InstanceHolder {
        private static final SelectionSpec INSTANCE = new SelectionSpec();
    }

    /**
     * 是否可以录制或者拍照
     *
     * @return
     */
    public boolean isCapture() {
        return captureType != CaptureType.None;
    }
}
