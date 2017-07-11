package com.otb.firebasechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.otb.firebasechat.R;
import com.otb.firebasechat.constants.AppConstants;
import com.otb.firebasechat.utils.CommonUtils;
import com.otb.firebasechat.utils.DialogUtils;
import com.otb.firebasechat.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        ButterKnife.bind(this);
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar.setTitle(AppConstants.EMPTY);
        toolbar.inflateMenu(R.menu.menu_home);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (CommonUtils.isNetworkAvailable(context)) {
                    switch (item.getItemId()) {
                        case R.id.action_change_password:
                            Intent intent = new Intent(context, ChangePasswordActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.action_delete_account:
                            DialogUtils.openDialogToDeleteAccount(context);
                            break;
                        case R.id.action_logout:
                            DialogUtils.openDialogToLogout(context);
                            break;
                    }
                } else {
                    ToastUtils.showShortToast(context, R.string.network_not_available);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
}
