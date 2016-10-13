package com.example.asus.test_rest_client.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.test_rest_client.R;
import com.example.asus.test_rest_client.adapter.UserAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    public View v ;
    public UserAdapter adapter;
    public boolean once;
    public UserFragment() {
        // Required empty public constructor
        once = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user, container, false);
        if(!once) {
            adapter = new UserAdapter(this);
            once = true;
        }
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rvForUsers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return v;
    }

}
