package com.otb.firebasechat.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.otb.firebasechat.R;
import com.otb.firebasechat.constants.AppConstants;
import com.otb.firebasechat.utils.Loading;
import com.otb.firebasechat.utils.ToastUtils;
import com.otb.firebasechat.utils.ValidationUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.filepicker.Filepicker;
import io.filepicker.models.FPFile;

public class ProfileActivity extends AppCompatActivity {

    private final static String TAG = ProfileActivity.class.getSimpleName();
    @BindView(R.id.ivProfilePic)
    CircleImageView ivProfilePic;
    @BindView(R.id.etName)
    EditText etName;
    private Context context;
    private String imageUrl, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = ProfileActivity.this;
        ButterKnife.bind(this);
        Filepicker.setKey(AppConstants.FILESTACK_API_KEY);
        setDataInView();
    }

    @OnClick(R.id.ivProfilePic)
    void onProfilePicClicked() {
        Intent intent = new Intent(this, Filepicker.class);
        startActivityForResult(intent, Filepicker.REQUEST_CODE_GETFILE);
    }

    @OnClick(R.id.btnUpdate)
    void onUpdateBtnClicked() {
        if (areFieldsValid()) {
            updateProfile();
        }
    }

    private void setDataInView(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(!TextUtils.isEmpty(firebaseUser.getDisplayName())){
            etName.setText(firebaseUser.getDisplayName());
        }
        if(firebaseUser.getPhotoUrl()!=null)
        new AQuery(context).id(ivProfilePic).image(firebaseUser.getPhotoUrl().toString());
    }

    private boolean areFieldsValid() {
        name = etName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.enter_your_name));
            return false;
        }

        if (!ValidationUtils.isNameValid(name)) {
            etName.setError(getString(R.string.enter_valid_name));
            return false;
        }

        return true;
    }

    private void updateProfile() {
        final Loading loading = new Loading(context);
        loading.show(getString(R.string.updating_profile));
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(imageUrl == null ? AppConstants.EMPTY : imageUrl))
                .build();
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            ToastUtils.showShortToast(context, R.string.profile_updated_successfully);
                            finish();
                        } else {
                            ToastUtils.showShortToast(context, R.string.unexpected_error_occurred);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Filepicker.REQUEST_CODE_GETFILE) {
            if (resultCode == RESULT_OK) {

                // Filepicker always returns array of FPFile objects
                ArrayList<FPFile> fpFiles = data.getParcelableArrayListExtra(Filepicker.FPFILES_EXTRA);

                // Option multiple was not set so only 1 object is expected
                FPFile file = fpFiles.get(0);
                imageUrl = file.getUrl();
                new AQuery(context).id(ivProfilePic).image(imageUrl);
                // Do something cool with the result
            } else {
                // Handle errors here
            }

        }
    }
}
