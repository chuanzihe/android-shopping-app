package com.example.hw8;

//import android.support.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private JSONArray mItemsList;
    private static Context mContext;

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public ImageView mImageVIew_wish;
        public TextView mTextView_title;
        public TextView mTextView_zipcode;
        public TextView mTextView_shipping;
        public TextView mTextView_condition;
        public TextView mTextView_price;
        public JSONObject mItemInfo;
        public String mDisplayTitle;


        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.results_imageView);
            mImageVIew_wish = itemView.findViewById(R.id.results_wishlist); //TODO: onclick listener
            mTextView_title = itemView.findViewById(R.id.results_title);
            mTextView_zipcode = itemView.findViewById(R.id.results_zipcode);
            mTextView_shipping = itemView.findViewById(R.id.results_shipping);
            mTextView_condition = itemView.findViewById(R.id.results_condition);
            mTextView_price = itemView.findViewById(R.id.results_price);

            mImageVIew_wish.setClickable(true);
            mImageVIew_wish.setOnClickListener(this);

            mImageView.setClickable(true);
            mImageView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.results_wishlist:
                    try {
                        String itemId = mItemInfo.getJSONArray("itemId").getString(0);


                        CharSequence text;
                        if(WishListPage.inList(itemId)){
                            text = mDisplayTitle + " was removed from wish list";
                            mImageVIew_wish.setImageResource(R.drawable.cart_plus);
                            WishListPage.removeWishList(mItemInfo);
                        }
                        else{
                            text = mDisplayTitle + " was added to wish list";
                            mImageVIew_wish.setImageResource(R.drawable.cart_remove);
                            WishListPage.addWishList(mItemInfo);
                        }
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(mContext, text, duration);
                        toast.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.results_imageView:
                    Intent intent = new Intent(mContext, DetailsPage.class);
                    String message = mItemInfo.toString();
                    intent.putExtra("Item Details", message);
                    mContext.startActivity(intent);
                    // TODO: toast

                    break;

                default:
                    break;
            }
        }

        public void getItemInfo(JSONObject itemInfo, String display_title){
            mItemInfo = itemInfo;
            mDisplayTitle = display_title;
        }
    }
    //TODO: pass json object + context for click
//    public ItemAdapter(ArrayList<SearchItems> exampleList) {
//        mItemsList = exampleList;
//    }

    public ItemAdapter(JSONArray itemsList, Context context) {
        mItemsList = itemsList;
        mContext   = context;
    }


//    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_items, parent, false);
        ItemViewHolder ivh = new ItemViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        try {
            JSONObject currentItem = mItemsList.getJSONObject(position);

            String itemId = currentItem.getJSONArray("itemId").getString(0);
            String title = currentItem.getJSONArray("title").getString(0);
            String display_title;
            if(title.length()>45){
                display_title = title.substring(0,40) + "...";
            }
            else{
                display_title = title;
            }
            String img_url = currentItem.getJSONArray("galleryURL").getString(0);

            String zipcode,shipping,condition,price;

            try{
                zipcode = "Zip: " + currentItem.getJSONArray("postalCode").getString(0);
            }
            catch(JSONException e) {
                zipcode = "N/A";
            }

            try{
                String shipping_cost = currentItem.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
                if(shipping_cost.equals("0.0")){
                    shipping = "Free Shipping";
                }
                else{
                    shipping = "With $" + shipping_cost;
                }
            }
            catch(JSONException e) {
                shipping = "N/A";
            }

            try{
                condition = currentItem.getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
            }
            catch(JSONException e){
                condition = "N/A";
            }

            try{
                price = "$" + currentItem.getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
            }
            catch(JSONException e){
                price = "N/A";
            }

            // todo: fit().center_side();
            Picasso.with(mContext).load(img_url).resize(160, 160).into(holder.mImageView);

            if(WishListPage.inList(itemId)) {
                holder.mImageVIew_wish.setImageResource(R.drawable.cart_remove);
            }
            else{
                holder.mImageVIew_wish.setImageResource(R.drawable.cart_plus);
            }


            holder.mTextView_title.setText(display_title);
            holder.mTextView_zipcode.setText(zipcode);
            holder.mTextView_condition.setText(condition);
            holder.mTextView_shipping.setText(shipping);
            holder.mTextView_price.setText(price);

            holder.getItemInfo(currentItem, display_title); // get set view info better?

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mItemsList.length();
    }
}
