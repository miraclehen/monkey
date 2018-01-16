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
package com.miraclehen.monkey.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miraclehen.monkey.R;
import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.entity.SelectionSpec;
import com.miraclehen.monkey.model.AlbumMediaCollection;
import com.miraclehen.monkey.model.SelectedItemCollection;
import com.miraclehen.monkey.ui.adapter.AlbumMediaAdapter;


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
    private SelectionSpec mSelectionSpec;
    private SelectedItemCollection mSelectedItemCollection;

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
        mRecyclerView = view.findViewById(R.id.recyclerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAlbum = getArguments().getParcelable(EXTRA_ALBUM);
        mSelectionSpec = SelectionSpec.getInstance();

        mSelectedItemCollection = mSelectionProvider.provideSelectedItemCollection();
        mAdapter = new AlbumMediaAdapter(getContext(), mAlbum,
                mSelectedItemCollection, mRecyclerView, mSelectionSpec.selectedDataList);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(UIUtils.convertDIPToPixels(getContext(), 2)));

        mRecyclerView.setAdapter(mAdapter);
        mAlbumMediaCollection.onCreate(getActivity(), this);
        mAlbumMediaCollection.load(mAlbum, mSelectionSpec.capture, mSelectionSpec.record);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumMediaCollection.onDestroy();
    }

    public void refreshMediaGrid() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 数据加载结束
     *
     * @param cursor
     */
    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        if (mCaptureLater) {
            //如果可以拍照或者视频
            int existCount = 0;
            if (mSelectionSpec.capture || mSelectionSpec.record) {
                existCount = 1;
            }
            if (cursor != null && cursor.getCount() > existCount) {
                cursor.moveToPosition(existCount);
                do {
                    if (cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)) != 0) {
                        laterCallback.later(MediaItem.valueOf(cursor));
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } else {


            cursor.moveToPosition(-1);
            processData(cursor);
            cursor.moveToPosition(-1);
            mAdapter.swapCursor(cursor);
        }
    }


    /**
     * 处理数据。
     * 将外部传进来的selectedUris与本地的数据的uri对比。如果一致。将添加到mSelectedItemCollection中。
     */
    private void processData(Cursor cursor) {
        if (!mSelectedItemCollection.isEmpty()) {
            return;
        }
        while (cursor.moveToNext()) {
            final MediaItem item = MediaItem.valueOf(cursor);
            if (mSelectionSpec.selectedDataList.contains(item)) {
                mSelectedItemCollection.add(item);
                mSelectionSpec.selectedDataList.remove(item);
            }
            if (mSelectionSpec.selectedDataList.size() == 0) {
                break;
            }
        }
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public void onAlbumMediaReset() {
        if (mCaptureLater) {
            mCaptureLater = false;
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

    public void captureLater(OnGetTargetMediaItemLaterCallback callback) {
        mCaptureLater = true;
        mAlbumMediaCollection.reloadForCapture(mAlbum, mSelectionSpec.capture, mSelectionSpec.record);
        laterCallback = callback;

    }

    public interface OnGetTargetMediaItemLaterCallback {
        void later(MediaItem mediaItem);
    }

    private OnGetTargetMediaItemLaterCallback laterCallback;
}
