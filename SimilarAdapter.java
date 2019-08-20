package com.example.hw8;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder> {

    private ArrayList<JSONObject> mItemsList;
    private static Context mContext;

    public static class SimilarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mImageView;
        public JSONObject mItemInfo;
        public TextView mTextTitle;
        public TextView mTextShipping;
        public TextView mTextDays;
        public TextView mTextPrice;
        public String mviewItemURL;

        public SimilarViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.simi_img);
            mTextTitle = itemView.findViewById(R.id.simi_title);
            mTextShipping = itemView.findViewById(R.id.simi_shipping);
            mTextDays = itemView.findViewById(R.id.simi_days);
            mTextPrice = itemView.findViewById(R.id.simi_price);

            mImageView.setClickable(true);
            mImageView.setOnClickListener(this);
        }

        public void getItemUrl(String viewItemURL){
            mviewItemURL = viewItemURL;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.simi_img:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mviewItemURL));
                    mContext.startActivity(browserIntent);
                    break;

                default:
                    break;
            }

        }
    }

    public SimilarAdapter(ArrayList<JSONObject> itemsList, Context context) {
        mItemsList = itemsList;
        mContext   = context;
    }

    @Override
    public SimilarViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // viewholder layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_similar, parent, false);
        SimilarViewHolder ivh = new SimilarViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(SimilarViewHolder holder, int position) {
        try {
            JSONObject currentItem = mItemsList.get(position);

            if(currentItem.has("imageURL")){
                String img_url = currentItem.getString("imageURL");
                Picasso.with(mContext).load(img_url).fit().centerInside().into(holder.mImageView);
            }

            String title = "Title: N/A";
            if (currentItem.has("title")){
                title = currentItem.getString("title");
            }
            holder.mTextTitle.setText(title);

            String shipping = "Shipping: N/A";
            if (currentItem.has("shippingCost")){
                String value = currentItem.getJSONObject("shippingCost").getString("__value__");
                if(value.equals("0.00")){
                    shipping = "Free Shipping";
                }
                else {
                    shipping = "$" + value;
                }
            }
            holder.mTextShipping.setText(shipping);

            String days = "Days left:N/A";
            if (currentItem.has("timeLeft")){
                String value = currentItem.getString("timeLeft");
                int pos_P = value.indexOf("P");
                int pos_D = value.indexOf("D");
                days = value.substring(pos_P+1,pos_D) + " Days Left";
            }
            holder.mTextDays.setText(days);

            String price = "Price: N/A";
            if (currentItem.has("buyItNowPrice")){
                price = "$" + currentItem.getJSONObject("buyItNowPrice").getString("__value__");
            }
            holder.mTextPrice.setText(price);

            String url = currentItem.getString("viewItemURL");
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            holder.getItemUrl(url); // get set view info better?

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }
}
