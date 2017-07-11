package com.otb.firebasechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.otb.firebasechat.R;
import com.otb.firebasechat.activity.LoginActivity;

import butterknife.ButterKnife;

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

    public static void openDialogToDeleteAccount(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_password, null);
        builder.setView(view);
        final EditText etPassword = ButterKnife.findById(view, R.id.etPassword);
        TextView tvTitle = ButterKnife.findById(view, R.id.tvTitle);
        tvTitle.setText(context.getString(R.string.delete_account));

        builder.setPositiveButton(context.getString(R.string.delete_account), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError(context.getString(R.string.enter_your_password));
                    return;
                }

                if (!ValidationUtils.isPasswordValid(password)) {
                    etPassword.setError(context.getString(R.string.enter_valid_password));
                    return;
                }

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), password);

                    final Loading loading = new Loading(context);
                    loading.show(context.getString(R.string.updating_passsword));

                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseUser.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        loading.dismiss();
                                                        if (task.isSuccessful()) {
                                                            alertDialog.dismiss();
                                                            Intent intent = new Intent(context, LoginActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(intent);
                                                            ((Activity) context).finish();
                                                            ToastUtils.showShortToast(context, R.string.your_account_is_deleted);
                                                        } else {
                                                            ToastUtils.showShortToast(context, R.string.failed_to_delete_your_account);
                                                        }
                                                    }
                                                });
                                    } else {
                                        loading.dismiss();
                                        ToastUtils.showShortToast(context, R.string.failed_to_delete_your_account);
                                    }
                                }
                            });
                }
            }
        });

    }
}
