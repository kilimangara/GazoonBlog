package com.example.asus.test_rest_client.huyznaet;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.model.User;


public class PreferenceHelper {
    public static final String KEY_FOR_TOKEN ="token";
    public static final String KEY_FOR_AUTH = "isAuth";
    public static final String KEY_FOR_USER = "user";
    private static PreferenceHelper instance;

    private Context context;
    private SharedPreferences preferences;

    private PreferenceHelper(){

    }

    public static PreferenceHelper getInstance(){
        if(instance == null){
            instance =new PreferenceHelper();
        }

        return instance;
    }

    public void init(Context context){
        this.context = context;
        preferences = this.context.getSharedPreferences("tokenPref", Context.MODE_PRIVATE);
    }

    public void putToken(String token){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_FOR_TOKEN, token);
        editor.apply();
    }
    public String getToken(){
        return preferences.getString(KEY_FOR_TOKEN, "");
    }
    public void putBoolean(boolean bool){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_FOR_AUTH, bool);
        editor.apply();
    }
    public boolean getBoolean(){
        return preferences.getBoolean(KEY_FOR_AUTH, false);
    }
    public void putUser(User user){
        String jsonFormat = MainActivity.gson.toJson(user);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_FOR_USER,jsonFormat );
        editor.apply();

    }
    public User getUser(){
        return MainActivity.gson.fromJson(preferences.getString(KEY_FOR_USER,""), User.class);
    }


}
