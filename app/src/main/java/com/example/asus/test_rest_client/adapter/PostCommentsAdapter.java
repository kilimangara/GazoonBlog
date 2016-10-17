package com.example.asus.test_rest_client.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.PostActivity;
import com.example.asus.test_rest_client.PostCommentsActivity;
import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.RestService;
import com.example.asus.test_rest_client.Utils;
import com.example.asus.test_rest_client.model.Comments;
import com.example.asus.test_rest_client.model.Commentss;
import com.example.asus.test_rest_client.model.OwnComment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Comments> list;
    RestService intf;
    int numberOfCom;
    AddingRePost listener;
    int idOfPost;
    Dialog dialog;
     public interface AddingRePost{
        void RePostAdded(String name,Integer id);
    }


    public PostCommentsAdapter(PostCommentsActivity context, int numb, int idOfPost){
        intf= MainActivity.retrofit.create(RestService.class);
        numberOfCom = numb;
        this.idOfPost = idOfPost;
        list = new ArrayList<>();
        listener = context;
        dialog = new Dialog(context,R.style.Dialog);
        dialog.setContentView(R.layout.dialog_model);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Call<Commentss> call= intf.getPostAllComments( idOfPost, MainActivity.preferenceHelper.getToken(),1000);
        call.enqueue(new Callback<Commentss>() {
            @Override
            public void onResponse(Call<Commentss> call, Response<Commentss> response) {
                list = response.body().getResults();
                Collections.reverse(list);
                dialog.dismiss();
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Commentss> call, Throwable t) {

            }
        });

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_comments, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Comments  comment = list.get(position);
        final CommentsViewHolder commentsViewHolder = (CommentsViewHolder) holder;

        commentsViewHolder.tvDate.setText(Utils.getFullDate(comment.getPublishedAt()));
        commentsViewHolder.tvText.setText(comment.getText());
        commentsViewHolder.tvAuthor.setText(comment.getAuthor().getName());
        if(comment.getTo()!=null){
            commentsViewHolder.img.setVisibility(View.VISIBLE);
            commentsViewHolder.tvRePost.setVisibility(View.VISIBLE);
            commentsViewHolder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.gray));
            for(Comments comments: list){
                if(comments.getId().equals(comment.getTo())){
                    commentsViewHolder.tvRePost.setText(comments.getAuthor().getName());

                }
            }
        }
        if(comment.getAuthor().getId().equals( MainActivity.user.getId())){
            commentsViewHolder.imgDel.setVisibility(View.VISIBLE);
        }
        commentsViewHolder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    list.remove(commentsViewHolder.getAdapterPosition());
                    notifyItemRemoved(commentsViewHolder.getAdapterPosition());
            }
        });
        commentsViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.RePostAdded(commentsViewHolder.tvAuthor.getText().toString(),comment.getId());
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void refresh(){
        dialog.show();
        list = new ArrayList<>();
        Call<Commentss> call= intf.getPostAllComments( idOfPost, MainActivity.preferenceHelper.getToken(),1000);
        call.enqueue(new Callback<Commentss>() {
            @Override
            public void onResponse(Call<Commentss> call, Response<Commentss> response) {
                list = response.body().getResults();
                Collections.reverse(list);
                dialog.dismiss();
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Commentss> call, Throwable t) {

            }
        });
    }

    protected class CommentsViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView imgDel;
        TextView tvDate;
        TextView tvAuthor;
        TextView tvText;
        TextView tvRePost;
        CardView cardView;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            imgDel = (ImageView) itemView.findViewById(R.id.commentDelete);
            img = (ImageView) itemView.findViewById(R.id.commentRePostIcon);
            tvDate = (TextView) itemView.findViewById(R.id.commentDate);
            tvAuthor = (TextView) itemView.findViewById(R.id.commentAuthor);
            tvText = (TextView) itemView.findViewById(R.id.commentText);
            tvRePost = (TextView) itemView.findViewById(R.id.commentRePost);
            cardView = (CardView) itemView.findViewById(R.id.cardComments);
        }
    }
}
