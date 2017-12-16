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
package com.jackson.monkey.entity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.StyleRes;


import com.jackson.monkey.CaptureType;
import com.jackson.monkey.MimeType;
import com.jackson.monkey.R;
import com.jackson.monkey.engine.ImageEngine;
import com.jackson.monkey.engine.impl.GlideEngine;
import com.jackson.monkey.filter.Filter;
import com.jackson.monkey.utils.MediaStoreCompat;

import java.util.ArrayList;
import java.util.List;
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

    public CaptureStrategy captureStrategy;
    public CaptureType captureType;
    public int spanCount;
    public int gridExpectedSize;
    public float thumbnailScale;
    public ImageEngine imageEngine;
    public boolean groupByDate;
    public final List<Uri> selectedUris = new ArrayList<>();

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

    public List<Uri> getSelectedUris() {
        return selectedUris;
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
        captureType = null;
        captureStrategy = null;
        spanCount = 3;
        gridExpectedSize = 0;
        thumbnailScale = 0.5f;
        imageEngine = new GlideEngine();
        groupByDate = false;
        selectedUris.clear();
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && maxSelectable == 1;
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public boolean onlyShowImages() {
        return MimeType.ofImage().containsAll(mimeTypeSet);
    }

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
        return captureStrategy != null;
    }
}
