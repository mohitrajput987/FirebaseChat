package com.otb.firebasechat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.otb.firebasechat.R;
import com.otb.firebasechat.utils.CommonUtils;
import com.otb.firebasechat.utils.DialogUtils;
import com.otb.firebasechat.utils.Loading;
import com.otb.firebasechat.utils.ToastUtils;
import com.otb.firebasechat.utils.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.etCurrentPassword)
    EditText etCurrentPassword;
    @BindView(R.id.etNewPassword)
    EditText etNewPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    private Context context;
    private String currentPassword, newPassword, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        context = ChangePasswordActivity.this;
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnChangePassword)
    void onChangePasswordBtnClicked() {
        if (areFieldsValid()) {
            changePassword();
        }
    }

    private void changePassword() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (CommonUtils.isNetworkAvailable(context)) {
            if (firebaseUser != null) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(firebaseUser.getEmail(), currentPassword);

                final Loading loading = new Loading(context);
                loading.show(getString(R.string.updating_passsword));

                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    loading.dismiss();
                                                    if (task.isSuccessful()) {
                                                        ToastUtils.showShortToast(context, R.string.password_updated_successfully);
                                                        finish();
                                                    } else {
                                                        ToastUtils.showShortToast(context, R.string.failed_to_update_password);
                                                    }
                                                }
                                            });
                                } else {
                                    loading.dismiss();
                                    ToastUtils.showShortToast(context, R.string.failed_to_update_password);
                                }
                            }
                        });
            } else {
                DialogUtils.openDialogOnSessionExpired(context);
            }
        } else {
            ToastUtils.showShortToast(context, R.string.network_not_available);
        }
    }


    private boolean areFieldsValid() {
        etCurrentPassword.setError(null);
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);
        currentPassword = etCurrentPassword.getText().toString();
        newPassword = etNewPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();


        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError(getString(R.string.enter_your_password));
            return false;
        }

        if (!ValidationUtils.isPasswordValid(currentPassword)) {
            etCurrentPassword.setError(getString(R.string.enter_valid_password));
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError(getString(R.string.enter_new_password));
            return false;
        }

        if (!ValidationUtils.isPasswordValid(newPassword)) {
            etNewPassword.setError(getString(R.string.enter_valid_password));
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.re_enter_new_password));
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.password_not_same));
            return false;
        }

        return true;
    }
}
