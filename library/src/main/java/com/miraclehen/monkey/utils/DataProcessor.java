package com.miraclehen.monkey.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.miraclehen.monkey.entity.MediaItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: hd
 * since: 2017/12/9
 * <p>
 * 数据处理
 */
public class DataProcessor {

    public static List<Uri> convertToUrlList(List<MediaItem> list) {
        List<Uri> result = new ArrayList<>();
        for (MediaItem item : list) {
            if (item.getUri() == null && TextUtils.isEmpty(item.getOriginalPath())) {
                continue;
            }
            if (item.getUri() != null) {
                result.add(item.getUri());
            } else {
                result.add(Uri.fromFile(new File(item.getOriginalPath())));
            }
        }
        return result;
    }

    public static List<String> convertToPathList(List<MediaItem> list) {
        List<String> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (MediaItem item : list) {
            if (TextUtils.isEmpty(item.getOriginalPath())) {
                continue;
            }
            result.add(item.getOriginalPath());
        }
        return result;
    }

    public static List<MediaItem> convertToMediaItemList(Map<String, Boolean> pathList) {
        List<MediaItem> result = new ArrayList<>();
        if (pathList == null) {
            return result;
        }
        for (Map.Entry<String, Boolean> entry : pathList.entrySet()) {
            if (TextUtils.isEmpty(entry.getKey()) || !entry.getValue()) {
                continue;
            }
            MediaItem item = new MediaItem();
            item.setOriginalPath(entry.getKey());
            result.add(item);
        }
        return result;
    }

    /**
     * 将selected列表分发
     *
     * @param mediaItemList
     * @return
     */
    public static Object[] dispatchPathOrUri(List<MediaItem> mediaItemList) {
        Object[] objects = new Object[2];
        List<String> pathList = new ArrayList<>();
        List<Uri> uriList = new ArrayList<>();
        for (MediaItem item : mediaItemList) {
            if (item == null) {
                continue;
            }
            if (!TextUtils.isEmpty(item.getOriginalPath())) {
                //优先路径
                pathList.add(item.getOriginalPath());
            } else if (item.getUri() != null) {
                //其次uri
                uriList.add(item.getUri());
            }
        }
        objects[0] = pathList;
        objects[1] = uriList;
        return objects;
    }

    public static Map<Long, Long> convertMapForId(List<Long> idList) {
        Map<Long, Long> result = new HashMap<>();
        for (Long fileId : idList) {
            result.put(fileId, null);
        }
        return result;
    }

}
