package com.miraclehen.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miraclehen.monkey.CaptureType;
import com.miraclehen.monkey.Matisse;
import com.miraclehen.monkey.MatisseActivity;
import com.miraclehen.monkey.MimeType;
import com.miraclehen.monkey.engine.impl.GlideEngine;
import com.miraclehen.monkey.entity.CaptureStrategy;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.listener.OnItemCheckChangeListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final String BUNDLE_KEY_DATA_LIST = "bundle_key_data_list";

    private UriAdapter mAdapter;
    private List<Uri> uris = new ArrayList<>();
    private ArrayList<MediaItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.zhihu).setOnClickListener(this);
        findViewById(R.id.dracula).setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(BUNDLE_KEY_DATA_LIST) != null) {
            dataList = savedInstanceState.getParcelableArrayList(BUNDLE_KEY_DATA_LIST);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new UriAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setData(dataList);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.zhihu:
                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofImageExcludeGif(), false)
                        .countable(false)
                        .spanCount(4)
                        .captureFinishBack(false)
                        .captureType(CaptureType.Image)
                        .captureStrategy(new CaptureStrategy(true, "com.miraclehen.sample.fileprovider"))
                        .maxSelectable(20)
                        .groupByDate(true)
                        .checkListener(new OnItemCheckChangeListener() {
                            @Override
                            public void onCheck(MediaItem mediaItem, boolean check) {
                                Log.i(TAG, mediaItem.toString() + "  \n" + check);
                            }
                        })
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.dracula:
                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofAll())
                        .theme(R.style.Matisse_Zhihu)
                        .captureStrategy(new CaptureStrategy(true, "com.miraclehen.sample.fileprovider"))
                        .countable(false)
                        .captureType(CaptureType.Video)
                        .spanCount(4)
                        .groupByDate(true)
                        .maxSelectable(9)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
        }
        mAdapter.setData(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            dataList = data.getParcelableArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION_ITEM);
            mAdapter.setData(dataList);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_KEY_DATA_LIST, dataList);
    }

    private static class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        ArrayList<MediaItem> mDataList = new ArrayList<>();

        void setData(ArrayList<MediaItem> list) {
            if (list == null) {
                return;
            }
            mDataList.clear();
            mDataList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UriViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item, parent, false));
        }

        @Override
        public void onBindViewHolder(UriViewHolder holder, int position) {
            MediaItem item = mDataList.get(position);
            holder.mUri.setText("uri: " + item.getContentUri().toString());
            holder.mPath.setText("path: " + item.getOriginalPath().toString());
            holder.mLat.setText("纬度: " + String.valueOf(item.getLatitude()));
            holder.mLong.setText("经度:" + String.valueOf(item.getLongitude()));
            holder.mSize.setText("大小:" + String.valueOf(item.getLength()));
            holder.mDuration.setText("时长:" + String.valueOf(item.getDuration()));
            holder.mWidth.setText("宽度:" + String.valueOf(item.getWidth()));
            holder.mHeight.setText("高度:" + String.valueOf(item.getHeight()));
//            holder.mDate.setText("日期:" + String.valueOf(item.getDate()));

            holder.mUri.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        static class UriViewHolder extends RecyclerView.ViewHolder {

            private TextView mUri;
            private TextView mLat;
            private TextView mLong;
            private TextView mSize;
            private TextView mDuration;
            private TextView mWidth;
            private TextView mHeight;
            private TextView mPath;
            private TextView mDate;

            UriViewHolder(View contentView) {
                super(contentView);
                mUri = (TextView) contentView.findViewById(R.id.uri);
                mLat = (TextView) contentView.findViewById(R.id.lat);
                mLong = (TextView) contentView.findViewById(R.id.long_1);
                mSize = (TextView) contentView.findViewById(R.id.size);
                mDuration = (TextView) contentView.findViewById(R.id.duration);
                mWidth = (TextView) contentView.findViewById(R.id.width);
                mHeight = (TextView) contentView.findViewById(R.id.height);
                mPath = (TextView) contentView.findViewById(R.id.path);
                mDate = (TextView) contentView.findViewById(R.id.mDate);
            }
        }
    }

    private List<Uri> getUri() {
        List<Uri> result = new ArrayList<>();
        if (dataList == null) {
            return result;
        }
        for (MediaItem item : dataList) {
            if (item == null) {
                continue;
            }
            result.add(item.getContentUri());
        }
        return result;
    }
}
