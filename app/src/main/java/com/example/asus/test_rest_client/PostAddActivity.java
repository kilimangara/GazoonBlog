package com.example.asus.test_rest_client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.asus.test_rest_client.model.Avatar;
import com.example.asus.test_rest_client.model.Post;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAddActivity extends AppCompatActivity {
    public final static int PICK_IMAGE = 0;
    private Animation anim;
    private EditText edTitle;
    private EditText edText;
    private ImageView imageview;
    private File imgfile;
    private boolean photoAdded;
    private RestService intf;
    private AlertDialog alertDialog;
    private Dialog dialogdown;
    private boolean forPatch;
    private int idOfPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add_avtivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.nested);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(oldScrollY-scrollY != 0){
                    hideKeyboard();
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dialogdown = new Dialog(this, R.style.Dialog);
        dialogdown.setContentView(R.layout.dialog_model);
        dialogdown.setCancelable(false);
        dialogdown.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        edText = (EditText) findViewById(R.id.post_text);
        edTitle = (EditText) findViewById(R.id.post_title);
        imageview = (ImageView) findViewById(R.id.add_photo);
        intf = MainActivity.retrofit.create(RestService.class);
        anim = AnimationUtils.loadAnimation(this, R.anim.shake);
        forPatch=false;
        if(getIntent().getBooleanExtra("fromNote",false)){
            edText.setText(getIntent().getStringExtra("text"));
            edTitle.setText(getIntent().getStringExtra("title"));
            idOfPost = getIntent().getIntExtra("id", 0);
            if(getIntent().getStringExtra("logo")!=null) {
                imageview.setVisibility(View.VISIBLE);
                MainActivity.imageLoader.displayImage(getIntent().getStringExtra("logo"), imageview);
            }
            forPatch = true;
        }

    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            if(edTitle.length() == 0){
                edTitle.startAnimation(anim);
                edTitle.setError("Please enter title!");

            }else{
            if(edText.length() == 0){
                edText.startAnimation(anim);
                edText.setError("Please enter text!");
            }else {
                alertDialog = setUpDialog();

                alertDialog.show();
            }
            }
            return true;
        }
        if(id == R.id.action_add_photo){
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            startActivityForResult(photoPicker, PICK_IMAGE);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        imageview.setVisibility(View.VISIBLE);
                        imageview.setImageBitmap(img);

                        imgfile = new File(getRealPathFromURI(uri));
                        photoAdded = true;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }
    }

    public void addPost(){
        dialogdown.show();
        if(photoAdded){
            RequestBody fileBody  =RequestBody.create(MediaType.parse("multipart/form-data"), imgfile);
            final MultipartBody.Part requestBody = MultipartBody.Part.createFormData("image", imgfile.getName(), fileBody );
            final Call<Avatar> saveImg =  intf.loadImage(requestBody, MainActivity.preferenceHelper.getToken());
            saveImg.enqueue(new Callback<Avatar>() {
                @Override
                public void onResponse(Call<Avatar> call, Response<Avatar> response) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", edTitle.getText().toString());
                    map.put("text", edText.getText().toString());
                    map.put("logo", response.body().getId());
                    Call<Post> savepost = intf.savePostOnServer(map, MainActivity.preferenceHelper.getToken());
                    savepost.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            intf.publishPost(response.body().getId(), MainActivity.preferenceHelper.getToken()).enqueue(new Callback<Post>() {
                                @Override
                                public void onResponse(Call<Post> call, Response<Post> response) {
                                    dialogdown.dismiss();
                                    Log.d("mytags", "add post with logo");
                                    setResult(MainActivity.RESULT_ADDED);
                                    finish();
                                    overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                                }

                                @Override
                                public void onFailure(Call<Post> call, Throwable t) {
                                    Log.d("mytags", t.getMessage());
                                    dialogdown.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Log.d("mytags", t.getMessage());
                            dialogdown.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(Call<Avatar> call, Throwable t) {
                    Log.d("mytags", t.getMessage());
                    dialogdown.dismiss();
                }
            });
        }
        else{
            Map<String, Object> map = new HashMap<>();
            map.put("title", edTitle.getText().toString());
            map.put("text", edText.getText().toString());
            Call<Post> savepost = intf.savePostOnServer(map, MainActivity.preferenceHelper.getToken());
            savepost.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    intf.publishPost(response.body().getId(), MainActivity.preferenceHelper.getToken()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            dialogdown.dismiss();
                            Log.d("mytags", "add post without logo");
                            setResult(MainActivity.RESULT_ADDED);
                            finish();
                            overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Log.d("mytags", t.getMessage());
                            dialogdown.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.d("mytags", t.getMessage());
                    dialogdown.dismiss();
                }
            });
        }
    }
    public void patchPost(){
        dialogdown.show();
        if(photoAdded){
            RequestBody fileBody  =RequestBody.create(MediaType.parse("multipart/form-data"), imgfile);
            final MultipartBody.Part requestBody = MultipartBody.Part.createFormData("image", imgfile.getName(), fileBody );
            final Call<Avatar> saveImg =  intf.loadImage(requestBody, MainActivity.preferenceHelper.getToken());
            saveImg.enqueue(new Callback<Avatar>() {
                @Override
                public void onResponse(Call<Avatar> call, Response<Avatar> response) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", edTitle.getText().toString());
                    map.put("text", edText.getText().toString());
                    map.put("logo", response.body().getId());
                    Call<Post> savepost = intf.patchPost(idOfPost, MainActivity.preferenceHelper.getToken(),map);
                    savepost.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            intf.publishPost(response.body().getId(), MainActivity.preferenceHelper.getToken()).enqueue(new Callback<Post>() {
                                @Override
                                public void onResponse(Call<Post> call, Response<Post> response) {
                                    dialogdown.dismiss();
                                    setResult(MainActivity.RESULT_PATCHED);
                                    Log.d("mytags", "patch post with logo");
                                    finish();
                                    overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                                }

                                @Override
                                public void onFailure(Call<Post> call, Throwable t) {
                                    Log.d("mytags", t.getMessage());
                                    dialogdown.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Log.d("mytags", t.getMessage());
                            dialogdown.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(Call<Avatar> call, Throwable t) {
                    Log.d("mytags", t.getMessage());
                    dialogdown.dismiss();
                }
            });
        }
        else{
            Map<String, Object> map = new HashMap<>();
            map.put("title", edTitle.getText().toString());
            map.put("text", edText.getText().toString());
            Call<Post> patchpost = intf.patchPost(idOfPost, MainActivity.preferenceHelper.getToken(), map);
            patchpost.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    intf.publishPost(response.body().getId(), MainActivity.preferenceHelper.getToken()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            dialogdown.dismiss();
                            setResult(MainActivity.RESULT_PATCHED);
                            finish();
                            overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Log.d("mytags", t.getMessage());
                            dialogdown.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.d("mytags", t.getMessage());
                    dialogdown.dismiss();
                }
            });
        }
    }
    public AlertDialog setUpDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("What fo you want to do?");
        builder.setPositiveButton("Post It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if(!forPatch) {
                    addPost();
                }
                else{
                    patchPost();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Save as note", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialogdown.show();
                if(photoAdded){
                    RequestBody fileBody  =RequestBody.create(MediaType.parse("multipart/form-data"), imgfile);
                    final MultipartBody.Part requestBody = MultipartBody.Part.createFormData("image", imgfile.getName(), fileBody );
                    final Call<Avatar> saveImg =  intf.loadImage(requestBody, MainActivity.preferenceHelper.getToken());
                    saveImg.enqueue(new Callback<Avatar>() {
                        @Override
                        public void onResponse(Call<Avatar> call, Response<Avatar> response) {
                            Map<String, Object> map = new HashMap<>();
                            Call<Post> savepost;
                            map.put("title", edTitle.getText().toString());
                            map.put("text", edText.getText().toString());
                            map.put("logo", response.body().getId());
                            if(!forPatch) {
                                savepost = intf.savePostOnServer(map, MainActivity.preferenceHelper.getToken());
                            }
                            else{
                                savepost = intf.patchPost(idOfPost, MainActivity.preferenceHelper.getToken(), map);
                            }
                            savepost.enqueue(new Callback<Post>() {
                                @Override
                                public void onResponse(Call<Post> call, Response<Post> response) {
                                    dialogdown.dismiss();
                                    setResult(MainActivity.RESULT_ADDED);
                                    finish();
                                    overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                                }

                                @Override
                                public void onFailure(Call<Post> call, Throwable t) {
                                    dialogdown.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Avatar> call, Throwable t) {
                            dialogdown.dismiss();
                        }
                    });
                }
                else{
                    Map<String, Object> map = new HashMap<>();
                    Call<Post> savepost;
                    map.put("title", edTitle.getText().toString());
                    map.put("text", edText.getText().toString());

                    if(!forPatch) {
                        savepost = intf.savePostOnServer(map, MainActivity.preferenceHelper.getToken());
                    }
                    else{
                        savepost = intf.patchPost(idOfPost, MainActivity.preferenceHelper.getToken(), map);
                    }
                    savepost.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            dialogdown.dismiss();
                            setResult(MainActivity.RESULT_ADDED);
                            finish();
                            overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            dialogdown.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        return builder.create();

    }
}
