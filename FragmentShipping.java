package com.example.hw8;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentShipping extends Fragment {

    private LayoutInflater mInflater;
    private Context mContext;
    private TableLayout mSoldbyTb;
    private TableLayout mShippingTb;
    private TableLayout mReturnTb;
    private TableRow rSoldby;
    private TableRow rShipping;
    private TableRow rReturn;
    private boolean soldby_view;
    private boolean shipping_view;
    private boolean return_view;
    private JSONObject mDetailInfo;
    private JSONObject mItemInfo;
    private TextView error_Noresult;
    private CircularScoreView scoreView;




    public FragmentShipping() {
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

        View view = inflater.inflate(R.layout.fragment_shipping, container, false);
        mSoldbyTb = view.findViewById(R.id.ship_table_soldby);
        mShippingTb = view.findViewById(R.id.ship_table_shipping);
        mReturnTb = view.findViewById(R.id.ship_table_return);
        rSoldby = view.findViewById(R.id.header_soldby);
        rShipping = view.findViewById(R.id.header_shipping);
        rReturn = view.findViewById(R.id.header_return);
        error_Noresult = view.findViewById(R.id.error_noresult);
        soldby_view = false;
        shipping_view  = false;
        return_view  = false;

        scoreView = view.findViewById(R.id.scoreview);
        return view;
    }


    public void getProductInfo(JSONObject detailInfo, JSONObject itemInfo) throws JSONException {
        mDetailInfo = detailInfo;
        mItemInfo = itemInfo;
        createSoldbyTb();
        createShippingTb();
        createReturnTb();

        if(!soldby_view && !shipping_view && !return_view ){
            // TODO: error message
            error_Noresult.setVisibility(View.VISIBLE);
        }
        else{
            error_Noresult.setVisibility(View.GONE);
        }

        if(soldby_view) {
            mSoldbyTb.setVisibility(View.VISIBLE);
            rSoldby.setVisibility(View.VISIBLE);
        }
        else {
            mSoldbyTb.setVisibility(View.GONE);
            rSoldby.setVisibility(View.GONE);
        }

        if(shipping_view) {
            mShippingTb.setVisibility(View.VISIBLE);
            rShipping.setVisibility(View.VISIBLE);
        }
        else {
            mShippingTb.setVisibility(View.GONE);
            rShipping.setVisibility(View.GONE);
        }

        if(return_view) {
            mReturnTb.setVisibility(View.VISIBLE);
            rReturn.setVisibility(View.VISIBLE);
        }
        else {
            mReturnTb.setVisibility(View.GONE);
            rReturn.setVisibility(View.GONE);
        }

    }

    private int dpConvert(int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    private void createShippingTb() throws JSONException {

        if(mItemInfo.has("shippingInfo")) {
            String shipping_cost = mItemInfo.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
            String shipping;
            if (shipping_cost.equals("0.0")) {
                shipping = "Free Shipping";
            } else {
                shipping = "With $" + shipping_cost;
            }
            createShippingRow("Shipping Cost",shipping);
            shipping_view = true;
        }

        if(mDetailInfo.has("GlobalShipping")){
            shipping_view = true;
            String global;
            Boolean global_tf = mDetailInfo.getBoolean("GlobalShipping");
            if(global_tf){
                global = "Yes";
            }
            else{
                global = "No";
            }
            createShippingRow("Global Shipping",global);
        }

        if(mDetailInfo.has("HandlingTime")){
            shipping_view = true;
            String time;
            int days = mDetailInfo.getInt ("HandlingTime");
            if(days>1){
                time = days + " days";
            }
            else{
                time = days + " day";
            }
            createShippingRow("Handling time",time);
        }

        if(mDetailInfo.has("ConditionDescription")){ //TODO: no item satisfied
            shipping_view = true;
            String condition = mDetailInfo.getString ("ConditionDescription");
            createShippingRow("Condition",condition);

        }




    }
    private void createReturnTb() throws JSONException {

        if (mDetailInfo.has("ReturnPolicy")){
            JSONObject policy = mDetailInfo.getJSONObject("ReturnPolicy");
            if (policy.has("ReturnsAccepted")){
                String accepted = policy.getString("ReturnsAccepted");
                createReturnRow("Policy",accepted);
                return_view = true;
            }

            if (policy.has("ReturnsWithin")){
                String within  = policy.getString("ReturnsWithin");
                createReturnRow("Returns within",within);
                return_view = true;
            }

            if (policy.has("Refund")){
                String refund  = policy.getString("Refund");
                createReturnRow("Refund Mode",refund);
                return_view = true;
            }

            if (policy.has("ShippingCostPaidBy")){
                String shippedby  = policy.getString("ShippingCostPaidBy");
                createReturnRow("Shipped by",shippedby);
                return_view = true;
            }
        }
    }

    private void createSoldbyTb() throws JSONException {
        // Store info
//        "storeInfo":[{ "storeName":["greenmartstore"],"storeURL":["http:\/\/stores.ebay.com\/greenmartstore"]}],

        if (mItemInfo.has("storeInfo")) {
            String storeName = mItemInfo.getJSONArray(("storeInfo")).getJSONObject(0)
                    .getJSONArray("storeName").getString(0);
            String storeURL  = mItemInfo.getJSONArray(("storeInfo")).getJSONObject(0)
                    .getJSONArray("storeURL").getString(0);
            String strHtml = "<a href='" + storeURL + "'>" + storeName + "</a>";

            Spanned row_html = Html.fromHtml(strHtml);
            createSoldbyRow("Store Name",row_html);
            soldby_view = true;
        }


//        "sellerInfo":[{"sellerUserName":["gmdeals_inc"],"feedbackScore":["3307"],"positiveFeedbackPercent":["97.9"],"feedbackRatingStar":["Red"],"topRatedSeller":["true"]}],"
        if(mItemInfo.has("sellerInfo")) {
            JSONObject sellerInfo = mItemInfo.getJSONArray("sellerInfo").getJSONObject(0);
            //        "feedbackScore":["3307"]
            if (sellerInfo.has("feedbackScore")) {
                String score = sellerInfo.getJSONArray("feedbackScore").getString(0);
                Spanned row_html = Html.fromHtml(score);
                createSoldbyRow("Feedback Score",row_html);
                soldby_view = true;
            }

            if (sellerInfo.has("positiveFeedbackPercent")) {
                String feedback = sellerInfo.getJSONArray("positiveFeedbackPercent").getString(0);
                Float feedback_score = Float.parseFloat(feedback);
                int int_score = Math.round(feedback_score);
                Spanned row_html = Html.fromHtml(""+int_score+"");
                createSoldbyRow("Popularity",row_html);
//                scoreView.setVisibility(View.VISIBLE);
                soldby_view = true;
            }
            else {
//                scoreView.setVisibility(View.GONE);
            }

            if (sellerInfo.has("feedbackRatingStar")) {
                String star = sellerInfo.getJSONArray("feedbackRatingStar").getString(0);

                if(star.contains("None")){
                    soldby_view = false;
                }
                else {
                    Spanned row_html = Html.fromHtml(star);
                    createSoldbyRow("Feedback star", row_html);
                }


            }
        }
    }
    private void createSoldbyRow(String th, Spanned tb){
        TableRow tableRow = new TableRow(mContext);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setMinimumHeight(dpConvert(40));

        TextView textView0 = new TextView(mContext);       //placeholder
        textView0.setWidth(dpConvert(30));
        tableRow.addView(textView0, 0);

        TextView textView1 = new TextView(mContext);       //title
        textView1.setWidth(dpConvert(120));
        textView1.setText(th);
        tableRow.addView(textView1, 1);

        if(th.equals("Popularity")){

//            CircularScoreView circle = new CircularScoreView(mContext, null);
            int score = Integer.parseInt(String.valueOf(tb));
//            circle.setScore(score);
//            tableRow.addView(circle, 2);

            scoreView.setScore(score);
            ((ViewGroup)scoreView.getParent()).removeView(scoreView);
            tableRow.addView(scoreView);

//            tableRow.addView(scoreView, 2);

//            TextView textView2 = new TextView(mContext);       //contents
//            textView2.setText(tb);
//            textView2.setWidth(dpConvert(200));
//            textView2.setHeight(dpConvert(30));
//            tableRow.addView(textView2, 3);


        }
        else if(th.equals("Feedback star")){
            String star = tb.toString();
            String star_color = tb.toString();
            ImageView feedstar = new ImageView(mContext);

            if(star.contains("Shooting")){
                feedstar.setImageResource(R.drawable.star_circle);
                star_color = tb.toString().replace("Shooting","");
            }
            else{
                feedstar.setImageResource(R.drawable.star_circle_outline);
            }

            switch (star_color){
                case "Yellow":
                    feedstar.setColorFilter(Color.YELLOW);break;
                case "Blue":
                    feedstar.setColorFilter(Color.BLUE);break;
                case "Turquoise":
                    feedstar.setColorFilter(Color.rgb(64,224,208));break;
                case "Purple":
                    feedstar.setColorFilter(Color.rgb(128,0,128));break;
                case "Red":
                    feedstar.setColorFilter(Color.RED);break;
                case "Green":
                    feedstar.setColorFilter(Color.GREEN);break;
                case "Silver":
                    feedstar.setColorFilter(Color.LTGRAY);break;
                default:
                    feedstar.setColorFilter(Color.BLACK);break;
            }
            feedstar.setMaxHeight(dpConvert(30));
            tableRow.addView(feedstar);

        }
        else {
            TextView textView2 = new TextView(mContext);       //contents
            textView2.setText(tb);
            textView2.setWidth(dpConvert(200));
            textView2.setHeight(dpConvert(30));
            tableRow.addView(textView2, 2);
        }

        mSoldbyTb.addView(tableRow);
    }

    private void createShippingRow(String th, String tb){

        TableRow tableRow = new TableRow(mContext);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setMinimumHeight(dpConvert(40));

        TextView textView0 = new TextView(mContext);       //placeholder
        textView0.setWidth(dpConvert(30));
        tableRow.addView(textView0, 0);

        TextView textView1 = new TextView(mContext);       //title
        textView1.setWidth(dpConvert(120));
        textView1.setText(th);
        tableRow.addView(textView1, 1);

        TextView textView2 = new TextView(mContext);       //contents
        textView2.setText(tb);
        textView2.setWidth(dpConvert(200));
        textView2.setHeight(dpConvert(30));          // TODO: height to 50 to fit 2 lines
        tableRow.addView(textView2, 2);

        mShippingTb.addView(tableRow);
    }

    private void createReturnRow(String th, String tb){

        TableRow tableRow = new TableRow(mContext);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setMinimumHeight(dpConvert(40));

        TextView textView0 = new TextView(mContext);       //placeholder
        textView0.setWidth(dpConvert(30));
        tableRow.addView(textView0, 0);

        TextView textView1 = new TextView(mContext);       //title
        textView1.setWidth(dpConvert(120));
        textView1.setText(th);
        tableRow.addView(textView1, 1);

        TextView textView2 = new TextView(mContext);       //contents
        textView2.setText(tb);
        textView2.setWidth(dpConvert(200));
        textView2.setHeight(dpConvert(30));
        tableRow.addView(textView2, 2);
        mReturnTb.addView(tableRow);
    }


}




