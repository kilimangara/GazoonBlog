package com.example.asus.test_rest_client.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.RestService;
import com.example.asus.test_rest_client.Utils;
import com.example.asus.test_rest_client.model.Comments;
import com.example.asus.test_rest_client.model.Commentss;
import com.example.asus.test_rest_client.model.OwnComment;
import com.example.asus.test_rest_client.model.OwnComments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentsAdapter extends BaseAdapter {
    List<OwnComment> list;
    LayoutInflater inflater;
    Context  ctx;
    RestService intf;
    ImageView img;
    TextView tvDate;
    TextView tvAuthor;
    TextView tvText;
    TextView tvRePost;
    CardView cardView;

    public CommentsAdapter(Context context){
        ctx = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list= new ArrayList<>();
        intf = MainActivity.retrofit.create(RestService.class);
        Call<OwnComments> call = intf.getComments(MainActivity.user.getId(),MainActivity.preferenceHelper.getToken());
        call.enqueue(new Callback<OwnComments>() {
            @Override
            public void onResponse(Call<OwnComments> call, Response<OwnComments> response) {
                if(response.body().getResults()!=null) {
                    list = response.body().getResults();
                    Log.d("mytags", "moi commenti "+list.toString());
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<OwnComments> call, Throwable t) {
                Log.d("mytags", t.getMessage());
            }
        });
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.model_comments, parent, false);
        }
        OwnComment comments = (OwnComment) getItem(position);
        img = (ImageView) view.findViewById(R.id.commentRePostIcon);
        tvAuthor = (TextView) view.findViewById(R.id.commentAuthor);
        tvText = (TextView) view.findViewById(R.id.commentText);
        tvAuthor.setText(MainActivity.user.getName());
        tvText.setText(comments.getText());


        return  view;
    }
}
