package com.example.asus.test_rest_client.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.Utils;
import com.example.asus.test_rest_client.model.Commentss;
import com.example.asus.test_rest_client.model.Post;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;


public class ClosePostsFragment extends Fragment {
    TextView tvTitle;
    TextView tvDate;
    TextView tvText;
    TextView tvAuthor;
    ImageView imgPost;
    public Post post;
    Commentss comments;
    private Observable<Commentss> commentssObservable;
    interface OnObservableCreated{

    }

    public ClosePostsFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_post, container, false);
        post = MainActivity.gson.fromJson(getArguments().getString("Post"),Post.class);
        tvAuthor = (TextView) v.findViewById(R.id.tvForName);
        tvDate = (TextView) v.findViewById(R.id.tvForDate);
        tvTitle = (TextView) v.findViewById(R.id.tvPostsTitle);
        tvText = (TextView) v.findViewById(R.id.tvPostsText);
        imgPost = (ImageView) v.findViewById(R.id.post_image);
        tvAuthor.setText(post.getAuthor().getName());
        tvDate.setText(Utils.getFullDate(post.getPublishedAt()));
        tvText.setText(post.getText());
        tvTitle.setText(post.getTitle());
        if(post.getLogo()!=null) {
            imgPost.setVisibility(View.VISIBLE);
            MainActivity.imageLoader.displayImage(post.getLogo().getMedium().getUrl(), imgPost);
        }
       setCommentssObservable(Observable.defer(new Func0<Observable<Commentss>>() {
            @Override
            public Observable<Commentss> call() {
                return MainActivity.apiFactory.getService().getPostComments(post.getId(), MainActivity.preferenceHelper.getToken()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .cache();
            }
        }));
        getCommentssObservable().subscribe(new Subscriber<Commentss>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("mytags", e.getMessage());
            }

            @Override
            public void onNext(Commentss commentss) {
                comments = commentss;
            }
        });

        return v;
    }
    public int getCommentNumber(){
        if(comments!= null) {
            return comments.getCount();
        }
        return 0;
    }

    public void setCommentssObservable(Observable<Commentss> observable){
        commentssObservable = observable;
    }
    public Observable<Commentss> getCommentssObservable(){
        return commentssObservable;
    }



    @Override
    public void onResume() {
        super.onResume();
        setCommentssObservable(Observable.defer(new Func0<Observable<Commentss>>() {
            @Override
            public Observable<Commentss> call() {
                return MainActivity.apiFactory.getService().getPostComments(post.getId(), MainActivity.preferenceHelper.getToken()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .cache();
            }
        }));
    }

}
