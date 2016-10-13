package com.example.asus.test_rest_client.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.asus.test_rest_client.fragments.NotesFragment;
import com.example.asus.test_rest_client.fragments.PostsFragment;
import com.example.asus.test_rest_client.fragments.UserFragment;

/**
 * Created by Asus on 12.09.2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public static final int POSTS_FRAGMENT = 0;
    public static final int USERS_FRAGMENT = 2;
    public static final int NOTES_FRAGMENT = 1;

    private int numbOfFrag;
    private PostsFragment postsFragment;
    private UserFragment userFragment;
    private NotesFragment notesFragment;

    public ViewPagerAdapter(FragmentManager fm, int num) {
        super(fm);
        this.numbOfFrag =num;
        postsFragment = new PostsFragment();
        userFragment = new UserFragment();
        notesFragment = new NotesFragment();

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case POSTS_FRAGMENT:
                return postsFragment;
            case USERS_FRAGMENT:
                return userFragment;
            case NOTES_FRAGMENT:
                return notesFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numbOfFrag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
