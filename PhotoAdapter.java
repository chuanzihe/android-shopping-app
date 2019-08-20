package com.example.hw8;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    
    private JSONArray mItemsList;
    private static Context mContext;

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public JSONObject mItemInfo;


        public PhotoViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.photos_cardimg);
        }
        
        public void getItemInfo(JSONObject itemInfo){
            mItemInfo = itemInfo;
        }
        
    }


    public PhotoAdapter(JSONArray itemsList, Context context) {
        mItemsList = itemsList;
        mContext   = context;
    }


    //    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // viewholder layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_photos, parent, false);
        PhotoViewHolder ivh = new PhotoViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        try {
            JSONObject currentItem = mItemsList.getJSONObject(position);
            String img_url = currentItem.getString("link");
            Picasso.with(mContext).load(img_url).fit().centerInside().into(holder.mImageView);
            holder.getItemInfo(currentItem); // get set view info better?
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mItemsList.length();
    }
}
