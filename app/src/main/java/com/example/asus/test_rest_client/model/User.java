
package com.example.asus.test_rest_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("created_at")
    @Expose
    private Double createdAt;
    @SerializedName("last_login")
    @Expose
    private Object lastLogin;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("about_me")
    @Expose
    private String aboutMe;
    @SerializedName("avatar")
    @Expose
    private Avatar avatar;

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

    /**
     * 
     * @return
     *     The createdAt
     */
    public Double getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(Double createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The lastLogin
     */
    public Object getLastLogin() {
        return lastLogin;
    }

    /**
     * 
     * @param lastLogin
     *     The last_login
     */
    public void setLastLogin(Object lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * 
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The aboutMe
     */
    public String getAboutMe() {
        return aboutMe;
    }

    /**
     * 
     * @param aboutMe
     *     The about_me
     */
    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    /**
     * 
     * @return
     *     The avatar
     */
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * 
     * @param avatar
     *     The avatar
     */
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

}
