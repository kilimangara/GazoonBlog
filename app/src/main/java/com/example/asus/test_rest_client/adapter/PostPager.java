package com.example.asus.test_rest_client.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.asus.test_rest_client.MainActivity;
import com.example.asus.test_rest_client.fragments.ClosePostsFragment;
import com.example.asus.test_rest_client.model.Post;

import java.util.ArrayList;
import java.util.List;


public class PostPager extends FragmentStatePagerAdapter {

    private List<ClosePostsFragment> fragments;
    private List<Post> posts;
    public PostPager(FragmentManager fm, List<Post> posts)  {
        super(fm);
        this.posts = posts;
        refreshAdapter();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public ClosePostsFragment getFragment(int position){
        return (ClosePostsFragment) getItem(position);
    }
    public void refreshAdapter(){
        fragments = new ArrayList<>();
        for(Post post : posts){
            ClosePostsFragment newFragment = new ClosePostsFragment();
            Bundle arg = new Bundle();
            arg.putString("Post", MainActivity.gson.toJson(post));
            newFragment.setArguments(arg);
            fragments.add(newFragment);
        }
    }


    @Override
    public int getCount() {
        return posts.size();
    }
}
