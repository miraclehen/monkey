package com.miraclehen.monkey.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

/**
 * author: miraclehen
 * since: 2018/3/13
 */
public class VideoMedataUtil {


    public static long[] getMedataInfo(String abPath) {
        final long[] result = new long[]{0L, 0L, 0L};
        if (abPath == null) {
            return result;
        }
        try {
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(abPath);
            mp.prepare();
            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    result[0] = width;
                    result[1] = height;
                    if (width < height) {
                        result[0] = 0L;
                    } else {
                        result[0] = 90L;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    public static long[] getMedataInfo2(String abPath) {
        final long[] result = new long[]{0L, 0L, 0L};
        if (abPath == null) {
            return result;
        }
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bmp = null;
            retriever.setDataSource(abPath);
            bmp = retriever.getFrameAtTime();
            result[0] = bmp.getWidth();
            result[1] = bmp.getHeight();
            if (result[0] > result[1]) {
                result[2] = 90;
            } else {
                result[2] = 0;
            }
            bmp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
