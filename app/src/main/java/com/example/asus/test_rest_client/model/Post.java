package com.example.asus.test_rest_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Post extends BasePost{


    @SerializedName("author")
    @Expose
    private User author;

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

}
