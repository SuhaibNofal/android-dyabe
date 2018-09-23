package com.app.nevada.nevadaapp;

import static com.app.nevada.nevadaapp.Constants.FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.SIXTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapters extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;

    public ListViewAdapters(Activity activity, ArrayList<HashMap<String, String>> list){
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
        convertView = inflater.inflate(R.layout.colmn_row, null);
        mHolder = new ViewHolder();

        mHolder.txtTranType=(TextView) convertView.findViewById(R.id.tranType);
        mHolder.txtTranDebt=(TextView) convertView.findViewById(R.id.tranDebt);
        mHolder.txtTranCredit=(TextView) convertView.findViewById(R.id.tranCredit);
        mHolder.txtTranDate=(TextView) convertView.findViewById(R.id.tranDate);


        HashMap<String, String> map=list.get(position);

        mHolder.txtTranType.setText(map.get(FIRST_COLUMN));
        mHolder.txtTranType.setTag(map.get(SIXTH_COLUMN));
        mHolder.txtTranDebt.setText(map.get(SECOND_COLUMN));
        mHolder.txtTranDebt.setTag(map.get(FIFTH_COLUMN));
        double Val =Double.parseDouble(map.get(THIRD_COLUMN));
        if (Val == 0)
        {
            mHolder.txtTranCredit.setText(map.get(THIRD_COLUMN));
        } else
        {
            mHolder.txtTranCredit.setText("("+ map.get(THIRD_COLUMN)+ ")");
        }

        mHolder.txtTranDate.setText(map.get(FOURTH_COLUMN));

        convertView.setTag(mHolder);

        return convertView;

    }
    private class ViewHolder {
        TextView txtTranType;
        TextView txtTranDebt;
        TextView txtTranCredit;
        TextView txtTranDate;

    }

}