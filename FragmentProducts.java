package com.example.hw8;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProducts extends Fragment {

    private LayoutInflater mInflater;
    private Context mContext;
    private LinearLayout mGallery;
    private TableLayout mHighlightTb;
    private TableLayout mSpecTb;
    private TextView mTitle;
    private TextView mPriceShip;


    public FragmentProducts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        mTitle = view.findViewById(R.id.product_title);
        mPriceShip = view.findViewById(R.id.product_price_ship);
        mGallery = view.findViewById(R.id.detail_product_gallery);
        mHighlightTb = view.findViewById(R.id.table_highlight);
        mSpecTb = view.findViewById(R.id.table_spec);
        return view;
    }

    public void getProductInfo(JSONObject details, String subtitle, JSONObject itemInfo) throws JSONException {
        // todo: params as title, subtitle, with price, shipping
        JSONObject mDetails = details;

        if(mDetails.has("Title")){
            mTitle.setText(mDetails.getString("Title"));
        }

        if(mDetails.has("PictureURL")) {

            JSONArray ImgURLs = mDetails.getJSONArray("PictureURL");
            createGallery(ImgURLs);

        }

        // NOTE: equals do not use null
//        if(!subtitle.equals("")) {
        if(itemInfo.has("subtitle")){
            String showsubtitile = itemInfo.getJSONArray("subtitle").getString(0);
            createHighlightRow("Subtitle", showsubtitile);
        }

        String showshipping = "N/A";
        String shipping_cost = itemInfo.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
        if(shipping_cost.equals("0.0")){
            showshipping = "Free Shipping";
        }
        else{
            showshipping = "With $" + shipping_cost;
        }


        String price = "";
        if(itemInfo.has("sellingStatus")){
            price = itemInfo.getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
            createHighlightRow("Price",price);
        }

        String priceshipping = "<font color='purple'>" + "$ " + price + "</font>" + " " + showshipping;
        mPriceShip.setText(Html.fromHtml(priceshipping));

        if(mDetails.has("ItemSpecifics")) {
            try {
                JSONArray specs = mDetails.getJSONObject("ItemSpecifics").getJSONArray("NameValueList");
                ArrayList<String> specsInfo = new ArrayList<String>();
                String brand = "";
                for (int i = 0; i < specs.length(); i++) {
                    String value = specs.getJSONObject(i).getJSONArray("Value").getString(0);
                    if(specs.getJSONObject(i).getString("Name").equals("Brand")){
                        brand = value;
                        createHighlightRow("Brand", brand);
                    }
                    else {
                        specsInfo.add(value);
                    }
                }
                createSpecTable(specsInfo,brand);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void createGallery(JSONArray ImgURLs)
    {
        for (int i = 0; i < ImgURLs.length(); i++)
        {
            View view = mInflater.inflate(R.layout.activity_details_product_gallery,
                    mGallery, false);
            String img_url = null;
            try {
                img_url = ImgURLs.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ImageView img = view
                    .findViewById(R.id.id_index_gallery_item_image);
            Picasso.with(mContext).load(img_url).fit().centerInside().into(img);
            mGallery.addView(view);
        }
    }

    private int dpConvert(int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    private void createHighlightRow(String th, String tb){
        TableRow tableRow = new TableRow(mContext);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(layoutParams);

        final float scale = getContext().getResources().getDisplayMetrics().density;

        TextView textView0 = new TextView(mContext);       //placeholder
        textView0.setWidth(dpConvert(50));
        tableRow.addView(textView0, 0);

        TextView textView1 = new TextView(mContext);       //first column
        textView1.setWidth(dpConvert(90));
        textView1.setText(th);
        textView1.setTypeface(null, Typeface.BOLD);
        tableRow.addView(textView1, 1);
        TextView textView2 = new TextView(mContext);       //second column
        textView2.setText(tb);
        textView2.setHeight(dpConvert(50));
        tableRow.addView(textView2, 2);
        mHighlightTb.addView(tableRow);
    }

    private void createSpecTable(ArrayList<String> specs, String brand){
        String ul_html = "<ul>";
        if(!brand.equals("")){
            ul_html += "<li> " + brand + "</li>";;
        }
        for(int i=0;i<specs.size();i++){
            String li = "<li> " + specs.get(i) + "</li>";
            ul_html += li;
        }

        ul_html += "</ul>";

        TableRow tableRow = new TableRow(mContext);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(layoutParams);

        TextView textView1 = new TextView(mContext);       //blank
        textView1.setWidth(dpConvert(50));
        textView1.setTypeface(null, Typeface.BOLD);
        tableRow.addView(textView1, 0);

        TextView specsView = new TextView(mContext);       //second column
        specsView.setText(Html.fromHtml(ul_html));
        tableRow.addView(specsView, 1);
        mSpecTb.addView(tableRow);
    }

}
