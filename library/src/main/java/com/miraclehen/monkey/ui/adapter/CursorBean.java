package com.miraclehen.monkey.ui.adapter;

/**
 * author: hd
 * since: 2018/1/10
 */
public class CursorBean {

    //是否是日期文本
    private boolean isDateView = false;
    //日期文本
    private String dateText = "";

    //适配器位置
    private int adapterPosition = -1;
    //在cursor中的位置
    private int cursorPosition = -1;

    public boolean isDateView() {
        return isDateView;
    }

    public void setDateView(boolean dateView) {
        isDateView = dateView;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }
}
