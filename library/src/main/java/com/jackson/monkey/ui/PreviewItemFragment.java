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
package com.jackson.monkey.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.chrisbanes.photoview.PhotoView;
import com.jackson.monkey.R;
import com.jackson.monkey.entity.MediaItem;
import com.jackson.monkey.entity.SelectionSpec;
import com.jackson.monkey.ui.widget.PreviewViewPager;
import com.jackson.monkey.utils.PhotoMetadataUtils;

import java.io.File;


public class PreviewItemFragment extends Fragment {
    private static final String BUNDLE_KEY_VIDEO_VIEW_POSITION = "BUNDLE_KEY_VIDEO_VIEW_POSITION";

    private static final String ARGS_ITEM = "args_item";

    private MediaItem mMediaItem;

    private View videoPlayButton;
    private PhotoView imageView;
    private VideoView videoView;

    private int position;

    public static PreviewItemFragment newInstance(MediaItem item) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMediaItem = getArguments().getParcelable(ARGS_ITEM);
        if (mMediaItem == null) {
            return;
        }

        videoPlayButton = view.findViewById(R.id.video_play_button);
        imageView = view.findViewById(R.id.image_view);
        videoView = view.findViewById(R.id.video_view);

        if (mMediaItem.isVideo()) {
            doVideo(view);
        } else {
            doImage(view);
        }


    }

    /**
     * 当资源类型是图片
     */
    private void doImage(View view) {
        videoView.setVisibility(View.GONE);
        videoPlayButton.setVisibility(View.GONE);

        Point size = PhotoMetadataUtils.getBitmapSize(mMediaItem.getContentUri(), getActivity());
        if (mMediaItem.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), size.x, size.y, imageView,
                    mMediaItem.getContentUri());
        } else {
            SelectionSpec.getInstance().imageEngine.loadImage(getContext(), size.x, size.y, imageView,
                    mMediaItem.getContentUri());
        }
    }

    /**
     * 当资源类型是视频
     */
    private void doVideo(final View view) {
        imageView.setVisibility(View.GONE);

        videoView.setVideoURI(mMediaItem.uri);
        videoView.requestFocus();

        videoPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayButton.setVisibility(View.INVISIBLE);
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.seekTo(0);
                videoPlayButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_VIDEO_VIEW_POSITION,videoView.getCurrentPosition());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(BUNDLE_KEY_VIDEO_VIEW_POSITION,0);
            videoView.seekTo(position);
        }

    }

    public void resetView() {
        if (getView() != null) {
//            ((PhotoView) getView().findViewById(R.id.image_view)).setmas();
        }
    }
}
