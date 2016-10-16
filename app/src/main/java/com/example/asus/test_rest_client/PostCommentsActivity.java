package com.example.asus.test_rest_client;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.test_rest_client.adapter.PostCommentsAdapter;
import com.example.asus.test_rest_client.model.Comments;
import com.example.asus.test_rest_client.model.Post;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCommentsActivity extends AppCompatActivity implements PostCommentsAdapter.AddingRePost{
    private RestService intf;
    private Integer idOfRepost;
    private TextView rePostName;
    private TextView sendBut;
    private EditText commentText;
    private ImageView imgRepost;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        final PostCommentsAdapter adapter = new PostCommentsAdapter(PostCommentsActivity.this);
        intf = MainActivity.retrofit.create(RestService.class);
        idOfRepost= null;
        rePostName = (TextView) findViewById(R.id.repost_author);
        imgRepost = (ImageView) findViewById(R.id.repost_comments_image);
        sendBut = (TextView) findViewById(R.id.comment_add);
        commentText = (EditText) findViewById(R.id.comments_text);
        sendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                Map<String, Object> map= new HashMap<>();
                if(!commentText.getText().toString().equals("")) {
                    map.put("text", commentText.getText().toString());
                    if (idOfRepost != null) {
                        map.put("to", idOfRepost);
                    }
                    intf.addComment(PostActivity.post.getId(), MainActivity.preferenceHelper.getToken(), map).enqueue(new Callback<Comments>() {
                        @Override
                        public void onResponse(Call<Comments> call, Response<Comments> response) {

                            adapter.refresh();
                            commentText.setText(null);
                            idOfRepost = null;
                            rePostName.setText(null);
                            rePostName.setVisibility(View.GONE);
                            imgRepost.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<Comments> call, Throwable t) {

                        }

                    });
                }
            }
        });
        rePostName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idOfRepost = null;
                rePostName.setText(null);
                rePostName.setVisibility(View.GONE);
                imgRepost.setVisibility(View.GONE);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvForComments);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                hideKeyboard();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void RePostAdded(String name, Integer id) {
        this.idOfRepost=id;
        rePostName.setText(name);
        rePostName.setVisibility(View.VISIBLE);
        imgRepost.setVisibility(View.VISIBLE);
    }
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
