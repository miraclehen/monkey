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
package com.miraclehen.monkey.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miraclehen.monkey.CaptureType;
import com.miraclehen.monkey.R;
import com.miraclehen.monkey.UICallback;
import com.miraclehen.monkey.entity.Album;
import com.miraclehen.monkey.entity.IncapableCause;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.entity.SelectionSpec;
import com.miraclehen.monkey.model.SelectedItemCollection;
import com.miraclehen.monkey.ui.widget.CheckView;
import com.miraclehen.monkey.ui.widget.MediaGrid;
import com.miraclehen.monkey.utils.DateTimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AlbumMediaAdapter extends
        RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements
        MediaGrid.OnMediaGridClickListener {

    private static final String TAG = "AlbumMediaAdapter";
    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_MEDIA = 0x02;
    private static final int VIEW_TYPE_DATE = 0x03;
    //拍摄视频Item
    private static final int VIEW_TYPE_RECORD = 0x04;
    //已选中的集合
    private final SelectedItemCollection mSelectedCollection;
    private final Album mAlbum;
    private final Drawable mPlaceholder;
    //外部可选配置
    private SelectionSpec mSelectionSpec;


    /**
     * 点击监听器回调
     */
    private UICallback mUICallback;

    private RecyclerView mRecyclerView;
    private int mImageResize;
    private int mDateCount = 0;
    private Context mContext;

    private HashMap<Long, Integer> mDateWithPosMap = new HashMap<>();
    private ArrayList<Long> mDateList = new ArrayList<>();
    private List<CursorBean> mCursorBeanList = new ArrayList<>();

    private OnDataChangeListener mOnDataChangeListener;

    private boolean isProcessData = false;




    /**
     * 适配器数据监听
     */
    public interface OnDataChangeListener{
        /**
         * 当处理数据完毕回调
         */
        void processFinished(Album album, List<CursorBean> dataList, Cursor cursor);
    }

    public AlbumMediaAdapter(Context context, Album album, SelectedItemCollection selectedCollection,
                             RecyclerView recyclerView, List<MediaItem> selectedData) {
        super(null);
        mContext = context;
        mAlbum = album;
        mSelectionSpec = SelectionSpec.getInstance();

        mSelectedCollection = selectedCollection;

        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.item_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();

        mRecyclerView = recyclerView;
    }

    @Override
    public void swapCursor(Cursor newCursor) {
        isProcessData = false;
        super.swapCursor(newCursor);
    }

    private void processData(Cursor newCursor) {
        //日期与cursor的位置
        mDateWithPosMap.clear();
        mDateList.clear();
        mCursorBeanList.clear();

        if (newCursor == null || newCursor.getCount() == 0) {
            return;
        }
        newCursor.moveToPosition(-1);
        while (newCursor.moveToNext()) {
            long tmpTime = newCursor.getLong(newCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
            long msTime = DateTimeUtil.timeToMs(tmpTime);
            if (msTime != 0) {
                while (mDateWithPosMap.containsKey(msTime)) {
                    msTime++;
                }
                mDateWithPosMap.put(msTime, newCursor.getPosition());
            } else {
                long tmpAddTime = newCursor.getLong(newCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
                long msAddTime = DateTimeUtil.timeToMs(tmpAddTime);
                while (mDateWithPosMap.containsKey(msAddTime)) {
                    msAddTime++;
                }
                mDateWithPosMap.put(msAddTime, newCursor.getPosition());
            }
        }

        //排序
        Long[] dateArray = mDateWithPosMap.keySet().toArray(new Long[0]);

        mDateList.addAll(Arrays.asList(dateArray));
        Collections.sort(mDateList, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (o2 - o1 == 0) {
                    return 0;
                } else if (o2 - o1 > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        //第一个日期视图
        addDateView(0);
        //第一个cursor视图
        addCursorView(0);

        for (int i = 1; i < mDateList.size(); i++) {
            if (!DateTimeUtil.isSameDayOfMillis(mDateList.get(i), mDateList.get(i - 1))) {
                //两个cursor，如果不是同一天
                //添加日期视图
                addDateView(i);
            }
            //添加cursor视图
            addCursorView(i);
        }

        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.processFinished(mAlbum, mCursorBeanList, newCursor);
        }


    }



    /**
     * 添加日期视图
     *
     * @param position mDateList 的索引值
     */
    private void addDateView(int position) {
        CursorBean cursorBean = new CursorBean();
        cursorBean.setDateView(true);
        cursorBean.setDateValue(mDateList.get(position));
        cursorBean.setDateText(getFormatDate(new Date(mDateList.get(position))));
        cursorBean.setAdapterPosition(mCursorBeanList.size());

        mCursorBeanList.add(cursorBean);
    }

    /**
     * 添加cursor视图
     *
     * @param position mDateList 的索引值
     */
    private void addCursorView(int position) {
        CursorBean cursorBean = new CursorBean();
        cursorBean.setAdapterPosition(mCursorBeanList.size());
        cursorBean.setCursorPosition(mDateWithPosMap.get(mDateList.get(position)));

        mCursorBeanList.add(cursorBean);
    }

    @Override
    public int getItemCount() {
        if (isDataValid(mCursor)) {
            if (mSelectionSpec.groupByDate) {
                if (!isProcessData) {
                    isProcessData = true;
                    processData(mCursor);
                }
                return mCursorBeanList.size();
            }
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAPTURE) {
            //打开照相视图
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_capture_item, parent, false);
            CaptureViewHolder holder = new CaptureViewHolder(v);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUICallback != null) {
                        mUICallback.startCapture(CaptureType.Image);
                    }
                }
            });
            return holder;
        } else if (viewType == VIEW_TYPE_RECORD) {
            //打开视频视图
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_record_item, parent, false);
            RecordViewHolder holder = new RecordViewHolder(v);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUICallback != null) {
                        mUICallback.startCapture(CaptureType.Video);
                    }
                }
            });
            return holder;
        } else {
            if (viewType == VIEW_TYPE_MEDIA) {
                //正常的Media视图
                MediaGrid v = (MediaGrid) LayoutInflater.from(parent.getContext()).inflate(R.layout.media_grid_item,
                        parent, false);
                return new MediaViewHolder(v);
            } else if (viewType == VIEW_TYPE_DATE) {
                //日期视图
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_date_item, parent, false);
                return new MediaDateViewHolder(v);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!mSelectionSpec.groupByDate) {
            super.onBindViewHolder(holder, position);
        } else {
            if (!isDataValid(mCursor)) {
                throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
            }

            if (getItemViewType(position) == VIEW_TYPE_DATE) {
                //日期视图
                onBindViewHolder(holder, null);
            } else {
                //正常的视图
                moveToPosition(position);
                onBindViewHolder(holder, mCursor);
            }
        }
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof CaptureViewHolder) {
            //拍照Holder
            CaptureViewHolder captureViewHolder = (CaptureViewHolder) holder;
            Drawable[] drawables = captureViewHolder.mHint.getCompoundDrawables();
            TypedArray ta = holder.itemView.getContext().getTheme().obtainStyledAttributes(
                    new int[]{R.attr.capture_textColor});
            int color = ta.getColor(0, 0);
            ta.recycle();
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        } else if (holder instanceof MediaViewHolder) {
            //MediaView Holder ,也就是Cursor视图
            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;

            final MediaItem item = MediaItem.valueOf(cursor);

            if (mSelectionSpec.inflateItemViewCallback != null) {
                mSelectionSpec.inflateItemViewCallback.callback(item, (MediaGrid) holder.itemView);
            }

            mediaViewHolder.mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                    getImageResize(mediaViewHolder.mMediaGrid.getContext()),
                    mPlaceholder,
                    mSelectionSpec.countable,
                    holder));
            mediaViewHolder.mMediaGrid.bindMedia(item);
            mediaViewHolder.mMediaGrid.setOnMediaGridClickListener(this);
            setCheckStatus(item, mediaViewHolder.mMediaGrid);
        } else if (holder instanceof MediaDateViewHolder) {
            //日期视图
            MediaDateViewHolder mediaDateViewHolder = (MediaDateViewHolder) holder;
            mediaDateViewHolder.mDate.setText("");

            mediaDateViewHolder.mDate.setText(getDateStringByAdapterPosition(holder.getAdapterPosition()));
        }
    }

    private void setCheckStatus(MediaItem item, MediaGrid mediaGrid) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setCheckedNum(checkedNum);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setCheckedNum(checkedNum);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setChecked(true);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setChecked(false);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setChecked(false);
                }
            }
        }
    }

    /**
     * 当item小图被点击
     *
     * @param thumbnail
     * @param item
     * @param holder
     */
    @Override
    public void onThumbnailClicked(ImageView thumbnail, MediaItem item, RecyclerView.ViewHolder holder) {
        if (mUICallback != null) {
            mUICallback.onMediaClick(mAlbum, item, holder.getAdapterPosition());
        }
    }

    /**
     * 当勾选checkView被点击
     *
     * @param checkView
     * @param item
     * @param holder
     */
    @Override
    public void onCheckViewClicked(CheckView checkView, MediaItem item, RecyclerView.ViewHolder holder) {
        if (mSelectionSpec.countable) {
            //数字可选模式
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyListeners(item, true);
                }
            } else {
                mSelectedCollection.remove(item);
                notifyListeners(item, false);
            }
        } else {
            //勾选模式
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                notifyListeners(item, false);
            } else {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyListeners(item, true);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 通知各种监听器
     *
     * @param mediaItem
     * @param check
     */
    private void notifyListeners(MediaItem mediaItem, boolean check) {
        //更新底部工具条数字监听器
        if (mUICallback != null) {
            mUICallback.updateBottomBarCount();
        }

        //当某个Item被勾选或者取消勾选监听器
        if (mSelectionSpec.checkListener != null) {
            mSelectionSpec.checkListener.onCheck(mediaItem, check);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!mSelectionSpec.groupByDate) {
            return super.getItemViewType(position);
        }

        if (isDatePosition(position)) {
            //日期视图
            return VIEW_TYPE_DATE;
        } else {
            if (mCursor != null && !mCursor.isClosed()) {
                //移动到对应位置
                moveToPosition(position);
                return getItemViewType(position, mCursor);
            }
        }

        return super.getItemViewType(position);

    }

    /**
     * 移动到对应的cursor位置
     *
     * @param position
     */
    private void moveToPosition(int position) {
        mCursor.moveToPosition(mCursorBeanList.get(position).getCursorPosition());
    }

    /**
     * 此位置是否是日期位置
     *
     * @param position
     * @return
     */
    private boolean isDatePosition(int position) {
        return mCursorBeanList.get(position).isDateView();
    }

    /**
     * 通过日期的视图位置。获取对应的日期文本
     *
     * @return
     */
    private String getDateStringByAdapterPosition(int position) {
        return mCursorBeanList.get(position).getDateText();
    }

    @Override
    public int getItemViewType(int position, Cursor cursor) {
        MediaItem item = MediaItem.valueOfId(cursor);
        boolean capture = item.isCapture();
        boolean record = item.isRecord();
        return capture ? VIEW_TYPE_CAPTURE : record ? VIEW_TYPE_RECORD : VIEW_TYPE_MEDIA;
    }

    @Override
    public long getItemId(int position) {
        if (!mSelectionSpec.groupByDate) {
            return super.getItemId(position);
        }

        //enable groupByDate
        if (!isDataValid(mCursor)) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }

        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getItemViewType(position) == VIEW_TYPE_DATE) {
                    return mSelectionSpec.spanCount;
                } else {
                    return 1;
                }
            }
        });
    }

    /**
     * 是否可以选中
     *
     * @param context
     * @param item
     * @return
     */
    private boolean assertAddSelection(Context context, MediaItem item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    /**
     * 设置监听器回调
     *
     * @param callback
     */
    public void setUICallback(UICallback callback) {
        this.mUICallback = callback;
    }

    /**
     * 设置数据变化监听器
     * @param onDataChangeListener
     */
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.media_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * mSelectionSpec.thumbnailScale);
        }
        return mImageResize;
    }

    private String getFormatDate(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.date_format), Locale.CHINA);

        if (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
            return mContext.getString(R.string.today);
        } else if (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            return format.format(date).substring(5);
        } else {
            return format.format(date);
        }
    }

    private static class MediaViewHolder extends RecyclerView.ViewHolder {

        private MediaGrid mMediaGrid;

        MediaViewHolder(View itemView) {
            super(itemView);
            mMediaGrid = (MediaGrid) itemView;
        }
    }

    private static class CaptureViewHolder extends RecyclerView.ViewHolder {

        private TextView mHint;

        CaptureViewHolder(View itemView) {
            super(itemView);
            mHint = itemView.findViewById(R.id.hint);
        }
    }

    private static class RecordViewHolder extends RecyclerView.ViewHolder {
        private TextView mHint;

        RecordViewHolder(View itemView) {
            super(itemView);
            mHint = itemView.findViewById(R.id.hint);
        }
    }

    private static class MediaDateViewHolder extends RecyclerView.ViewHolder {
        private TextView mDate;

        public MediaDateViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView;
        }
    }

}
