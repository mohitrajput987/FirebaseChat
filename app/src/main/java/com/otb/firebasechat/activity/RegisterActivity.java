package com.otb.firebasechat.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class RegisterActivity extends AppCompatActivity {

    private final static String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.cbShowPassword)
    AppCompatCheckBox cbShowPassword;
    @BindView(R.id.etName)
    EditText etName;
    private Context context;
    private String email, password, name;
    private FirebaseAuth firebaseAuth;
    private Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = RegisterActivity.this;
        ButterKnife.bind(this);
        setShowPasswordListener();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnRegister)
    void onRegisterBtnClicked() {
        if (areFieldsValid()) {
            register();
        }
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

    private boolean areFieldsValid() {
        etEmail.setError(null);
        etPassword.setError(null);
        etName.setError(null);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        name = etName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.enter_your_first_name));
            return false;
        }

        if (!ValidationUtils.isNameValid(name)) {
            etName.setError(getString(R.string.enter_valid_first_name));
            return false;
        }

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

    private void register() {
        if (CommonUtils.isNetworkAvailable(context)) {
            loading = new Loading(context);
            loading.show(getString(R.string.registering));
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendVerificationEmail();
                    } else {
                        loading.dismiss();
                        ToastUtils.showLongToast(context, R.string.unexpected_error_occurred);
                    }
                }
            });
        } else {
            ToastUtils.showShortToast(context, R.string.network_not_available);
        }
    }

    private void sendVerificationEmail() {
        Log.d(TAG, "sendVerificationEmail()");
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.dismiss();
                        if (task.isSuccessful()) {
                            ToastUtils.showLongToast(context, R.string.verfication_email_sent_to_user, user.getEmail());
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        } else {
                            ToastUtils.showShortToast(context, R.string.failed_to_send_verification_email);
                        }
                    }
                });
    }
}
