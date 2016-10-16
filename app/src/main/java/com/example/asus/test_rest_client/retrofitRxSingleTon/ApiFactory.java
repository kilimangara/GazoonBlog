package com.example.asus.test_rest_client.retrofitRxSingleTon;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.RestService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



public class ApiFactory {
    private static ApiFactory instance;
    private RestService intf;


    private ApiFactory(){

    }

    public static ApiFactory getInstance(){
        if(instance == null){
            instance = new ApiFactory();
        }
        return instance;
    }
    public void init(){
         Retrofit retrofit = new Retrofit.Builder().baseUrl(MainActivity.BASE_URL).addConverterFactory(GsonConverterFactory.create(MainActivity.gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        intf = retrofit.create(RestService.class);
    }
    public RestService getService(){
        return intf;
    }
}
