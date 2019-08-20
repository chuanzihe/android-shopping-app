package com.example.hw8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchForm extends Fragment implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher {

    private EditText keyword;
    private Spinner spinner;
    private CheckBox nearby;
    private EditText miles;
    private RadioButton current;
    private TextView current_text;
    private TextView from_text;
    private RadioButton otherzip;
    private Button search;
    private Button clear;
    private AppCompatAutoCompleteTextView autoTextView;
    private Context mContext;

    private CheckBox shipping;
    private CheckBox pickup;
    private CheckBox cond_new;
    private CheckBox cond_used;
    private CheckBox cond_unspec;
    private TextInputLayout textlayout_keyword;
    private TextInputLayout textlayout_zipcode;
    private String jsonzip;

    public static final String EXTRA_MESSAGE = "com.example.hw8.MESSAGE";

    public SearchForm() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();

        View view = inflater.inflate(R.layout.search_form, container, false);

        // category spinner
        spinner = view.findViewById(R.id.search_cate);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        from_text  = view.findViewById(R.id.search_text_from);
        keyword = view.findViewById(R.id.search_keyword);
        textlayout_keyword = view.findViewById(R.id.search_keyword_layout);
        textlayout_zipcode = view.findViewById(R.id.search_zipcode_layout);
        miles = view.findViewById(R.id.search_miles);

        current = view.findViewById(R.id.search_current);
        current_text = view.findViewById(R.id.search_current_text);
        current.setChecked(true);

        otherzip = view.findViewById(R.id.search_zipcode_radio);
        autoTextView = view.findViewById(R.id.autoCompleteTextView);
        autoTextView.addTextChangedListener(this);

        nearby = view.findViewById(R.id.search_nearby);
        nearby.setOnCheckedChangeListener(this);
        nearby.setChecked(false);

        // todo: set visibility

        shipping = view.findViewById(R.id.search_freeship);
        shipping.setOnCheckedChangeListener(this);

        pickup = view.findViewById(R.id.search_local);
        pickup.setOnCheckedChangeListener(this);

        cond_new = view.findViewById(R.id.search_new);
        cond_new.setOnCheckedChangeListener(this);
        cond_used = view.findViewById(R.id.search_used);
        cond_used.setOnCheckedChangeListener(this);
        cond_unspec = view.findViewById(R.id.search_unspec);
        cond_unspec.setOnCheckedChangeListener(this);

        search = view.findViewById(R.id.search_button_search);
        search.setOnClickListener(this);

        clear = view.findViewById(R.id.search_button_clear);
        clear.setOnClickListener(this);

        // visibility
        miles.setVisibility(View.GONE);
        from_text.setVisibility(View.GONE);
        current.setVisibility(View.GONE);
        current_text.setVisibility(View.GONE);
        otherzip.setVisibility(View.GONE);
        autoTextView.setVisibility(View.GONE);
        textlayout_zipcode.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String local_url = "http://ip-api.com/json";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, local_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject res = new JSONObject(response);
                            jsonzip = res.getString("zip");

                        } catch (Throwable t){
                            Log.e("Zip code","Could not parse JSON");
                        }
                        return;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                return;
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int cate_id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (cate_id == R.id.action_settings) {
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

//    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        @Override
        public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

        }

        @Override
        public void onNothingSelected (AdapterView < ? > parent){

        }
