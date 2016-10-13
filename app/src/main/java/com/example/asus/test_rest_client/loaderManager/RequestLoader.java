package com.example.asus.test_rest_client.loaderManager;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.RestService;
import com.example.asus.test_rest_client.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class RequestLoader extends AsyncTaskLoader<User> {
    public static final String MYTAGS="mytags";
    private RestService intf ;
    private Map<String, Object> mapJson;
    private boolean registration;

    public RequestLoader(Context context, Bundle args) {
        super(context);
        intf = MainActivity.retrofit.create(RestService.class);
        mapJson = new HashMap<>();
        registration = args.getBoolean("register");
        Log.d(MYTAGS, ""+registration);
        if(registration) {
            mapJson.put("name", args.getString("name"));
        }
        mapJson.put("email", args.getString("email"));
        mapJson.put("password", args.getString("password"));


    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.d(MYTAGS, "OnForceLoad");
    }

    @Override
    public User loadInBackground() {
        Log.d(MYTAGS, "loadinbackground");
        User thisUser = new User();
        if(registration){
            Call<User> registerCall = intf.register(mapJson);
            try {
                registerCall.execute();
                mapJson.remove("name");
                thisUser = logIn();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            thisUser = logIn();
        }
        return thisUser;
    }

    public User logIn(){
        Call<Object> call = intf.auth(mapJson);
        Map<String, Object> tokenMap;
        User thisUser = null;
        try {
            Response<Object>  response = call.execute();
            tokenMap = MainActivity.gson.fromJson(response.body().toString(), Map.class);
            String token = (String) tokenMap.get("token");
            Call<User> callus = intf.getOwnInfo((int)(double)tokenMap.get("user"), "Token " + token);
            Response<User> response1 = callus.execute();
            thisUser = response1.body();
            if(!token.equals("")) {
                MainActivity.preferenceHelper.putToken("Token ".concat((String)tokenMap.get("token")));
                MainActivity.preferenceHelper.putBoolean(true);
                MainActivity.preferenceHelper.putUser(thisUser);
            }

        } catch (IOException|NullPointerException e) {

            e.printStackTrace();

        }

        return thisUser;
    }
}
