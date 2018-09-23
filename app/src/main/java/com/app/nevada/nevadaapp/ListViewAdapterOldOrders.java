
package com.app.nevada.nevadaapp;

        import android.app.Activity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.HashMap;

        import static com.app.nevada.nevadaapp.Constants.LT_FIRST_COLUMN;
        import static com.app.nevada.nevadaapp.Constants.LT_SECOND_COLUMN;
        import static com.app.nevada.nevadaapp.Constants.LT_THIRD_COLUMN;
        import static com.app.nevada.nevadaapp.Constants.LT_FOURTH_COLUMN;

/**
 * Created by Mothana on 19/02/2017.
 */

public class ListViewAdapterOldOrders extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    public Activity activity;

    public ListViewAdapterOldOrders(Activity activity, ArrayList<HashMap<String, String>> list){
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
        final ListViewAdapterOldOrders.ViewHolder mHolder;
        LayoutInflater inflater=activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.colmn_row_old_orders, null);
        mHolder = new ListViewAdapterOldOrders.ViewHolder();

        mHolder.txtOrderNo=(TextView) convertView.findViewById(R.id.lblOrderNo);
        mHolder.txtOrderItem=(TextView) convertView.findViewById(R.id.lblOrderItem);
        mHolder.txtOrderQty=(TextView) convertView.findViewById(R.id.lblOrderQty);
        mHolder.txtOrderDate=(TextView) convertView.findViewById(R.id.lblOrderDate);

        HashMap<String, String> map=list.get(position);
        mHolder.txtOrderNo.setText(map.get(LT_FIRST_COLUMN));
        mHolder.txtOrderItem.setText(map.get(LT_SECOND_COLUMN));
        mHolder.txtOrderQty.setText(map.get(LT_THIRD_COLUMN));
        mHolder.txtOrderDate.setText(map.get(LT_FOURTH_COLUMN));

        convertView.setTag(mHolder);

        return convertView;
    }
    private class ViewHolder {
        public TextView txtOrderNo;
        public TextView txtOrderItem;
        public TextView txtOrderQty;
        public TextView txtOrderDate;

    }

}
