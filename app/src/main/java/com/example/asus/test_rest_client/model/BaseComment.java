package com.example.asus.test_rest_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Asus on 01.10.2016.
 */
public class BaseComment {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("published_at")
    @Expose
    private Double publishedAt;
    @SerializedName("to")
    @Expose
    private Integer to;
    @SerializedName("post")
    @Expose
    private Integer post;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return The publishedAt
     */
    public Double getPublishedAt() {
        return publishedAt;
    }

    /**
     * @param publishedAt The published_at
     */
    public void setPublishedAt(Double publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * @return The to
     */
    public Integer getTo() {
        return to;
    }

    /**
     * @param to The to
     */
    public void setTo(Integer to) {
        this.to = to;
    }

    /**
     * @return The post
     */
    public Integer getPost() {
        return post;
    }

    /**
     * @param post The post
     */
    public void setPost(Integer post) {
        this.post = post;
    }

}
