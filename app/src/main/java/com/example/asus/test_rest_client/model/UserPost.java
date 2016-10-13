package com.example.asus.test_rest_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Asus on 01.10.2016.
 */
public class UserPost extends BasePost{
    @SerializedName("author")
    @Expose
    private Integer author;

    /**
     *
     * @return
     * The author
     */
    public Integer getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(Integer author) {
        this.author = author;
    }
}
