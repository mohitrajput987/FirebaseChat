package com.otb.firebasechat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.otb.firebasechat.R;
import com.otb.firebasechat.utils.CommonUtils;
import com.otb.firebasechat.utils.Loading;
import com.otb.firebasechat.utils.ToastUtils;
import com.otb.firebasechat.utils.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mohit Rajput on 4/7/17.
 * This activity presents login screen
 */
public class LoginActivity extends AppCompatActivity {

    private final static String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.cbShowPassword)
    AppCompatCheckBox cbShowPassword;
    private Context context;
    private String email, password;
    private FirebaseAuth firebaseAuth;
    private Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        ButterKnife.bind(this);
        setShowPasswordListener();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setShowPasswordListener() {
        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @OnClick(R.id.btnLogin)
    void onLoginBtnClicked() {
        if (areFieldsValid()) {
            login();
        }
    }

    @OnClick(R.id.tvRegister)
    void onRegisterBtnClicked() {
        Intent intent = new Intent(context, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tvForgotPassword)
    void onForgotPasswordBtnClicked() {
        Intent intent = new Intent(context, ForgotPasswordActivity.class);
        startActivity(intent);
    }


    private boolean areFieldsValid() {
        etEmail.setError(null);
        etPassword.setError(null);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.enter_your_email));
            return false;
        }

        if (!ValidationUtils.isEmailValid(email)) {
            etEmail.setError(getString(R.string.enter_valid_email));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.enter_your_password));
            return false;
        }

        if (!ValidationUtils.isPasswordValid(password)) {
            etPassword.setError(getString(R.string.enter_valid_password));
            return false;
        }

        return true;
    }

    private void login() {
        loading = new Loading(context);
        loading.show(getString(R.string.logging_in));
        if (CommonUtils.isNetworkAvailable(context)) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loading.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (!firebaseUser.isEmailVerified()) {
                            openDialogToVerifyEmail();
                        } else {
                            ToastUtils.showLongToast(context, R.string.logged_in_successfully);
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        ToastUtils.showLongToast(context, R.string.this_email_is_not_registered);
                    }
                }
            });
        } else {
            ToastUtils.showShortToast(context, R.string.network_not_available);
        }
    }

    private void openDialogToVerifyEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.email_not_verified));
        builder.setPositiveButton(context.getString(R.string.resend), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendVerificationEmail();
            }
        });

        builder.setNegativeButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendVerificationEmail() {
        Log.d(TAG, "sendVerificationEmail()");
        loading = new Loading(context);
        loading.show(getString(R.string.sending_verification_email));
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.dismiss();
                        if (task.isSuccessful()) {
                            ToastUtils.showLongToast(context, R.string.verfication_email_sent_to_user, user.getEmail());
                        } else {
                            ToastUtils.showLongToast(context, R.string.failed_to_send_verification_email, user.getEmail());
                        }
                    }
                });
    }
}
