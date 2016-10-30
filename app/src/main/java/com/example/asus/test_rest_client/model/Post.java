package com.example.asus.test_rest_client.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.asus.test_rest_client.MainActivity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Post extends BasePost implements Parcelable{


    @SerializedName("author")
    @Expose
    private User author;

    protected Post(Parcel in) {
        Bundle res= in.readBundle();
        setAuthor(MainActivity.gson.fromJson(res.getString("author"),User.class));
        setId(res.getInt("id"));
        setLogo(MainActivity.gson.fromJson(res.getString("logo"),Avatar.class));
        setText(res.getString("text"));
        setTitle(res.getString("title"));
        setPublishedAt(res.getDouble("published"));
    }
    public Post(UserPost post, User user){
        setAuthor(user);
        setPublishedAt(post.getPublishedAt());
        setText(post.getText());
        setTitle(post.getTitle());
        setLogo(post.getLogo());
        setId(post.getId());
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    /**
     *
     * @return
     * The author
     */
    public User getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle obj= new Bundle();
        obj.putInt("id", getId());
        obj.putString("title", getTitle());
        obj.putString("text", getText());
        obj.putString("author", MainActivity.gson.toJson(getAuthor()));
        obj.putString("logo",MainActivity.gson.toJson(getLogo()));
        obj.putDouble("published",getPublishedAt());
        parcel.writeBundle(obj);
    }
}
