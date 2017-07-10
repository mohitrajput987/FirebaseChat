package com.otb.firebasechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.otb.firebasechat.R;
import com.otb.firebasechat.activity.LoginActivity;

/**
 * Created by Mohit Rajput on 6/7/17.
 * This utility class provides dialogs to show messages and alert windows
 */

public class DialogUtils {
    public static void openDialogToShowMessage(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void openDialogOnSessionExpired(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(context.getString(R.string.session_expired_title));
        builder.setMessage(context.getString(R.string.session_expired_message));
        builder.setPositiveButton(context.getString(R.string.login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void openDialogToLogout(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.logout));
        builder.setMessage(context.getString(R.string.logout_message));

        builder.setPositiveButton(context.getString(R.string.logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CommonUtils.isNetworkAvailable(context)) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                } else {
                    ToastUtils.showShortToast(context, R.string.network_not_available);
                }
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
