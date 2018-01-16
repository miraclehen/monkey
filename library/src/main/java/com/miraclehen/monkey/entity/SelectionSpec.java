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

import com.miraclehen.monkey.MimeType;
import com.miraclehen.monkey.R;
import com.miraclehen.monkey.engine.ImageEngine;
import com.miraclehen.monkey.engine.impl.GlideEngine;
import com.miraclehen.monkey.filter.Filter;
import com.miraclehen.monkey.listener.CatchSpePositionCallback;
import com.miraclehen.monkey.listener.OnExtraFileCheckListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SelectionSpec {

    public Set<MimeType> mimeTypeSet;
    public boolean mediaTypeExclusive;
    public boolean showSingleMediaType;
    @StyleRes
    public int themeId;
    public int orientation;
    public boolean countable;
    public int maxSelectable;
    public List<Filter> filters;
    public boolean capture;
    //是否可以录制
    public boolean record;
    public CaptureStrategy captureStrategy;
    public int spanCount;
    public int gridExpectedSize;
    public float thumbnailScale;
    public ImageEngine imageEngine;
    public boolean groupByDate;

    public boolean singleResultModel;
    public int toolbarLayoutId;
    public int backViewId;
    public int anchorViewId;

    //获取指定位置的MediaItem数据
    public int catchSpecPosition = -1;
    public CatchSpePositionCallback catchSpecPositionCallback;
    //已选中的MediaItem
//    public final List<String> selectedPathList = new ArrayList<>();
//    public final Map<Integer, Boolean> selectedPathMap = new HashMap<>();
    public final List<MediaItem> selectedDataList = new ArrayList<>();
    //额额外的MediaItem数据。当点击这些Item时候。会触发checkListener
    public OnExtraFileCheckListener checkListener;
    public final Map<Long, Long> extraIdMap = new HashMap<>();

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
        showSingleMediaType = false;
        themeId = R.style.Matisse_Zhihu;
        orientation = 0;
        countable = false;
        maxSelectable = 1;
        filters = null;
        capture = false;
        record = false;
        captureStrategy = null;
        spanCount = 3;
        gridExpectedSize = 0;
        thumbnailScale = 0.5f;
        imageEngine = new GlideEngine();
        groupByDate = false;
//        selectedPathList.clear();
        checkListener = null;
        singleResultModel = false;
        toolbarLayoutId = -1;
//        selectedPathMap.clear();
        selectedDataList.clear();
        extraIdMap.clear();
        catchSpecPosition = -1;
        catchSpecPositionCallback = null;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && maxSelectable == 1;
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && MimeType.ofImage().containsAll(mimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && MimeType.ofVideo().containsAll(mimeTypeSet);
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
        return captureStrategy != null;
    }
}
