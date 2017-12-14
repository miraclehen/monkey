package com.jackson.monkey;

import android.net.Uri;

import com.jackson.monkey.engine.ImageLoadEngine;
import com.jackson.monkey.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * author: hd
 * since: 2017/12/15
 *
 * 偏好。外部传递进来
 */
public final class Preference {

    public Set<MimeType> mimeTypeSet;
    public int orientation;
    public boolean countable;
    public int maxSelectable;
    public boolean photograph;
    public boolean video;
    public CaptureStrategy captureStrategy;
    public int spanCount;
    public ImageLoadEngine imageLoadEngine;
    public boolean groudByDate;
    public final List<Uri> selectedUriList = new ArrayList<>();


}
