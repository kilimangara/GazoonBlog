package com.example.asus.test_rest_client.adapter;


import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.fragments.UserFragment;
import com.example.asus.test_rest_client.huyznaet.PreferenceHelper;
import com.example.asus.test_rest_client.model.OwnComments;
import com.example.asus.test_rest_client.model.User;
import com.example.asus.test_rest_client.model.UserPosts;
import com.example.asus.test_rest_client.model.Users;
import com.example.asus.test_rest_client.retrofitRxSingleTon.ApiFactory;
import com.example.asus.test_rest_client.serializers.UserSettings;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> users ;
    private int page;
    private boolean hasEnded;
    private UserFragment fragment;
    private String query;
    private OnUserRefreshDoneListener listener;

    public interface OnUserRefreshDoneListener{
        void usersLoad();
    }

    public UserAdapter(UserFragment userFragment){
        fragment = userFragment;
        listener=  fragment;
        page=0;
        hasEnded = false;
        users = new ArrayList<>();
        query="";
        getPage(query);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_users, parent, false);
        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.user_avatar);
        TextView textView = (TextView) v.findViewById(R.id.user_name_);
        TextView numOfPosts = (TextView) v.findViewById(R.id.posts_number);
        TextView numOfComments = (TextView) v.findViewById(R.id.comments_number);
        CardView cardView = (CardView) v.findViewById(R.id.card2);
        return new UserViewHolder(v, circleImageView, textView, numOfComments, numOfPosts, cardView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.itemView.setEnabled(true);
        final UserViewHolder viewHolder = (UserViewHolder) holder;
        if(position == getItemCount()-1 && !hasEnded){
            getPage(query);
        }
        if(user.getPostsNumber()!=null){
            viewHolder.numOfPosts.setText(String.valueOf(user.getPostsNumber()));
        }
        if(user.getCommentsNumber()!=null){
            viewHolder.numOfComments.setText(String.valueOf(user.getCommentsNumber()));
        }
        if(user.getName().length() == 0){
            viewHolder.textView.setText("Noname pidr");
        }
        else {
            viewHolder.textView.setText(user.getName());
        }
        if(user.getAvatar()!=null){
            MainActivity.imageLoader.displayImage(user.getAvatar().getMedium().getUrl(), viewHolder.circleImageView);
        }
        else{
            viewHolder.circleImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.avatar));
        }
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(fragment.getContext(), UserSettings.class);
                intent.putExtra("User", MainActivity.gson.toJson(user));
                fragment.startActivity(intent);
            }
        });


    }
    public void refreshPage(){
        page=0;
        hasEnded = false;
        int size = users.size();
        users = new ArrayList<>();
        notifyItemRangeRemoved(0, size);
        query="";
        getPage(query);
    }
    private void getPage(String query){




            MainActivity.apiFactory.getService().getUsers(MainActivity.preferenceHelper.getToken(), query,page*10, 10).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Users, Observable<User>>() {
                    @Override
                    public Observable<User> call(Users users) {
                        if(users.getResults()== null){
                            hasEnded = true;
                        }
                        return Observable.from(users.getResults());
                    }
                }).subscribe(new Subscriber<User>() {
                @Override
                public void onCompleted() {
                    listener.usersLoad();
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("mytags", e.getMessage());
                    hasEnded=true;
                    listener.usersLoad();
                }

                @Override
                public void onNext(User user) {
                    final User user1= user;
                    Observable.zip(
                            ApiFactory.getInstance().getService().getUserPosts(user.getId(), PreferenceHelper.getInstance().getToken(), 1)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()),
                            ApiFactory.getInstance().getService().getComments(user.getId(), PreferenceHelper.getInstance().getToken()).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()),
                            new Func2<UserPosts, OwnComments, List<Integer>>() {
                                @Override
                                public List<Integer> call(UserPosts userPosts, OwnComments ownComments) {
                                    List<Integer> list = new ArrayList<>();
                                    list.add(userPosts.getCount());
                                    list.add(ownComments.getCount());
                                    return list;
                                }
                            }).doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            listener.usersLoad();
                            Snackbar.make(fragment.v,"Connection troubles", Snackbar.LENGTH_LONG);
                        }
                    }).subscribe(new Action1<List<Integer>>() {
                        @Override
                        public void call(List<Integer> integers) {
                            user1.setPostsNumber(integers.get(0));
                            user1.setCommentsNumber(integers.get(1));
                            users.add(user1);
                            notifyItemInserted(users.size());
                        }
                    });
                }
            });

        page++;
    }
    public void findUsers(String query){
        users = new ArrayList<>();
        page=0;
        hasEnded = false;
        this.query=query;
        getPage(query);

    }
    @Override
    public int getItemCount() {
        return users.size();
    }
    protected class UserViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView textView;
        TextView numOfComments;
        TextView numOfPosts;
        CardView cardView;

        public UserViewHolder(View itemView, CircleImageView civ, TextView tv, TextView numOfComments, TextView numOfPosts, CardView cardView) {
            super(itemView);
            this.circleImageView = civ;
            this.textView =  tv;
            this.numOfComments = numOfComments;
            this.numOfPosts = numOfPosts;
            this.cardView = cardView;
        }
    }
}
