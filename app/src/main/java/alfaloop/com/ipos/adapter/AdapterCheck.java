package alfaloop.com.ipos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import alfaloop.com.ipos.R;

public class AdapterCheck extends BaseAdapter {


    private LayoutInflater myInflater;
    private JSONArray mJsonArray;
    private int mTotalPrice;

    public AdapterCheck(Context context, JSONArray mJsonArray){
        myInflater = LayoutInflater.from(context);
        this.mJsonArray = mJsonArray;

        //Calucate Total Price
        mTotalPrice = 0;
        for(int i = 0 ; i < mJsonArray.length() ; i++) {
            try {
                JSONObject tJsonObj = (JSONObject)getItem(i);
                //Log.d(TAG, tobj.toString());
                String ProductName = tJsonObj.keys().next().toString();
                int tPrice = 0;

                //Name.setText(ProductName);
                //Num.setText(String.valueOf(tJsonObj.getJSONObject(ProductName).getInt("數量")) );
                Log.d("Test", tJsonObj.getJSONObject(ProductName).toString());
                int tAdditionPrice = 0;
                if( !tJsonObj.getJSONObject(ProductName).isNull("加料")) {
                    Iterator<?> keys = tJsonObj.getJSONObject(ProductName).getJSONObject("加料").keys();
                    while(keys.hasNext()) {
                        tAdditionPrice += tJsonObj.getJSONObject(ProductName).getJSONObject("加料").getInt(keys.next().toString());
                    }
                }
                tPrice = tJsonObj.getJSONObject(ProductName).getInt("數量") * (tJsonObj.getJSONObject(ProductName).getInt("售價")+tAdditionPrice);
                tJsonObj.getJSONObject(ProductName).put("總額",tPrice);
                mTotalPrice += tPrice;

                mJsonArray.put(i, tJsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }
    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mJsonArray.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mJsonArray.optLong(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = myInflater.inflate(R.layout.listview_holder, null);


        TextView Name = (TextView) convertView.findViewById(R.id.tv_HolderName);
        TextView Addition = (TextView) convertView.findViewById(R.id.tv_HolderAddition);
        TextView Num = (TextView) convertView.findViewById(R.id.tv_HolderNum);

        JSONObject tJsonObj = (JSONObject)getItem(position);
        //Log.d(TAG, tobj.toString());
        String ProductName = tJsonObj.keys().next().toString();
        try {
            Name.setText(ProductName);
            Num.setText(String.valueOf(tJsonObj.getJSONObject(ProductName).getInt("數量")) );
            if( !tJsonObj.getJSONObject(ProductName).isNull("加料")) {
                Iterator<?> keys = tJsonObj.getJSONObject(ProductName).getJSONObject("加料").keys();
                while(keys.hasNext()) {
                    Addition.append(keys.next().toString()  + " " );
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }


    public int getPrice(int position) {
        int tPrice = 0;
        try {

            JSONObject tobj = (JSONObject)getItem(position);
            tPrice =tobj.getJSONObject(tobj.keys().next().toString()).getInt("總額");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tPrice;
    }

    public int getTotalPrice() {
        return mTotalPrice;
    }
}
