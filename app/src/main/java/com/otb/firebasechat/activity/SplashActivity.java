package com.otb.firebasechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.otb.firebasechat.R;

/**
 * Created by Mohit Rajput on 4/7/17.
 * This activity creates splash screen when app starts
 */
public class SplashActivity extends AppCompatActivity {

    private final static long SPLASH_DISPLAY_TIME_IN_MILLIS = 3 * 1000;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = SplashActivity.this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToNextActivity();
            }
        }, SPLASH_DISPLAY_TIME_IN_MILLIS);
    }

    private void moveToNextActivity() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Intent intent;
        if (firebaseUser == null) {
            intent = new Intent(context, LoginActivity.class);
        } else {
            intent = new Intent(context, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
