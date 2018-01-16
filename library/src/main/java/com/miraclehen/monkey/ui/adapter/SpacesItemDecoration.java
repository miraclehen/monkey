package com.miraclehen.monkey.ui.adapter;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * author: hd
 * since: 2018/1/4
 * <p>
 * 空白分割线
 * <p>
 * 当一行为3个的时候
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int lot = -1;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int span = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        if (lot == -1) {
            //每个人分到的lot
            lot = space * (span - 1) / span;
        }

        if (span == 3) {
            doItemOffsetsOnSpan3(outRect, view, parent, state);
        } else if (span == 4) {
            doItemOffsetsOnSpan4(outRect, view, parent, state);
        }


    }

    /**
     * 当span数量为3时候的布局
     * 间距为1.5lot
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    private void doItemOffsetsOnSpan3(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if ((parent.getChildLayoutPosition(view) + 1) % 3 == 1) {
            //此位置是左边
            outRect.left = 0;
            outRect.right = lot;
        } else if ((parent.getChildLayoutPosition(view) + 1) % 3 == 0) {
            //此位置是右边一个
            outRect.left = lot;
            outRect.right = 0;
        } else {
            //此位置是中间
            outRect.left = lot / 2;
            outRect.right = lot / 2;
        }

        outRect.top = 0;

        if (parent.getAdapter().getItemCount() - parent.getAdapter().getItemCount() % 3 < (parent.getChildLayoutPosition(view) + 1)) {
            outRect.bottom = 0;
        } else {
            outRect.bottom = lot + lot / 2;
        }
    }

    /**
     * 当span数量为4时候的布局
     * 间距为lot + lot / 3
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    private void doItemOffsetsOnSpan4(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if ((parent.getChildLayoutPosition(view) + 1) % 4 == 1) {
            //此位置是左边
            outRect.left = 0;
            outRect.right = lot;
        } else if ((parent.getChildLayoutPosition(view) + 1) % 4 == 0) {
            //此位置是右边一个
            outRect.left = lot;
            outRect.right = 0;
        } else if ((parent.getChildLayoutPosition(view) + 1) % 4 == 2) {
            //此位置是左边第二个
            outRect.left = lot / 3;
            outRect.right = (lot / 3) * 2;
        } else {
            //此位置是左边第三个
            outRect.left = (lot / 3) * 2;
            outRect.right = lot / 3;
        }

        if (parent.getAdapter().getItemCount() - parent.getAdapter().getItemCount() % 4 < (parent.getChildLayoutPosition(view) + 1)) {
            outRect.bottom = 0;
        } else {
            outRect.bottom = lot + lot / 3;
        }

    }
}
