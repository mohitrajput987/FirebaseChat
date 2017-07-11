package com.otb.firebasechat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.otb.firebasechat.R;
import com.otb.firebasechat.utils.CommonUtils;
import com.otb.firebasechat.utils.Loading;
import com.otb.firebasechat.utils.ToastUtils;
import com.otb.firebasechat.utils.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    private final static String TAG = ForgotPasswordActivity.class.getSimpleName();
    @BindView(R.id.etEmail)
    EditText etEmail;
    private Context context;
    private String email;
    private FirebaseAuth firebaseAuth;
    private Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        context = ForgotPasswordActivity.this;
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnResetPassword)
    void onResetBtnClicked() {
        if (areFieldsValid()) {
            sendPasswordResetMail();
        }
    }

    private boolean areFieldsValid() {
        etEmail.setError(null);
        email = etEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.enter_your_email));
            return false;
        }

        if (!ValidationUtils.isEmailValid(email)) {
            etEmail.setError(getString(R.string.enter_valid_email));
            return false;
        }
        return true;
    }

    private void sendPasswordResetMail() {
        if (!CommonUtils.isNetworkAvailable(context)) {
            ToastUtils.showShortToast(context, R.string.network_not_available);
        }
        loading = new Loading(context);
        loading.show(getString(R.string.sending_reset_mail));
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.dismiss();
                        if (task.isSuccessful()) {
                            ToastUtils.showLongToast(context, R.string.we_have_sent_you_reset_mail);
                            finish();
                        } else {
                            ToastUtils.showShortToast(context, R.string.failed_to_send_mail);
                        }
                    }
                });
    }
}
