package com.app.nevada.nevadaapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.app.nevada.nevadaapp.Constants.AR_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.AR_THIRD_COLUMN;

/**
 * Created by Mothana on 19/02/2017.
 */

public class ListViewAdapterAramcoOrders extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    public Activity activity;

    public ListViewAdapterAramcoOrders(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ListViewAdapterAramcoOrders.ViewHolder mHolder;
        LayoutInflater inflater=activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.colmn_row_aramco_orders, null);
        mHolder = new ListViewAdapterAramcoOrders.ViewHolder();

        mHolder.txtAramcoNo=(TextView) convertView.findViewById(R.id.lblAramcoNo);
        mHolder.txtAramcoItem=(TextView) convertView.findViewById(R.id.lblAramcoItem);
        mHolder.txtAramcoQty=(TextView) convertView.findViewById(R.id.lblAramcoQty);

        HashMap<String, String> map=list.get(position);
        mHolder.txtAramcoNo.setText(map.get(AR_FIRST_COLUMN));
        mHolder.txtAramcoItem.setText(map.get(AR_SECOND_COLUMN));
        mHolder.txtAramcoQty.setText(map.get(AR_THIRD_COLUMN));
        mHolder.txtAramcoNo.setTag(map.get(AR_FOURTH_COLUMN).concat(",").concat(map.get(AR_FIRST_COLUMN)).concat(",").concat(map.get(AR_SECOND_COLUMN)).concat(",").concat(map.get(AR_THIRD_COLUMN)).concat(",").concat(map.get("VarTimeOrder")));
        convertView.setTag(mHolder);

        return convertView;
    }
    private class ViewHolder {
        public TextView txtAramcoNo;
        public TextView txtAramcoItem;
        public TextView txtAramcoQty;

    }

}
