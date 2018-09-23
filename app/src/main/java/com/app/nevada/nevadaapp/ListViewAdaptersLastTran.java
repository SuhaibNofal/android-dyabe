package com.app.nevada.nevadaapp;

import static com.app.nevada.nevadaapp.Constants.CUS_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.CUS_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_THIRD_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FOURTH_COLUMN;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdaptersLastTran extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    public ListViewAdaptersLastTran(Activity activity, ArrayList<HashMap<String, String>> list){
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
        convertView = inflater.inflate(R.layout.colmn_row_last_transaction, null);
        mHolder = new ViewHolder();

        mHolder.txttranAccName=(TextView) convertView.findViewById(R.id.tranAccName);
        mHolder.txttranDebt=(TextView) convertView.findViewById(R.id.tranDebt);
        mHolder.txttranCredit=(TextView) convertView.findViewById(R.id.tranCredit);
        mHolder.txttranSubject=(TextView) convertView.findViewById(R.id.tranSubject);

        HashMap<String, String> map=list.get(position);
        DecimalFormat format = new DecimalFormat("##.##");
        String formattedDebt = format.format(Float.parseFloat(map.get(LT_SECOND_COLUMN)));
        String formattedCredit = format.format(Float.parseFloat(map.get(LT_THIRD_COLUMN)));

        double Val =Double.parseDouble(map.get(LT_THIRD_COLUMN));

        mHolder.txttranAccName.setText(map.get(LT_FIRST_COLUMN));
        mHolder.txttranDebt.setText(formattedDebt);
        if (Val == 0)
        {
            mHolder.txttranCredit.setText(formattedCredit);
        } else
        {
            mHolder.txttranCredit.setText("("+ formattedCredit+ ")");
        }

        mHolder.txttranSubject.setText(map.get(LT_FOURTH_COLUMN));

        convertView.setTag(mHolder);

        return convertView;

    }
    private class ViewHolder {
        public TextView txttranDebt;
        public TextView txttranCredit;
        public TextView txttranAccName;
        public TextView txttranSubject;

    }
}