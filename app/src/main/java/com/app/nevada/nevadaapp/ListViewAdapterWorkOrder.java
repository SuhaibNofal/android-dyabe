package com.app.nevada.nevadaapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FIFTH_COLUMN;

public class ListViewAdapterWorkOrder extends BaseAdapter {
    ArrayList<HashMap<String,String>>list;
    Activity activity;
    public ListViewAdapterWorkOrder(Activity activity,ArrayList<HashMap<String,String>>list){
        this.activity=activity;
        this.list=list;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolderWork mHolder;
        LayoutInflater inflater=activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.colmn_row_work_order, null);
        mHolder = new ViewHolderWork();
        mHolder.tvcridit=(TextView) convertView.findViewById(R.id.tv_cridit_work);
        mHolder.tvOrderNumber=(TextView) convertView.findViewById(R.id.tv_order_number_work);
        mHolder.tvStationName=(TextView) convertView.findViewById(R.id.tv_station_name_work);
        mHolder.tvUploadDate=(TextView) convertView.findViewById(R.id.tv_uplod_date_work);

        HashMap<String, String> map=list.get(position);
        mHolder.tvcridit.setText(map.get(CUS_FIRST_COLUMN));
        mHolder.tvOrderNumber.setText(map.get(CUS_SECOND_COLUMN));
        mHolder.tvStationName.setText(map.get(CUS_THIRD_COLUMN));
        mHolder.tvUploadDate.setText(map.get(CUS_FOURTH_COLUMN));
        convertView.setTag(mHolder);
        return convertView;
    }
    class ViewHolderWork {
        public TextView tvcridit;
        public TextView tvOrderNumber;
        public TextView tvStationName;
        public TextView tvUploadDate;

    }
}
