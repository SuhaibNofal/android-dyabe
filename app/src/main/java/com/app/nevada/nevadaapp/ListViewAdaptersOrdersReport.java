package com.app.nevada.nevadaapp;

import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SIXTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SEVENTH_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdaptersOrdersReport extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    public Activity activity;

    public ListViewAdaptersOrdersReport(Activity activity, ArrayList<HashMap<String, String>> list){
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
        final ViewHolder mHolder;
        LayoutInflater inflater=activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.colmn_row_orders_report, null);
        mHolder = new ViewHolder();

        mHolder.txtStatusOrderNo=(TextView) convertView.findViewById(R.id.txtStatusOrderNo);
        mHolder.txtStatusCustName=(TextView) convertView.findViewById(R.id.txtStatusCustName);
        mHolder.txtStatusOnRoad=(TextView) convertView.findViewById(R.id.txtStatusOnRoad);
        mHolder.txtStatusDriverApproved=(TextView) convertView.findViewById(R.id.txtStatusDriverApproved);
        mHolder.txtStatusCustApproved=(TextView) convertView.findViewById(R.id.txtStatusCustApproved);

        HashMap<String, String> map=list.get(position);
        String t1 =map.get(CUS_THIRD_COLUMN);
        mHolder.txtStatusOrderNo.setText(map.get(CUS_FIRST_COLUMN));
        mHolder.txtStatusCustName.setText(map.get(CUS_SECOND_COLUMN));
        if (map.get(CUS_THIRD_COLUMN).equals("True")){
            mHolder.txtStatusOnRoad.setText("نعم");
        }else{
            mHolder.txtStatusOnRoad.setText("لا");
        }
        if (map.get(CUS_FOURTH_COLUMN).equals("True")){
            mHolder.txtStatusDriverApproved.setText("نعم");
        }else{
            mHolder.txtStatusDriverApproved.setText("لا");
        }
        if (map.get(CUS_FIFTH_COLUMN).equals("True")){
            mHolder.txtStatusCustApproved.setText("نعم");
        }else{
            mHolder.txtStatusCustApproved.setText("لا");
        }
        convertView.setTag(mHolder);

        return convertView;
    }
    class ViewHolder {
        public TextView txtStatusOrderNo;
        public TextView txtStatusCustName;
        public TextView txtStatusOnRoad;
        public TextView txtStatusDriverApproved;
        public TextView txtStatusCustApproved;
    }

}