//    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){

            case R.id.search_nearby:

                if (nearby.isChecked()){
                    miles.setVisibility(View.VISIBLE);
                    from_text.setVisibility(View.VISIBLE);
                    current.setVisibility(View.VISIBLE);
                    current_text.setVisibility(View.VISIBLE);
                    otherzip.setVisibility(View.VISIBLE);
                    autoTextView.setVisibility(View.VISIBLE);
                    textlayout_zipcode.setVisibility(View.VISIBLE);


                    break;                     
                }
                else{
                    miles.setVisibility(View.GONE);
                    from_text.setVisibility(View.GONE);
                    current.setVisibility(View.GONE);
                    current_text.setVisibility(View.GONE);
                    otherzip.setVisibility(View.GONE);
                    autoTextView.setVisibility(View.GONE);
                    textlayout_zipcode.setVisibility(View.GONE);

                    break;
                }
            case R.id.search_current:
                if (current.isChecked()){
                    textlayout_zipcode.setFocusable(false);
//                    autoTextView.setFocusable(false);

                }
                else if(otherzip.isChecked()){
                    textlayout_zipcode.setFocusableInTouchMode(true);
//                    autoTextView.setFocusableInTouchMode(true);
                }
                
        }
    }

    @Override
    public void onClick(View view){

        switch(view.getId()){
            case R.id.search_button_clear:
                keyword.setText("");
                spinner.setSelection(0);
                cond_new.setChecked(false);
                cond_used.setChecked(false);
                cond_unspec.setChecked(false);
                shipping.setChecked(false);
                pickup.setChecked(false);
                nearby.setChecked(false);
                miles.setText("");
                current.setChecked(true);
                otherzip.setChecked(false);
                autoTextView.setText("");

                break;

            case R.id.search_button_search:

                // input check

                Boolean keyword_invalid = keyword.getText().toString().trim().length()==0;
                Boolean zipcode_invalid = otherzip.isChecked() && autoTextView.getText().toString().trim().length()<5;

                if(keyword_invalid){
                    textlayout_keyword.setError("Please enter mandatory field");
                }
                else{
                    textlayout_keyword.setError(null);
                }

                if(zipcode_invalid){
                    textlayout_zipcode.setError("Please enter mandatory field");
                }
                else{
                    textlayout_zipcode.setError(null);
                }

                if(keyword_invalid || zipcode_invalid){
                    Context context = getContext();         //toast
                    CharSequence text = "Please fix all fields with errors";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }

                String url_keyword = keyword.getText().toString();

                int spinner_id = (int) spinner.getSelectedItemId();
                String category = "";
                if(spinner_id == 0){category = "all";}
                else if(spinner_id == 1){category = "art";}
                else if(spinner_id == 2){category = "baby";}
                else if(spinner_id == 3){category = "books";}
                else if(spinner_id == 4){category = "clothing";}
                else if(spinner_id == 5){category = "computers";}
                else if(spinner_id == 6){category = "health";}
                else if(spinner_id == 7){category = "music";}
                else if(spinner_id == 8){category = "video";}

                String location = nearby.isChecked()?"1":"0";
                String val_miles = miles.getText().toString();

                if(val_miles.equals("")){
                    val_miles = "10";
                }
                String cur_zipcode = "90007";
                cur_zipcode = jsonzip;
                String number = autoTextView.getText().toString();

//                Boolean val_shipping = shipping.isChecked();
//                Boolean val_pickup   = pickup.isChecked();
//                Boolean val_new = cond_new.isChecked();
//                Boolean val_used = cond_used.isChecked();
//                Boolean val_unspec = cond_unspec.isChecked();

                String val_shipping = shipping.isChecked()?"1":"";
                String val_pickup   = pickup.isChecked()?"1":"";
                String val_new = cond_new.isChecked()?"1":"";
                String val_used = cond_used.isChecked()?"1":"";
                String val_unspec = cond_unspec.isChecked()?"1":"";

                JSONObject obj = new JSONObject();
                try {
                    obj.put("miles",val_miles);
                    obj.put("keyword", url_keyword);
                    obj.put("category", category);
                    obj.put("location",location);
                    obj.put("cur_zipcode",cur_zipcode);
                    obj.put("number",number);
                    obj.put("shipping",val_shipping);
                    obj.put("pickup",val_pickup);
                    obj.put("new",val_new);
                    obj.put("used",val_used);
                    obj.put("unspecified",val_unspec);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Intent intent = new Intent(getContext(), SearchResults.class);
                String message = obj.toString();
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String auto_url = "http://api.geonames.org/postalCodeSearchJSON?postalcode_startsWith="+s+"&username=heremy571&country=US&maxRows=5";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, auto_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject res = new JSONObject(response);
                            JSONArray zipcodeRes = res.getJSONArray("postalCodes");

                            ArrayList<String> zipList = new ArrayList<String>();
                            for(int i=0;i<zipcodeRes.length();i++) {
                                zipList.add(zipcodeRes.getJSONObject(i).getString("postalCode"));
                            }

                            ArrayAdapter<String> zipAdapter = new ArrayAdapter<String>
                                    (mContext, android.R.layout.select_dialog_item, zipList);
                            autoTextView.setThreshold(1); //will start working from first character
                            autoTextView.setAdapter(zipAdapter);

                        } catch (Throwable t){
                            Log.e("Zip code","Could not parse JSON");
                        }
                        return;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                return;
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    @Override
    public void afterTextChanged(Editable s) {

    }
}