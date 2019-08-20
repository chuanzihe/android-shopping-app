package com.example.hw8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SearchResults extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView progressText;

    private Context mContext;
    private TextView mSummaryText;
    private TextView mErrorText;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;

        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_search_results);
        Toolbar tb = findViewById(R.id.toolbar_bar);
//        getSupportActionBar();
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(SearchForm.EXTRA_MESSAGE);

        progressBar = findViewById(R.id.results_progressBar);
        progressText = findViewById(R.id.results_progressText);

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        mRecyclerView = findViewById(R.id.results_recyclerView);
        mRecyclerView.setHasFixedSize(true); // size invariant to item numbers; performance
        mRecyclerView.setVisibility(View.GONE);
        mSummaryText = findViewById(R.id.search_summary);
        mSummaryText.setVisibility(View.GONE);

        mErrorText = findViewById(R.id.search_error_noresults);
        mErrorText.setVisibility(View.GONE);

        String aws_url = "";
        String keyword = "";
        try {
            JSONObject obj = new JSONObject(message);
            keyword = obj.getString("keyword");

            aws_url = "http://chuanzihw8.us-west-2.elasticbeanstalk.com/showitems?"
                    + "miles="+obj.getString("miles")
                    + "&keyword="+ URLEncoder.encode(obj.getString("keyword"), StandardCharsets.UTF_8.toString())
                    + "&category="+obj.getString("category")
                    + "&location="+obj.getString("location")
                    + "&cur_zipcode="+obj.getString("cur_zipcode")
                    + "&number="+obj.getString("number")
                    + "&shipping="+obj.getString("shipping")
                    + "&pickup="+obj.getString("pickup")
                    + "&new="+obj.getString("new")
                    + "&used="+obj.getString("used")
                    + "&unspecified="+obj.getString("unspecified");

//            aws_url = URLEncoder.encode(aws_url, StandardCharsets.UTF_8.toString());
            Log.d("My App", obj.toString());
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + message + "\"");
        }

        final String showkeyword = keyword;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "http://chuanzihw8.us-west-2.elasticbeanstalk.com/showitems?category=all&cur_zipcode=90007&keyword=iphone&location=0&miles=10&new=fasle&pickup=false&shipping=false&unspecified=false&used=false&zipcode=&number=";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, aws_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);

                        try{
                            JSONObject res = new JSONObject(response);
                            JSONArray products = res.getJSONArray("findItemsAdvancedResponse").getJSONObject(0).getJSONArray("searchResult").getJSONObject(0).getJSONArray("item");

                            String summary_html = "Showing " + "<font color='#FFF34B29'> " + products.length() + "</font>"
                                                + " results for " + "<font color='#FFF34B29'> " + showkeyword + "</font>";
                            mSummaryText.setText(Html.fromHtml(summary_html));
//                            mSummaryText.setText("TEST");
                            mSummaryText.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLayoutManager = new GridLayoutManager(mContext, 2);
                            mAdapter = new ItemAdapter(products, mContext);
                            mAdapter.notifyDataSetChanged();

                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                        } catch (Throwable t){
                            mErrorText.setVisibility(View.VISIBLE);
                            Log.e("My App","Could not parse JSON");
                        }
                        return;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: error handling
                mErrorText.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.VISIBLE);
//                progressText.setVisibility(View.VISIBLE);
                return;
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

//    @Override
    public boolean onOptionsItemSelected(int featureId, MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
        }
        return true;
    }


    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }





}
