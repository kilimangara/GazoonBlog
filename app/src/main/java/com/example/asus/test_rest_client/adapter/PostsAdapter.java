package com.example.asus.test_rest_client.adapter;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.test_rest_client.AnimationUtils;
import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.PostActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.RestService;
import com.example.asus.test_rest_client.Utils;
import com.example.asus.test_rest_client.fragments.PostsFragment;
import com.example.asus.test_rest_client.model.Post;
import com.example.asus.test_rest_client.model.Posts;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static public List<Post> results;
    private PostsFragment fragment;
    private String query;
    private int page;
    private int previousPosition;
    private boolean hasEnded;
    private OnPostsLoadFinished listener;

    public interface OnPostsLoadFinished{
        void postsLoad();
    }

    public PostsAdapter(PostsFragment postsFragment){
        fragment = postsFragment;
        listener = fragment;
        page = 0;
        hasEnded = false;
        previousPosition=0;
        results = new ArrayList<>();
        query ="";
        getPage(query);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.model_post, parent, false);
            TextView title = (TextView) v.findViewById(R.id.postTitle);
            TextView date = (TextView) v.findViewById(R.id.postDate);
            TextView author = (TextView) v.findViewById(R.id.postAuthor);

        return new PostsViewHolder(v, title, date, author);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Post post = results.get(position);
        holder.itemView.setEnabled(true);
        final PostsViewHolder postHolder= (PostsViewHolder) holder;
        View itemView = holder.itemView;
        if(position == getItemCount() -1 && !hasEnded){
            getPage(query);
        }
        itemView.setVisibility(View.VISIBLE);
        postHolder.titleView.setText(post.getTitle());
        postHolder.dateView.setText(Utils.getFullDate(post.getPublishedAt()));
        postHolder.authorView.setText(post.getAuthor().getName());
            if (position > previousPosition) {
                AnimationUtils.animateCardView(postHolder, true);

            }
            if(position< previousPosition) {
                AnimationUtils.animateCardView(postHolder, false);

            }
        previousPosition= position;
        postHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getContext(), PostActivity.class);
                intent.putExtra("ID", postHolder.getAdapterPosition());
                intent.putExtra("results", MainActivity.gson.toJson(results));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragment.getActivity(),
                        new Pair<View, String>(postHolder.dateView, fragment.getContext().getString(R.string.transition_name_date)),
                        new Pair<View, String>(postHolder.authorView, fragment.getContext().getString(R.string.transition_name_author)),
                        new Pair<View, String>(postHolder.titleView, fragment.getContext().getString(R.string.transition_name_title))
                        );
                ActivityCompat.startActivity(fragment.getActivity(), intent, options.toBundle());

            }
        });



    }

    private void getPage(String query){


        MainActivity.apiFactory.getService().getPosts(MainActivity.preferenceHelper.getToken(), query,page*10).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Posts>() {
                    @Override
                    public void onCompleted() {
                        Log.d("mytags", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(fragment.v, R.string.snackbar_error, Snackbar.LENGTH_LONG).show();
                        listener.postsLoad();
                    }

                    @Override
                    public void onNext(Posts post) {
                        if(post.getResults().size()!= 0){
                            int size = results.size();
                            results.addAll(post.getResults());
                            Log.d("mytags",""+size+" "+results.size());
                            notifyItemRangeInserted(size, results.size());
                        }
                        else{
                            hasEnded = true;
                        }
                        listener.postsLoad();
                    }
                });
        page++;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }
    protected class PostsViewHolder extends RecyclerView.ViewHolder{
        TextView titleView;
        TextView dateView;
        TextView authorView;
        CardView cardView;
        public PostsViewHolder(View itemView, TextView titleView, TextView dateView, TextView authorView) {
            super(itemView);
            this.titleView = titleView;
            this.dateView = dateView;
            this.authorView = authorView;
            this.cardView = (CardView) itemView.findViewById(R.id.card);
        }

    }
    public PostsFragment getFragment(){
        return fragment;
    }
    public void findPosts(String query){
        page = 0;
        hasEnded = false;
        results = new ArrayList<>();
        this.query =query;
        previousPosition =0;
        getPage(query);
    }
    public void refreshPage(){
        page = 0;
        hasEnded = false;
        previousPosition=0;
        int size = results.size();
        results = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
        query ="";
        getPage(query);
    }
}
