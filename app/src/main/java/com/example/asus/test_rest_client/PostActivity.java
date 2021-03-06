package com.example.asus.test_rest_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.test_rest_client.adapter.PostPager;
import com.example.asus.test_rest_client.adapter.PostsAdapter;
import com.example.asus.test_rest_client.fragments.ClosePostsFragment;
import com.example.asus.test_rest_client.huyznaet.PreferenceHelper;
import com.example.asus.test_rest_client.model.Commentss;
import com.example.asus.test_rest_client.model.Post;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class PostActivity extends AppCompatActivity implements ClosePostsFragment.OnFragmentCreatedListener {
    TextView tvComments;
    ImageView img;
    ViewPager pager;
    PostPager adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        ArrayList<Post> temp= MainActivity.gson.fromJson(getIntent().getStringExtra("results"), new TypeToken<List<Post>>(){}.getType());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pager = (ViewPager) findViewById(R.id.post_pager);
        adapter = new PostPager(getSupportFragmentManager() ,temp);
        pager.setAdapter(adapter);
        pager.setCurrentItem(getIntent().getIntExtra("ID",0));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                adapter.getFragment(position).getCommentssObservable().subscribe(new Subscriber<Commentss>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mytags", e.getMessage());
                    }

                    @Override
                    public void onNext(Commentss integer) {
                        Log.d("mytags", "num "+integer.getCount());
                        tvComments.setText(String.valueOf(integer.getCount()));
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        Log.d("mytags", "prov");
        tvComments = (TextView) findViewById(R.id.numb_comments);
        img = (ImageView) findViewById(R.id.post_comments_image);
        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, PostCommentsActivity.class);
                intent.putExtra("numb",((ClosePostsFragment)adapter.getItem(pager.getCurrentItem())).getCommentNumber());
                intent.putExtra("ID", ((ClosePostsFragment)adapter.getItem(pager.getCurrentItem())).post.getId());
                startActivity(intent);
            }
        };
        tvComments.setOnClickListener(listener);
        img.setOnClickListener(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.getFragment(pager.getCurrentItem()).getCommentssObservable().subscribe(new Subscriber<Commentss>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Commentss commentss) {
                tvComments.setText(String.valueOf(commentss.getCount()));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("mytags", "PostActivityResumed");
    }

    @Override
    public void fragmentCreated() {
        adapter.getFragment(pager.getCurrentItem()).getCommentssObservable().subscribe(new Subscriber<Commentss>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Commentss commentss) {
                tvComments.setText(String.valueOf(commentss.getCount()));
            }
        });
    }
}
