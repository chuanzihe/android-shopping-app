package com.example.hw8;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

public class DetailsPage extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FragmentPhotos fragmentPhotos;
    private FragmentProducts fragmentProducts;
    private FragmentSimilar fragmentSimilar;
    private FragmentShipping fragmentShipping;
    private static String mItemId;
    private String shareMessage;
    private String shareURL;
    private String title_display;
    private JSONObject itemInfo;
    private FloatingActionButton fab;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_page);

        mContext = this;

        Intent intent = getIntent();
        String message = intent.getStringExtra("Item Details");

        itemInfo = null;
        String subtitle = "";
        String title = "";
        String price = "";
        String fbURL = "";
        title_display = "";

        try {
            itemInfo = new JSONObject(message);
            mItemId = itemInfo.getJSONArray("itemId").getString(0);
            title = itemInfo.getJSONArray("title").getString(0);
            price = itemInfo.getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__");

            if(title.length()>30){
                title_display = title.substring(0,30) + "...";
            }
            else {
                title_display = title.substring(0,title.length());
            }
            shareMessage = "Buy " + title_display + " for $" + price + " from Ebay!";

            if(itemInfo.has("subtitle")){
                subtitle  = itemInfo.getJSONArray("subtitle").getString(0);
            }
            Log.d("item info", message);
        } catch (Throwable t) {
            Log.e("item info", "Could not parse malformed JSON: \"" + message + "\"");
        }

        ViewPager viewPager = findViewById(R.id.details_container);
        MainActivity.ViewPagerAdapter adapter = new MainActivity.ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setOffscreenPageLimit(4); // TODO: note, render four fragment at once

        fragmentPhotos = new FragmentPhotos();
        fragmentProducts = new FragmentProducts();
        fragmentShipping = new FragmentShipping();
        fragmentSimilar = new FragmentSimilar();

        adapter.addFragment(fragmentProducts,"Products");
        adapter.addFragment(fragmentShipping,"Shipping");
        adapter.addFragment(fragmentPhotos,"Photos");
        adapter.addFragment(fragmentSimilar,"Similar");

        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.details_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant);
        tabLayout.getTabAt(1).setIcon(R.drawable.truck_delivery);
        tabLayout.getTabAt(2).setIcon(R.drawable.google);
        tabLayout.getTabAt(3).setIcon(R.drawable.equal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(title);


        fab = (FloatingActionButton) findViewById(R.id.fab_cart);

        if(WishListPage.inList(mItemId)){
            fab.setImageResource(R.drawable.cart_remove);
        }
        else {
            fab.setImageResource(R.drawable.cart_plus);
        }

        final JSONObject finalItemInfo1 = itemInfo;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text;
                if(WishListPage.inList(mItemId)){
                    text = title_display + " was removed from wish list";
                    fab.setImageResource(R.drawable.cart_plus);
                    WishListPage.removeWishList(finalItemInfo1);
                }
                else{
                    text = title_display + " was added to wish list";
                    fab.setImageResource(R.drawable.cart_remove);
                    WishListPage.addWishList(finalItemInfo1);
                }
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(mContext, text, duration);
                toast.show();

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        // prepare info containers
        JSONObject infoProduct = new JSONObject();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=ChuanziH-csci571p-PRD-c16de56b6-2b65e3cb&siteid=0&version=967&ItemID=" + mItemId + "&IncludeSelector=Description,Details,ItemSpecifics";
        String coded_url = URLEncoder.encode(url);
        String aws_url = "http://chuanzihw8.us-west-2.elasticbeanstalk.com/showdetails?url=" + coded_url;

        // Request a string response from the provided URL.
        final String finalSubtitle = subtitle;
        final JSONObject finalItemInfo = itemInfo;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, aws_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject res = new JSONObject(response);
                            JSONObject infoDetails = res.getJSONObject("Item");
                            shareURL = infoDetails.getString("ViewItemURLForNaturalSearch");
                            // needed: title, subtitle, price, shipping
                            fragmentProducts.getProductInfo(infoDetails, finalSubtitle, itemInfo); //TODO: add subtitle
                            fragmentShipping.getProductInfo(infoDetails, finalItemInfo);

                        } catch (Throwable t){
                            Log.e("request details","Could not parse JSON");
                        }
                        return;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fragment", "Bad");
                return;
            }
        });

        // TODO: encode to aws
        // TODO: weird photos?
        String photo_url = "https://www.googleapis.com/customsearch/v1?q=" + title +"&cx=017113069725005104885:iipkotvstey&imgSize=huge&imgType=news&num=8&searchType=image&key=AIzaSyBZaxZtlzB3RBLLR_cmMzCROk1iQWOekZ0";
        StringRequest photoRequest = new StringRequest(Request.Method.GET, photo_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject res = new JSONObject(response);
                            JSONArray infoPhotos = res.getJSONArray("items");
                            fragmentPhotos.getInfo(infoPhotos);
                        } catch (Throwable t){
                            Log.e("Photos","Photo frag error");
                        }
                        return;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fragment", "Bad");
                return;
            }
        });

        // TODO: encode to aws
        String similar_url = "http://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getSimilarItems&SERVICE-NAME=MerchandisingService&SERVICE-VERSION=1.1.0&CONSUMER-ID=ChuanziH-csci571p-PRD-c16de56b6-2b65e3cb&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&itemId=" + mItemId + "&maxResults=20";
        StringRequest similarRequest = new StringRequest(Request.Method.GET, similar_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject res = new JSONObject(response);
                            JSONArray infoSimilar = res.getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").getJSONArray("item");
                            fragmentSimilar.getInfo(infoSimilar);
                        } catch (Throwable t){
                            Log.e("Similar","Similar frag error");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Fragment", "Bad");

            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.add(photoRequest);
        queue.add(similarRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fb) {
            String mfbURL = "https://www.facebook.com/sharer/sharer.php?hashtag=%23CSCI571Spring2019Ebay&u="
                    +shareURL+"&src=sdkpreparse&quote="+shareMessage;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mfbURL));
            getApplicationContext().startActivity(browserIntent);
            return true;
        }

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_details_page, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }
}
