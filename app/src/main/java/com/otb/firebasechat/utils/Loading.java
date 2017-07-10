package com.otb.firebasechat.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.otb.firebasechat.R;

/**
 * Created by Mohit Rajput on 4/7/17.
 * Material loading bar
 */
public class Loading {

    private Context context;
    private Dialog dialog;

    public Loading(Context context) {
        this.context = context;
    }

    public void show(String message) {
        dialog = new Dialog(context);
        try {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_custom_loading, null);
            TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
            tvMessage.setText(message);
            dialog.setContentView(view);
            dialog.getWindow().setLayout(-1, -2);
            dialog.setCancelable(true);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showSolidProgress(String message) {
        dialog = new Dialog(context);
        try {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.color.white);
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_custom_loading, null);
            TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
            tvMessage.setText(message);
            dialog.setContentView(view);
            dialog.getWindow().setLayout(-1, -2);
            dialog.setCancelable(false);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


