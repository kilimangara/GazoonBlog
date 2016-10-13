package com.example.asus.test_rest_client.adapter;


import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.RestService;
import com.example.asus.test_rest_client.fragments.PostsFragment;
import com.example.asus.test_rest_client.fragments.UserFragment;
import com.example.asus.test_rest_client.model.User;
import com.example.asus.test_rest_client.model.Users;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<User> users ;
    RestService intf;
    int page;
    boolean hasEnded;
    UserFragment fragment;
    public UserAdapter(UserFragment userFragment){
        users = new ArrayList<>();
        intf = MainActivity.retrofit.create(RestService.class);
        page=0;
        hasEnded = false;
        fragment = userFragment;
        getPage();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_users, parent, false);
        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.user_avatar);
        TextView textView = (TextView) v.findViewById(R.id.user_name_);
        return new UserViewHolder(v, circleImageView, textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.itemView.setEnabled(true);
        final UserViewHolder viewHolder = (UserViewHolder) holder;
        if(position == getItemCount()-1 && !hasEnded){
            getPage();
        }
        viewHolder.textView.setText(user.getName());
        if(user.getAvatar()!=null){
            MainActivity.imageLoader.displayImage(user.getAvatar().getMedium().getUrl(), viewHolder.circleImageView);
        }
        else{
            viewHolder.circleImageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
        }


    }
    public void getPage(){
        Call<Users> usersCall = intf.getUsers(MainActivity.preferenceHelper.getToken(), page*10, 10);
        usersCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.body().getResults() != null){
                    users.addAll(response.body().getResults());
                    notifyDataSetChanged();
                }
                else{
                    hasEnded = true;
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Snackbar.make(fragment.v, R.string.snackbar_error, Snackbar.LENGTH_LONG).show();
            }
        });
        page++;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    protected class UserViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView textView;

        public UserViewHolder(View itemView, CircleImageView civ, TextView tv) {
            super(itemView);
            this.circleImageView = civ;
            this.textView =  tv;

        }
    }
}
