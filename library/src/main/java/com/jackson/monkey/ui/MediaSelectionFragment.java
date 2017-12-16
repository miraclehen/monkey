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

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackson.monkey.CaptureType;
import com.jackson.monkey.R;
import com.jackson.monkey.entity.Album;
import com.jackson.monkey.entity.MediaItem;
import com.jackson.monkey.entity.SelectionSpec;
import com.jackson.monkey.model.AlbumMediaCollection;
import com.jackson.monkey.model.SelectedItemCollection;
import com.jackson.monkey.ui.adapter.AlbumMediaAdapter;
import com.jackson.monkey.utils.UIUtils;


/**
 * 一本相册的内容
 */
public class MediaSelectionFragment extends Fragment implements
        AlbumMediaCollection.AlbumMediaCallbacks, AlbumMediaAdapter.CheckStateListener,
        AlbumMediaAdapter.OnMediaClickListener {

    public static final String EXTRA_ALBUM = "extra_album";

    private final AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    private RecyclerView mRecyclerView;
    private AlbumMediaAdapter mAdapter;
    private SelectionProvider mSelectionProvider;
    private AlbumMediaAdapter.CheckStateListener mCheckStateListener;
    private AlbumMediaAdapter.OnMediaClickListener mOnMediaClickListener;

    private Album mAlbum;
    private SelectionSpec mSpec;

    private boolean mCaptureLater = false;

    public static MediaSelectionFragment newInstance(Album album) {
        MediaSelectionFragment fragment = new MediaSelectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectionProvider) {
            mSelectionProvider = (SelectionProvider) context;
        } else {
            throw new IllegalStateException("Context must implement SelectionProvider.");
        }
        if (context instanceof AlbumMediaAdapter.CheckStateListener) {
            mCheckStateListener = (AlbumMediaAdapter.CheckStateListener) context;
        }
        if (context instanceof AlbumMediaAdapter.OnMediaClickListener) {
            mOnMediaClickListener = (AlbumMediaAdapter.OnMediaClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAlbum = getArguments().getParcelable(EXTRA_ALBUM);
        mSpec = SelectionSpec.getInstance();

        mAdapter = new AlbumMediaAdapter(getContext(),
                mSelectionProvider.provideSelectedItemCollection(), mRecyclerView, mSpec.selectedUris);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        int spanCount;
        if (mSpec.gridExpectedSize > 0) {
            spanCount = UIUtils.spanCount(getContext(), mSpec.gridExpectedSize);
        } else {
            spanCount = mSpec.spanCount;
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

            int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
            //mRecyclerView.addItemDecoration(new MediaGridInset(spanCount, spacing, false));
            mRecyclerView.setAdapter(mAdapter);
            mAlbumMediaCollection.onCreate(getActivity(), this);
            mAlbumMediaCollection.load(mAlbum, mSpec.captureType == CaptureType.Image,
                    mSpec.captureType == CaptureType.Video);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumMediaCollection.onDestroy();
    }

    public void refreshMediaGrid() {
        mAdapter.notifyDataSetChanged();
    }

    public void refreshSelection() {
        mAdapter.refreshSelection();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        if (mCaptureLater) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                laterCallback.later(MediaItem.valueOf(cursor));
            }
        }else {
            mAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onAlbumMediaReset() {
        if (mCaptureLater) {
            mCaptureLater=false;
            return;
        }
        mAdapter.swapCursor(null);
    }

    @Override
    public void onUpdate() {
        // notify outer Activity that check state changed
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public void onMediaClick(Album album, MediaItem item, int adapterPosition) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick((Album) getArguments().getParcelable(EXTRA_ALBUM),
                    item, adapterPosition);
        }
    }

    public interface SelectionProvider {
        SelectedItemCollection provideSelectedItemCollection();
    }

    public void captureLater(OnGetTargetMediaItemLaterCallback callback){
        mCaptureLater= true;
        mAlbumMediaCollection.reloadForCapture(mAlbum);
        laterCallback = callback;

    }

    public interface OnGetTargetMediaItemLaterCallback{
        void later(MediaItem mediaItem);
    }

    private OnGetTargetMediaItemLaterCallback laterCallback;
}
