
package com.example.asus.test_rest_client.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Avatar {

    @SerializedName("medium")
    @Expose
    private Original medium;
    @SerializedName("original")
    @Expose
    private Original original;
    @SerializedName("small")
    @Expose
    private Original small;
    @SerializedName("id")
    @Expose
    private Integer id;

    /**
     * 
     * @return
     *     The medium
     */
    public Original getMedium() {
        return medium;
    }

    /**
     * 
     * @param medium
     *     The medium
     */
    public void setMedium(Original medium) {
        this.medium = medium;
    }

    /**
     * 
     * @return
     *     The original
     */
    public Original getOriginal() {
        return original;
    }

    /**
     * 
     * @param original
     *     The original
     */
    public void setOriginal(Original original) {
        this.original = original;
    }

    /**
     * 
     * @return
     *     The small
     */
    public Original getSmall() {
        return small;
    }

    /**
     * 
     * @param small
     *     The small
     */
    public void setSmall(Original small) {
        this.small = small;
    }

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

}
