package com.example.asus.test_rest_client.serializers;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.RestService;
import com.example.asus.test_rest_client.adapter.CommentsAdapter;
import com.example.asus.test_rest_client.adapter.UserPostsAdapter;
import com.example.asus.test_rest_client.model.Avatar;
import com.example.asus.test_rest_client.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSettings extends AppCompatActivity{


    private CircleImageView avatarView;
    private ImageView imageView;
    private EditText edName;
    private NestedScrollView scrollView;
    private EditText edAbout;
    private Button button;
    private Dialog dialog;
    private boolean isChanged;
    private RestService intf;
    private File imgfile;
    public static final int PICK_IMAGE = 1;
    public static final String IMAGE_NAME= "Avatar.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        isChanged = false;
        imageView = (ImageView) findViewById(R.id.settings_imgView);
        avatarView = (CircleImageView) findViewById(R.id.civ_user_avatar);
        button = (Button) findViewById(R.id.user_info_apply);
        scrollView = (NestedScrollView) findViewById(R.id.user_activity_container);
        CollapsingToolbarLayout collapsingToolbarLayout  = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("Settings");
        setSupportActionBar(toolbar);
        dialog = new Dialog(this, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_model);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUser();
            }
        });
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                dialog.show();
                startActivityForResult(photoPicker, PICK_IMAGE);
            }
        });
        edName = (EditText) findViewById(R.id.user_name);
        edAbout = (EditText) findViewById(R.id.user_about_me);
        TextWatcher textWatcher= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        edName.setText(MainActivity.user.getName());
        if(MainActivity.user.getAboutMe()!=null){
            edAbout.setText(MainActivity.user.getAboutMe());
        }
        edName.addTextChangedListener(textWatcher);
        edAbout.addTextChangedListener(textWatcher
        );
        if(MainActivity.user.getAvatar() != null) {
            MainActivity.imageLoader.displayImage(MainActivity.user.getAvatar().getMedium().getUrl(), avatarView);
        }

        RecyclerView listView= (RecyclerView) findViewById(R.id.user_posts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        UserPostsAdapter adapter = new UserPostsAdapter(this);
        listView.setAdapter(adapter);
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getApplicationContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri uri = data.getData();
                        final InputStream imgStream = getApplicationContext().getContentResolver().openInputStream(uri);
                        final Bitmap img = BitmapFactory.decodeStream(imgStream);
                        avatarView.setImageBitmap(img);

                        imgfile = new File(getRealPathFromURI(uri));
                        button.setEnabled(true);
                        isChanged = true;
                        dialog.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }
        dialog.dismiss();
    }
    public void loadUser(){
        intf = MainActivity.retrofit.create(RestService.class);
        final Map<String, Object> map = new HashMap<>();
        dialog.show();
        if(isChanged){
            RequestBody fileBody  =RequestBody.create(MediaType.parse("multipart/form-data"), imgfile);
            MultipartBody.Part requestBody = MultipartBody.Part.createFormData("image", imgfile.getName(), fileBody );
            Call<Avatar> call= intf.loadImage(requestBody,MainActivity.preferenceHelper.getToken());
                call.enqueue(new Callback<Avatar>() {
                    @Override
                    public void onResponse(Call<Avatar> call, Response<Avatar> response) {
                        if(response.body()!= null) {
                            MainActivity.user.setAvatar(response.body());
                            map.put("avatar", response.body().getId());
                        }
                        if(edAbout.getText().toString().length() != 0) {
                            MainActivity.user.setAboutMe(edAbout.getText().toString());
                            map.put("about_me",edAbout.getText().toString());
                        }
                        if(edName.getText().toString().length() != 0){
                            MainActivity.user.setName(edName.getText().toString());
                            map.put("name",edName.getText().toString());
                        }
                        Call<User> userCall = intf.patchOwnInfo(MainActivity.user.getId(), MainActivity.preferenceHelper.getToken(), map);
                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                Snackbar.make(scrollView, "Information applied", Snackbar.LENGTH_LONG).show();
                                MainActivity.preferenceHelper.putUser(MainActivity.user);
                                dialog.dismiss();
                                button.setEnabled(false);
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Log.d("mytags", t.getMessage());
                                dialog.dismiss();
                                Snackbar.make(scrollView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Avatar> call, Throwable t) {
                        Snackbar.make(scrollView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                        dialog.dismiss();
                        Log.d("mytags", t.getMessage());
                    }

                });
        }
        else {
            if (edAbout.getText().toString().length() != 0) {
                MainActivity.user.setAboutMe(edAbout.getText().toString());
                map.put("about_me",edAbout.getText().toString());
            }
            if (edName.getText().toString().length() != 0) {
                MainActivity.user.setName(edName.getText().toString());
                map.put("name",edName.getText().toString());
            }
            Call<User> userCall = intf.patchOwnInfo(MainActivity.user.getId(), MainActivity.preferenceHelper.getToken(),map );
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Snackbar.make(scrollView, "Information applied", Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                    MainActivity.preferenceHelper.putUser(MainActivity.user);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Snackbar.make(scrollView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                    Log.d("mytags", t.getMessage());
                }
            });
        }
    }

}
