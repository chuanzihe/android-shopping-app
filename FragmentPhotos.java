package com.example.hw8;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPhotos extends Fragment {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private JSONArray mPhotos;

    public FragmentPhotos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // first line
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        mRecyclerView = view.findViewById(R.id.photos_recyclerView);
        mRecyclerView.setHasFixedSize(true); // size invariant to item numbers; performance

        return view;
    }

    public void getInfo(JSONArray photos){
        mPhotos = photos;
        mLayoutManager = new GridLayoutManager(mContext, 1);
        mAdapter = new PhotoAdapter(mPhotos, mContext);
        mAdapter.notifyDataSetChanged();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

}
