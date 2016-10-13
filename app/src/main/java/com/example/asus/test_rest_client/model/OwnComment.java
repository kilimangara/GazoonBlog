package com.example.asus.test_rest_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Asus on 27.09.2016.
 */
public class OwnComment extends BaseComment {
    @SerializedName("author")
    @Expose
    private Integer author;

    /**
     * @return The author
     */
    public Integer getAuthor() {
        return author;
    }

    /**
     * @param author The author
     */
    public void setAuthor(Integer author) {
        this.author = author;
    }


}
