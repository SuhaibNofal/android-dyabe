package com.app.nevada.nevadaapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.app.nevada.nevadaapp.Constants.LT_FIFTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FIRST_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_FOURTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SECOND_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_SIXTH_COLUMN;
import static com.app.nevada.nevadaapp.Constants.LT_THIRD_COLUMN;

/**
 * Created by Mothana on 19/02/2017.
 */

public class ListViewAdapterCurrentOrders extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;

    String language;
    public Activity activity;

    public ListViewAdapterCurrentOrders(Activity activity, ArrayList<HashMap<String, String>> list,String lang) {
        super();
        this.activity = activity;
        this.list = list;
        this.language=lang;
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
        final ListViewAdapterCurrentOrders.ViewHolder mHolder;
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.colmn_row_current_orders, null);
        mHolder = new ListViewAdapterCurrentOrders.ViewHolder();

        mHolder.txtCurrentOrderNo = (TextView) convertView.findViewById(R.id.lblCurrentOrderNo);
        mHolder.txtCusName = (TextView) convertView.findViewById(R.id.lblCusName);
        mHolder.btnCusLocation = (Button) convertView.findViewById(R.id.btnCusLocation);

        HashMap<String, String> map = list.get(position);
        boolean isOnroad = Boolean.parseBoolean(map.get(LT_FIFTH_COLUMN));
        if (isOnroad) {
            convertView.setBackgroundColor(Color.RED);
        }
        mHolder.txtCurrentOrderNo.setText(map.get(LT_FIRST_COLUMN));//map.get(LT_FIRST_COLUMN));
        mHolder.txtCusName.setText(map.get(LT_SECOND_COLUMN));
        mHolder.btnCusLocation.setTag(map.get(LT_THIRD_COLUMN).concat(",").concat(String.valueOf(map.get(LT_SECOND_COLUMN))).concat(",").concat(String.valueOf(map.get(LT_FIRST_COLUMN)))
                .concat((",").concat((String.valueOf(map.get(LT_FOURTH_COLUMN))))).concat((",").concat((String.valueOf(map.get(LT_FIFTH_COLUMN))))).concat((",").concat((String.valueOf(map.get(LT_SIXTH_COLUMN)))))
                .concat((",").concat(String.valueOf(position))).concat(",").concat((String.valueOf(map.get("VarStatus")))));
        if (language.contains("en")) {
            mHolder.btnCusLocation.setTextSize(10);
            switch (String.valueOf(map.get("VarStatus"))) {
                case "0":
                    mHolder.btnCusLocation.setText("start");
                    break;
                case "1":
                    mHolder.btnCusLocation.setText("finish loading ");
                    break;
                case "2":
                    mHolder.btnCusLocation.setText("location");
                    break;
                case "3":
                    mHolder.btnCusLocation.setText("ending");
                    break;
                case "7":
                    mHolder.btnCusLocation.setText("wait customer ");
                    break;
            }
        } else if(!language.contains("en")){
            mHolder.btnCusLocation.setTextSize(10);
            switch (String.valueOf(map.get("VarStatus"))) {
                case "0":
                    mHolder.btnCusLocation.setText("بدء الرحلة");
                    break;
                case "1":
                    mHolder.btnCusLocation.setText("تم التحميل ");
                    break;
                case "2":
                    mHolder.btnCusLocation.setText("موقع العميل");
                    break;
                case "3":
                    mHolder.btnCusLocation.setText("انهاء");
                    break;
                case "7":
                    mHolder.btnCusLocation.setText("في انتظار عميل ");
                    break;
            }
        }




        convertView.setTag(mHolder);

        return convertView;
    }

    private class ViewHolder {
        public TextView txtCurrentOrderNo;
        public TextView txtCusName;
        public Button btnCusLocation;


    }

}
