package com.example.asus.test_rest_client;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.test_rest_client.adapter.ViewPagerAdapter;
import com.example.asus.test_rest_client.fragments.NotesFragment;
import com.example.asus.test_rest_client.fragments.PostsFragment;
import com.example.asus.test_rest_client.fragments.UserFragment;
import com.example.asus.test_rest_client.huyznaet.PreferenceHelper;
import com.example.asus.test_rest_client.loaderManager.RequestLoader;
import com.example.asus.test_rest_client.model.User;
import com.example.asus.test_rest_client.retrofitRxSingleTon.ApiFactory;
import com.example.asus.test_rest_client.serializers.UserSettings;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SplashFragmentLogin.OnLoadFinishListener {
    public static final String BASE_URL= "http://gazon.pythonanywhere.com/api/";
    public static final int REQUEST_ADD = 0;
    public static final int REQUEST_PATCH = 1;
    public static final int RESULT_ADDED= 201;
    public static final int RESULT_PATCHED= 202;
   // public static final int RESULT_ADDED_FRON_NOTES=203;
    public static PreferenceHelper preferenceHelper;
    public static User user;
    public static ImageLoader imageLoader;
    public static File cacheDir;
    public static Gson gson;
    public static Retrofit retrofit;
    public static ApiFactory apiFactory;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public DisplayImageOptions options;

    public TabLayout tabLayout;
    public PostsFragment postsFragment;
    public NotesFragment notesFragment;
    public UserFragment userFragment;
    public ViewPager viewPager;
    public TextView tvEmail;
    public TextView tvName;
    public SearchView searchView;
    public FragmentManager fragmentManager;
    public CircleImageView circleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");
        Log.d("mytags", "OnCreateMain");
        user = null;
        gson = new GsonBuilder().setPrettyPrinting().create();
        apiFactory = ApiFactory.getInstance();
        apiFactory.init();
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        fragmentManager = getSupportFragmentManager();
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
        cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        options =new DisplayImageOptions.Builder().cacheOnDisk(true).showImageOnFail(R.drawable.avatar)
                .showImageForEmptyUri(R.drawable.avatar)
                .showImageOnLoading(R.drawable.avatar)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration configuration = new  ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(configuration);
        imageLoader = ImageLoader.getInstance();
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostAddActivity.class);
                intent.putExtra("fromNote", false);
                startActivityForResult(intent,REQUEST_ADD);
                overridePendingTransition(R.anim.activity_down_up_enter, R.anim.activity_down_up_close_enter);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        searchView = (SearchView) findViewById(R.id.searchView);
        circleImageView  = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        tvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvForEmail);
        tvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvForName);
        Log.d("mytags", " " +preferenceHelper.getBoolean());
        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_forum_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_note_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_group_white_24dp));
        if(preferenceHelper.getBoolean()){
            Log.d("mytags", "already signed in");
            user = preferenceHelper.getUser();
            setUI();
            resetUser();
        }
        else {
            Log.d("mytags", "going to splash");
            runSplash();

        }


    }
    private void setUI(){
        Log.d("mytags", "UI Setted");
        final ViewPagerAdapter adapter =new ViewPagerAdapter(fragmentManager, 3);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (viewPager.getCurrentItem()){
                    case ViewPagerAdapter.POSTS_FRAGMENT:
                        getSupportActionBar().setTitle("Posts");
                        break;
                    case ViewPagerAdapter.NOTES_FRAGMENT:
                        getSupportActionBar().setTitle("Notes");
                        break;
                    case ViewPagerAdapter.USERS_FRAGMENT:
                        getSupportActionBar().setTitle("Users");
                        break;
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        postsFragment= (PostsFragment) adapter.getItem(ViewPagerAdapter.POSTS_FRAGMENT);
        notesFragment = (NotesFragment) adapter.getItem(ViewPagerAdapter.NOTES_FRAGMENT);
        userFragment = (UserFragment) adapter.getItem(ViewPagerAdapter.USERS_FRAGMENT);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(searchView.getQuery().length() == 0){
                    switch (viewPager.getCurrentItem()){
                        case ViewPagerAdapter.POSTS_FRAGMENT:
                            postsFragment.adapter.refreshPage();
                            break;
                        case ViewPagerAdapter.NOTES_FRAGMENT:
                            Toast.makeText(MainActivity.this, "Хуй тебе Газон, а не поиск здесь", Toast.LENGTH_LONG).show();
                            break;
                        case ViewPagerAdapter.USERS_FRAGMENT:
                            userFragment.adapter.refreshPage();
                            break;
                    }
                }
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() != 0){
                    switch (viewPager.getCurrentItem()){
                        case ViewPagerAdapter.POSTS_FRAGMENT:
                            postsFragment.adapter.findPosts(query);
                            break;
                        case ViewPagerAdapter.NOTES_FRAGMENT:
                            Toast.makeText(MainActivity.this, "Хуй тебе Газон, а не поиск здесь", Toast.LENGTH_LONG).show();
                            break;
                        case ViewPagerAdapter.USERS_FRAGMENT:
                            userFragment.adapter.findUsers(query);
                            break;
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(PERMISSIONS_STORAGE, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ADD:
                if(resultCode == RESULT_ADDED){
                    postsFragment.adapter.refreshPage();
                }
                if(resultCode == RESULT_PATCHED){
                    notesFragment.adapter.refreshPage();
                }
                break;
            case REQUEST_PATCH:
                if(resultCode == RESULT_ADDED){
                    postsFragment.adapter.refreshPage();
                    notesFragment.adapter.refreshPage();
                }
                if(resultCode == RESULT_PATCHED){
                    notesFragment.adapter.refreshPage();
                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("mytags", "OnStartMain");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("mytags", "reseting user");
        resetUser();

        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceHelper.getInstance().putUser(MainActivity.user);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.f
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        }else if (id == R.id.nav_user) {
            Intent intent = new Intent(MainActivity.this, UserSettings.class);
            intent.putExtra("User", gson.toJson(MainActivity.user));
            startActivity(intent);
            overridePendingTransition(R.anim.activity_down_up_enter, R.anim.activity_down_up_close_enter);

        } else if (id == R.id.nav_exit) {
            preferenceHelper.putToken("");
            preferenceHelper.putBoolean(false);
            runSplash();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void runSplash(){
        SplashFragmentLogin login = new SplashFragmentLogin();
        fragmentManager.beginTransaction().replace(R.id.drawer_layout, login).addToBackStack(null)
                .commit();
    }

    public void resetUser(){


        if(user.getEmail()!= null){
            tvEmail.setText(user.getEmail());
        }
        if(user.getName() !=null){
            tvName.setText(user.getName());
        }
        if(user.getAvatar()!= null) {
            imageLoader.displayImage(user.getAvatar().getMedium().getUrl(), circleImageView);
        }
        else{
            circleImageView.setImageDrawable(getDrawable(R.drawable.avatar));
        }
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void loadFinished() {
        Log.d("mytags", "signed");
            resetUser();
        setUI();
                new Handler().post(new Runnable() {
                @Override
                public void run() {
                    fragmentManager.popBackStack();
                    hideKeyboard();
                }
            });
        

    }
}
