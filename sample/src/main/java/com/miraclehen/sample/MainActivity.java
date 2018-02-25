package com.miraclehen.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.miraclehen.monkey.CaptureType;
import com.miraclehen.monkey.MimeType;
import com.miraclehen.monkey.Monkey;
import com.miraclehen.monkey.MonkeyActivity;
import com.miraclehen.monkey.engine.impl.GlideEngine;
import com.miraclehen.monkey.entity.CaptureStrategy;
import com.miraclehen.monkey.entity.MediaItem;
import com.miraclehen.monkey.listener.InflateItemViewCallback;
import com.miraclehen.monkey.listener.OnItemCheckChangeListener;
import com.miraclehen.monkey.ui.widget.MediaGrid;
import com.miraclehen.monkey.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final String BUNDLE_KEY_DATA_LIST = "bundle_key_data_list";

    private List<Uri> uris = new ArrayList<>();
    private ArrayList<MediaItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.simple_multi_image).setOnClickListener(this);
        findViewById(R.id.simple_single_image).setOnClickListener(this);

        findViewById(R.id.simple_multi_video).setOnClickListener(this);
        findViewById(R.id.simple_single_video).setOnClickListener(this);

        findViewById(R.id.zhihu).setOnClickListener(this);
        findViewById(R.id.dracula).setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(BUNDLE_KEY_DATA_LIST) != null) {
            dataList = savedInstanceState.getParcelableArrayList(BUNDLE_KEY_DATA_LIST);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.simple_multi_image:
                Monkey.from(MainActivity.this)
                        .choose(MimeType.ofImageExcludeGif())
                        .countable(true)
                        .maxSelectable(20)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.simple_single_image:
                Monkey.from(MainActivity.this)
                        .choose(MimeType.ofImageExcludeGif())
                        .singleResultModel(true)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.simple_multi_video:
                Monkey.from(MainActivity.this)
                        .choose(MimeType.ofVideo())
                        .countable(true)
                        .maxSelectable(20)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.simple_single_video:
                Monkey.from(MainActivity.this)
                        .choose(MimeType.ofVideo())
                        .singleResultModel(true)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.zhihu:
                Monkey.from(MainActivity.this)
                        .choose(MimeType.ofImageExcludeGif())
                        .countable(false)
                        .spanCount(4)
                        .selectedMediaItem(dataList)
                        .captureFinishBack(false)
                        .captureType(CaptureType.Image)
                        .captureStrategy(new CaptureStrategy(true, "com.miraclehen.sample.fileprovider"))
                        .maxSelectable(20)
                        .toolbarLayoutId(R.layout.layout_custom_tool_bar)
                        .theme(R.style.Matisse_Zhihu)
                        .groupByDate(true)
                        .inflateItemViewCallback(new InflateItemViewCallback() {
                            @Override
                            public void callback(MediaItem mediaItem, MediaGrid mediaGrid) {
                                ImageView imageView = new ImageView(MainActivity.this);
                                imageView.setImageResource(R.drawable.img_cloud);
                                mediaGrid.addView(imageView, new FrameLayout.LayoutParams(UIUtils.convertDIPToPixels(MainActivity.this, 25),
                                        UIUtils.convertDIPToPixels(MainActivity.this, 25), Gravity.START | Gravity.TOP));
                            }
                        })
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
                Monkey.from(MainActivity.this)
                        .choose(MimeType.ofImageExcludeGif())
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            dataList = data.getParcelableArrayListExtra(MonkeyActivity.EXTRA_RESULT_SELECTION_ITEM);
            for (MediaItem item : dataList) {
                Log.i(TAG, item.toString());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_KEY_DATA_LIST, dataList);
    }

}
