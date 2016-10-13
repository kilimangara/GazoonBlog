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



public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static public List<Post> results;
    Posts posts;
    PostsFragment fragment;
    RestService intf ;
    private int page;
    private boolean hasEnded;
    private OnPostsLoadFinished listener;

    public interface OnPostsLoadFinished{
        void postsLoad();
    }

    public PostsAdapter(PostsFragment postsFragment){
        fragment = postsFragment;
        listener = fragment;
        intf = MainActivity.retrofit.create(RestService.class);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Post post = results.get(position);
        holder.itemView.setEnabled(true);
        final PostsViewHolder postHolder= (PostsViewHolder) holder;
        View itemView = holder.itemView;
        if(position == getItemCount() -1 && !hasEnded){
            getPage();
        }
        itemView.setVisibility(View.VISIBLE);
        postHolder.titleView.setText(post.getTitle());
        postHolder.dateView.setText(Utils.getFullDate(post.getPublishedAt()));
        postHolder.authorView.setText(post.getAuthor().getName());
        postHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getContext(), PostActivity.class);
                intent.putExtra("ID", postHolder.getAdapterPosition());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragment.getActivity(),
                        new Pair<View, String>(postHolder.dateView, fragment.getContext().getString(R.string.transition_name_date)),
                        new Pair<View, String>(postHolder.authorView, fragment.getContext().getString(R.string.transition_name_author)),
                        new Pair<View, String>(postHolder.titleView, fragment.getContext().getString(R.string.transition_name_title))
                        );
                ActivityCompat.startActivity(fragment.getActivity(), intent, options.toBundle());

            }
        });



    }
    public void getPage(){
        Call<Posts> callPosts = intf.getPosts(MainActivity.preferenceHelper.getToken(), page*10);
        Log.d("mytags",callPosts.request().toString());
        callPosts.enqueue(new Callback<Posts>() {
            @Override
            public void onResponse(Call<Posts> call, Response<Posts> response) {
                posts = response.body();
                if(posts.getResults().size() != 0) {
                    results.addAll(posts.getResults());
                    notifyDataSetChanged();
                }
                else{
                    hasEnded = true;
                }
                listener.postsLoad();
                Log.d("mytags", "result posts "+ posts.getResults().size());
            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t) {
                Snackbar.make(fragment.v, R.string.snackbar_error, Snackbar.LENGTH_LONG).show();
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
    public void refreshPage(){
        page = 0;
        hasEnded = false;
        results = new ArrayList<>();
        getPage();
    }
}
