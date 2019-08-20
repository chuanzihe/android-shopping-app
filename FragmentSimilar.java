package com.example.hw8;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSimilar extends Fragment implements AdapterView.OnItemSelectedListener{

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private JSONArray mItemList;
    private ArrayList<JSONObject> mItemArr = new ArrayList<>(), mItemArrOrigin = new ArrayList<>();
    private Spinner spinner_type;
    private Spinner spinner_order;
    private String mSpinnerType;
    private int mSpinnerOrder;


    public FragmentSimilar() {
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
        View view = inflater.inflate(R.layout.fragment_similar, container, false);
        mRecyclerView = view.findViewById(R.id.similar_recyclerView);
        mRecyclerView.setHasFixedSize(true); // size invariant to item numbers; performance

        // type spinner
        spinner_type = view.findViewById(R.id.spinner_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_type = ArrayAdapter.createFromResource(mContext,
                R.array.spinner_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_type.setAdapter(adapter_type);
        spinner_type.setOnItemSelectedListener(this);

        // order spinner
        spinner_order = view.findViewById(R.id.spinner_order);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_order = ArrayAdapter.createFromResource(mContext,
                R.array.spinner_order, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_order.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_order.setAdapter(adapter_order);
        spinner_order.setOnItemSelectedListener(this);

        return view;
    }

    public void getInfo(JSONArray itemlist) throws JSONException {
        mItemList = itemlist;

        mItemArrOrigin = new ArrayList<JSONObject>();
        if (itemlist != null) {
            for (int i=0;i<itemlist.length();i++){
                mItemArrOrigin.add(itemlist.getJSONObject(i));
            }
        }

        mItemArr = new ArrayList<JSONObject>();
        for(JSONObject p : mItemArrOrigin) {
            mItemArr.add(new JSONObject(p.toString()));  //TODO: deep copy?
        }

        sortItemList();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {

            case R.id.spinner_type:
                mSpinnerType = (String) parent.getSelectedItem();
                sortItemList();
                break;
            case R.id.spinner_order:
                String order = (String) parent.getSelectedItem();
                mSpinnerOrder = order.equals("Ascending")? 1:-1;
                sortItemList();
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void sortItemList(){
        if(mSpinnerType.equals("Default")){
            mItemArr = new ArrayList<JSONObject>();
            for(JSONObject p : mItemArrOrigin) {
                try {
                    mItemArr.add(new JSONObject(p.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            spinner_order.setEnabled(false);

        }
        else {

            spinner_order.setEnabled(true);

            Collections.sort(mItemArr, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {

                    try {
                        switch (mSpinnerType) {

                            case "Name":
                                String lhs_name = lhs.getString("title");
                                String rhs_name = rhs.getString("title");
                                return mSpinnerOrder * lhs_name.compareTo(rhs_name);

                            case "Price":
                                Float lhs_value = Float.parseFloat(lhs.getJSONObject("buyItNowPrice").getString("__value__"));
                                Float rhs_value = Float.parseFloat(rhs.getJSONObject("buyItNowPrice").getString("__value__"));
                                return mSpinnerOrder * lhs_value.compareTo(rhs_value);

                            case "Days":
                                String lvalue = lhs.getString("timeLeft");
                                int pos_D = lvalue.indexOf("D");
                                int lhs_days = Integer.parseInt(lvalue.substring(1, pos_D));

                                String rvalue = rhs.getString("timeLeft");
                                pos_D = rvalue.indexOf("D");
                                int rhs_days = Integer.parseInt(rvalue.substring(1, pos_D));
                                return mSpinnerOrder * (lhs_days-rhs_days);

                            default:
                                return 0;
                        }

                    } catch (JSONException e) {
                        return 0;
                    }

                }

            });
        }
        mLayoutManager = new GridLayoutManager(mContext, 1);
        mAdapter = new SimilarAdapter(mItemArr, mContext);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
