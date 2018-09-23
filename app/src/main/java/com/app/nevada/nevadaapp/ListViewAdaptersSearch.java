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

public class ListViewAdaptersSearch extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    public Activity activity;

    public ListViewAdaptersSearch(Activity activity, ArrayList<HashMap<String, String>> list){
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
        convertView = inflater.inflate(R.layout.colmn_row_customers_complaints, null);
        mHolder = new ViewHolder();

        mHolder.txtRepOrderNo=(TextView) convertView.findViewById(R.id.txtRepOrderNo);
        mHolder.txtRepCustName=(TextView) convertView.findViewById(R.id.txtRepCustName);
        mHolder.txtRepRate=(TextView) convertView.findViewById(R.id.txtRepRate);
        mHolder.btnOpenDetails=(ImageView) convertView.findViewById(R.id.btnOpenDetails);

        HashMap<String, String> map=list.get(position);
        String t1 =map.get(CUS_THIRD_COLUMN);
        mHolder.txtRepOrderNo.setText(map.get(CUS_FIRST_COLUMN));
        mHolder.txtRepCustName.setText(map.get(CUS_SECOND_COLUMN));
        mHolder.txtRepRate.setText(map.get(CUS_THIRD_COLUMN).concat("/5"));
        mHolder.btnOpenDetails.setTag(map.get(CUS_FOURTH_COLUMN).concat(",&,").concat(map.get(CUS_FIFTH_COLUMN)).concat(",&,").concat(map.get(CUS_SIXTH_COLUMN)).concat(",&,").concat(map.get(CUS_SEVENTH_COLUMN)));

        convertView.setTag(mHolder);

        return convertView;
    }
    class ViewHolder {
        public TextView txtRepOrderNo;
        public TextView txtRepCustName;
        public TextView txtRepRate;
        public ImageView btnOpenDetails;
    }

}