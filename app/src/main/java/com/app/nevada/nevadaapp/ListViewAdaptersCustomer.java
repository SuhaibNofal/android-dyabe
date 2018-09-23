package com.app.nevada.nevadaapp;

import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdaptersCustomer extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    public Activity activity;

    public ListViewAdaptersCustomer(Activity activity, ArrayList<HashMap<String, String>> list){
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
        convertView = inflater.inflate(R.layout.colmn_row_customer, null);
        mHolder = new ViewHolder();

        mHolder.txtName=(TextView) convertView.findViewById(R.id.txtCName);
        mHolder.txtNumber=(TextView) convertView.findViewById(R.id.txtCNo);
        mHolder.txtAccountNumber=(TextView) convertView.findViewById(R.id.txtCAccNo);

        HashMap<String, String> map=list.get(position);
        String t1 =map.get(CUS_FIRST_COLUMN);
        String t2 =map.get(CUS_SECOND_COLUMN);
        String t3 =map.get(CUS_THIRD_COLUMN);
        mHolder.txtName.setText(map.get(CUS_FIRST_COLUMN));
        mHolder.txtNumber.setText(map.get(CUS_SECOND_COLUMN));
        mHolder.txtAccountNumber.setText(map.get(CUS_THIRD_COLUMN));

        convertView.setTag(mHolder);

        return convertView;
    }
    class ViewHolder {
        public TextView txtName;
        public TextView txtNumber;
        public TextView txtAccountNumber;

    }

}