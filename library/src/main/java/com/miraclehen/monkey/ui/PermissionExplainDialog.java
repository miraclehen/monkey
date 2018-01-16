package com.miraclehen.monkey.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * author: hd
 * since: 2017/12/16
 * <p>
 * 请求权限解释Dialog
 */
public class PermissionExplainDialog extends DialogFragment {
    private static String BUNDLE_KEY_MESSAGE = "BUNDLE_KEY_MESSAGE";

    private OnDialogPositiveButtonClickListener mClickListener;
    private String mMessage = "";

    public interface OnDialogPositiveButtonClickListener {
        void onClick();
    }

    public void setClickListener(OnDialogPositiveButtonClickListener clickListener) {
        mClickListener = clickListener;
    }

    public static PermissionExplainDialog newInstance(String message, OnDialogPositiveButtonClickListener listener) {
        Bundle args = new Bundle();
        PermissionExplainDialog fragment = new PermissionExplainDialog();
        fragment.setClickListener(listener);
        fragment.setMessage(message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(mMessage)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mClickListener != null) {
                            mClickListener.onClick();
                        }
                    }
                });
        return dialog.create();
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
