package com.example.asus.test_rest_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.test_rest_client.adapter.PostsAdapter;
import com.example.asus.test_rest_client.huyznaet.PreferenceHelper;
import com.example.asus.test_rest_client.model.Commentss;
import com.example.asus.test_rest_client.model.Post;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {
    public static Post post ;
    TextView tvTitle;
    TextView tvDate;
    TextView tvText;
    TextView tvComments;
    TextView tvAuthor;
    ImageView img;
    RestService intf;
    ImageView imgPost;
    public static Commentss commentss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        post = PostsAdapter.results.get(getIntent().getIntExtra("ID",0));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvAuthor = (TextView) findViewById(R.id.tvForName);
        tvDate = (TextView) findViewById(R.id.tvForDate);
        tvTitle = (TextView) findViewById(R.id.tvPostsTitle);
        tvText = (TextView) findViewById(R.id.tvPostsText);
        tvComments = (TextView) findViewById(R.id.numb_comments);
        imgPost = (ImageView) findViewById(R.id.post_image);
        img = (ImageView) findViewById(R.id.post_comments_image);
        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, PostCommentsActivity.class);

                startActivity(intent);
            }
        };
        tvComments.setOnClickListener(listener);
        img.setOnClickListener(listener);
        tvAuthor.setText(post.getAuthor().getName());
        tvDate.setText(Utils.getFullDate(post.getPublishedAt()));
        tvText.setText(post.getText());
        tvTitle.setText(post.getTitle());
        intf = MainActivity.retrofit.create(RestService.class);
        if(post.getLogo()!=null) {
            imgPost.setVisibility(View.VISIBLE);
            MainActivity.imageLoader.displayImage(post.getLogo().getMedium().getUrl(), imgPost);
        }
        Call<Commentss> commentssCall =intf.getPostComments(post.getId(), MainActivity.preferenceHelper.getToken());
        commentssCall.enqueue(new Callback<Commentss>() {
            @Override
            public void onResponse(Call<Commentss> call, Response<Commentss> response) {
                tvComments.setText(String.valueOf(response.body().getCount()));
                Log.d("mytags", " "+response.body().getCount());
                commentss = response.body();
            }

            @Override
            public void onFailure(Call<Commentss> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Call<Commentss> commentssCall =intf.getPostComments(post.getId(), MainActivity.preferenceHelper.getToken());
        commentssCall.enqueue(new Callback<Commentss>() {
            @Override
            public void onResponse(Call<Commentss> call, Response<Commentss> response) {
                tvComments.setText(String.valueOf(response.body().getCount()));
                Log.d("mytags", " "+response.body().getCount());
                commentss = response.body();
            }

            @Override
            public void onFailure(Call<Commentss> call, Throwable t) {

            }
        });
    }
}
