package com.example.asus.test_rest_client.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.Utils;
import com.example.asus.test_rest_client.model.User;
import com.example.asus.test_rest_client.model.UserPost;
import com.example.asus.test_rest_client.model.UserPosts;
import com.example.asus.test_rest_client.retrofitRxSingleTon.ApiFactory;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserPost> results;
    private User idOfUser;

    public UserPostsAdapter(Context context, User idOfUser){
        this.idOfUser = idOfUser;
        refreshPage();
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
        final UserPost post = results.get(position);
        holder.itemView.setEnabled(true);
        final PostsViewHolder postHolder= (PostsViewHolder) holder;
        View itemView = holder.itemView;
        /*if(position == getItemCount() -1 && !hasEnded){
            getPage();
        }*/
        itemView.setVisibility(View.VISIBLE);
        postHolder.titleView.setText(post.getTitle());
        if(post.getPublishedAt() == null){
            postHolder.dateView.setText("Not published");
        }
        else {
            postHolder.dateView.setText(Utils.getFullDate(post.getPublishedAt()));
        }
        postHolder.authorView.setText(idOfUser.getName());
       /* postHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
                intf.deleteOPost(results.get(postHolder.getLayoutPosition()).getId(), PreferenceHelper.getInstance().getToken()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("mytags", ""+response.code());
                        if(response.code() == 204) {
                            results.remove(postHolder.getLayoutPosition());
                            notifyItemRemoved(postHolder.getLayoutPosition());
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
            }
        });*/
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
    private void getPage(){
        /*Call<UserPosts> callPosts = intf.getUserPosts(MainActivity.user.getId(),MainActivity.preferenceHelper.getToken(), 50);
        callPosts.enqueue(new Callback<UserPosts>() {
            @Override
            public void onResponse(Call<UserPosts> call, Response<UserPosts> response) {
                if(response.body().getCount() != 0) {
                    Log.d("mytags", " "+page +" "+ getItemCount());
                    results.addAll(response.body().getResults());
                    notifyDataSetChanged();
                }
                else{
                    hasEnded = true;
                }
            }

            @Override
            public void onFailure(Call<UserPosts> call, Throwable t) {

            }
        });*/

        ApiFactory.getInstance().getService().getUserPosts(idOfUser.getId(), MainActivity.preferenceHelper.getToken(),50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UserPosts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserPosts userPosts) {
                            if(userPosts.getCount() != null){
                                results.addAll(userPosts.getResults());
                                notifyDataSetChanged();
                            }
                    }
                });
        //page++;
    }
    private void refreshPage(){
        results = new ArrayList<>();
        getPage();
    }
}
