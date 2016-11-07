package com.example.asus.test_rest_client;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.asus.test_rest_client.loaderManager.RequestLoader;
import com.example.asus.test_rest_client.model.User;

;


public class SplashFragmentLogin extends Fragment implements LoaderManager.LoaderCallbacks<User> {
    AutoCompleteTextView tvEmail;
    EditText edPass;
    Button sBut;
    EditText edName;
    CheckedTextView register;
    OnLoadFinishListener listener;
    Animation anim;
    Bundle bnd1 ;
    private TextView tvError;
    private boolean emailHasChars;
    private boolean passHasChars;
    private boolean forRegister;
    public static final int LOADER_REQUEST_ID = 1;

    public SplashFragmentLogin() {
        // Required empty public constructor
    }

    public interface OnLoadFinishListener{
        void loadFinished();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (OnLoadFinishListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException("MainActivity must implement OnLoadFinishListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_splash_fragment_login, container, false);
        Log.d("mytags", "OnCreateSplash");
        tvEmail = (AutoCompleteTextView) v.findViewById(R.id.email);
        edPass = (EditText) v.findViewById(R.id.password);
        sBut = (Button) v.findViewById(R.id.email_sign_in_button);
        edName = (EditText) v.findViewById(R.id.name);
        register = (CheckedTextView) v.findViewById(R.id.register);
        tvError = (TextView) v.findViewById(R.id.error);
        forRegister = false;
        anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        bnd1 = new Bundle();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(forRegister){
                    register.setText("Register");
                    forRegister = !forRegister;
                    edName.setVisibility(View.GONE);
                }
                else{
                    register.setText("Sign In");
                    forRegister = !forRegister;
                    edName.setVisibility(View.VISIBLE);
                }
            }
        });
        sBut.setEnabled(false);

        if(MainActivity.preferenceHelper.getBoolean()){
            return null;
        }
        tvEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    emailHasChars =true;
                }
                sBut.setEnabled(emailHasChars&passHasChars);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() !=0){
                    passHasChars = true;
                }
                sBut.setEnabled(emailHasChars&passHasChars);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forRegister){
                    if (edName.length() != 0){
                        bnd1.putBoolean("register", true);
                        bnd1.putString("name", edName.getText().toString());
                        bnd1.putString("email", tvEmail.getText().toString());
                        bnd1.putString("password", edPass.getText().toString());
                        getLoaderManager().initLoader(LOADER_REQUEST_ID, bnd1, SplashFragmentLogin.this);
                        getRequestClick(v);
                    }
                    else{
                        edName.setError("Type your name");
                        edName.startAnimation(anim);
                    }
                }else {
                    bnd1.putBoolean("register", false);
                    bnd1.putString("email", tvEmail.getText().toString());
                    bnd1.putString("password", edPass.getText().toString());
                    getLoaderManager().initLoader(LOADER_REQUEST_ID, bnd1, SplashFragmentLogin.this);
                    getRequestClick(v);
                }
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("mytags", "OnStartSplash");
    }

    public void getRequestClick(View v){
        Loader<User> loader;

        getLoaderManager().destroyLoader(LOADER_REQUEST_ID);
        loader = getLoaderManager().initLoader(LOADER_REQUEST_ID, bnd1,SplashFragmentLogin.this);
        loader.forceLoad();
    }

    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        Log.d(RequestLoader.MYTAGS, "OnCreateLoader");
        Loader<User> loader = null;
        if(id == LOADER_REQUEST_ID){
            loader = new RequestLoader(getContext(), args);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User data) {
        Log.d("mytags","LOADFINISHED!! "+loader.getId());

        if(data !=null) {
            MainActivity.user = data;
            listener.loadFinished();
            Log.d("mytags","norm "+loader.getId());
        }
        else{
            Log.d("mytags","ne norm "+loader.getId());
           tvError.setVisibility(View.VISIBLE);
        }


    }
    private void validation(){

    }

    @Override
    public void onLoaderReset(Loader<User> loader) {
        Log.d(RequestLoader.MYTAGS, "OnLoadFinished");
    }

}
