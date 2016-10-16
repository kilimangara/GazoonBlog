package com.example.asus.test_rest_client.adapter;


import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.PostAddActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.RestService;
import com.example.asus.test_rest_client.fragments.NotesFragment;
import com.example.asus.test_rest_client.model.Post;
import com.example.asus.test_rest_client.model.Posts;
import com.example.asus.test_rest_client.model.UserPost;
import com.example.asus.test_rest_client.model.UserPosts;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static public List<UserPost> postsList;
    public NotesFragment fragment;
    private RestService intf;
    private PostsAdapter.OnPostsLoadFinished listener;

    public NotesAdapter(NotesFragment fragment){
        intf = MainActivity.retrofit.create(RestService.class);
        this.fragment = fragment;
        listener = fragment;
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
        final UserPost post = postsList.get(position);
        holder.itemView.setEnabled(true);
        final PostsViewHolder postHolder= (PostsViewHolder) holder;
        View itemView = holder.itemView;
        itemView.setVisibility(View.VISIBLE);
        postHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getActivity(), PostAddActivity.class);
                intent.putExtra("fromNote", true);
                intent.putExtra("title", post.getTitle());
                intent.putExtra("text", post.getText());
                if(post.getLogo()!=null) {
                    intent.putExtra("logo", post.getLogo().getMedium().getUrl());
                }
                intent.putExtra("id", post.getId());
                fragment.getActivity().startActivityForResult(intent,MainActivity.REQUEST_PATCH);
            }
        });
        postHolder.titleView.setText(post.getTitle());
        postHolder.dateView.setText(R.string.post_not_published);
        postHolder.authorView.setText(MainActivity.user.getName());
    }

    @Override
    public int getItemCount() {
        return postsList.size();
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
    public void refreshPage(){
        postsList = new ArrayList<>();
        Log.d("mytags", "trying to refresh notes");
        intf.getUserPosts(MainActivity.user.getId(), MainActivity.preferenceHelper.getToken(), 100).enqueue(new Callback<UserPosts>() {
            @Override
            public void onResponse(Call<UserPosts> call, Response<UserPosts> response) {
                Log.d("mytags","Posts " + response.body().getResults().toString());
                for(UserPost post : response.body().getResults()){
                    if(post.getPublishedAt() == null){
                        postsList.add(post);
                    }
                }
                Log.d("mytags", postsList.toString());
                notifyDataSetChanged();
                listener.postsLoad();
            }

            @Override
            public void onFailure(Call<UserPosts> call, Throwable t) {
                Log.d("mytags", t.getMessage());
                listener.postsLoad();
            }
        });
    }

    public void removeItem(int location){
        if(location >= 0 && location <= getItemCount()-1 ){
            postsList.remove(location);
            notifyItemRemoved(location);
        }
    }
    public void moveNote(int position){


    }
}