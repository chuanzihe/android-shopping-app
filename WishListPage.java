package com.example.hw8;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class WishListPage extends Fragment {

    public static JSONArray mWishList;
    public static ArrayList<String> indWishList;
    private static Context wishContext;
    private static RecyclerView wishRecyclerView;
    private static TextView wishlistErrorView;
    private static TextView wishSummaryNum;
    private static TextView wishSummaryPrice;
    private static RecyclerView.Adapter wishAdapter;
    private static RecyclerView.LayoutManager wishLayoutManager;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public WishListPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wishContext = getContext(); // for static method
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.wish_list, container, false);

        // TODO: use shared preference in context

        prefs = getContext().getSharedPreferences("MyPrefFile", MODE_PRIVATE);
//        clearLocalWishList(); // TODO
        String restoredTextList = prefs.getString("WishList", "[]");
        String restoredTextInd = prefs.getString("indWishList", "");
        // TODO: empty wishlist
        if (!restoredTextList.equals("[]")) {
            try {
                mWishList = new JSONArray(restoredTextList);
                indWishList = new ArrayList<> (Arrays.asList(restoredTextInd.replaceAll("\\[|\\]", "").replaceAll("\\s+","").split(",")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            mWishList = new JSONArray();
            indWishList = new ArrayList<>();
        }

        wishRecyclerView = view.findViewById(R.id.wish_recyclerView);
        wishlistErrorView = view.findViewById(R.id.wishlist_error);
        wishSummaryPrice = view.findViewById(R.id.wish_sum_price);
        wishSummaryNum = view.findViewById(R.id.wish_sum_num);

        wishRecyclerView.setHasFixedSize(true); // size invariant to item numbers; performance
        wishLayoutManager = new GridLayoutManager(wishContext, 2);

        updateLocalWishList();
        wishRecyclerView.setLayoutManager(wishLayoutManager);

//        wishAdapter = new ItemAdapter(mWishList, wishContext);
//        wishAdapter.notifyDataSetChanged(); //re-render results on adapter context changed
//        wishRecyclerView.setLayoutManager(wishLayoutManager);
//        wishRecyclerView.setAdapter(wishAdapter);
//
//        if(!(mWishList.length() >0)){
//            wishRecyclerView.setVisibility(View.GONE);
//            wishlistErrorView.setVisibility(View.VISIBLE);
//        }
//        else{
//            wishRecyclerView.setVisibility(View.VISIBLE);
//            wishlistErrorView.setVisibility(View.GONE);
//        }



       return view;
    }

    static void updateLocalWishList(){

        editor =  wishContext.getSharedPreferences("MyPrefFile", MODE_PRIVATE).edit();
        editor.putString("WishList", WishListPage.mWishList.toString());
        editor.putString("indWishList", WishListPage.indWishList.toString());
        editor.apply();
        wishAdapter = new ItemAdapter(mWishList, wishContext);
        wishAdapter.notifyDataSetChanged(); //re-render results on adapter context changed
        wishRecyclerView.setAdapter(wishAdapter);

        if(indWishList.size()>0){
            wishRecyclerView.setVisibility(View.VISIBLE);
            wishlistErrorView.setVisibility(View.GONE);

        }
        else{
            wishRecyclerView.setVisibility(View.GONE);
            wishlistErrorView.setVisibility(View.VISIBLE);
        }


        float price = 0;
        for (int i = 0; i<mWishList.length(); i++) {

            float getprice = 0;
            try {
                JSONObject item = mWishList.getJSONObject(i);
                getprice = Float.valueOf(item.getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__"));
            } catch (JSONException e) {
                getprice = 0;
            }
            price  = price + getprice;
        }
        String summary_num  = "Wishlist total(" + mWishList.length() +" items):";
        String summary_price = "$" + price;
        wishSummaryNum.setText(summary_num);
        wishSummaryPrice.setText(summary_price);

    }

    static void clearLocalWishList(){

        editor =  wishContext.getSharedPreferences("MyPrefFile", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    static void addWishList(JSONObject itemInfo){

        try {
            String itemId = itemInfo.getJSONArray("itemId").getString(0);
            mWishList.put(itemInfo);
            indWishList.add(itemId);
            updateLocalWishList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void removeWishList(JSONObject itemInfo){
        try {
            String itemId = itemInfo.getJSONArray("itemId").getString(0);
            int index = indWishList.indexOf(itemId);
            mWishList.remove(index);
            indWishList.remove(index);
            updateLocalWishList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static boolean inList(String itemId){
        return indWishList.contains(itemId);
    }

}