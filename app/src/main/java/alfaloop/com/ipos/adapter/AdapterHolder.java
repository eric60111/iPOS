package alfaloop.com.ipos.adapter;

import android.content.Context;
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


public class AdapterHolder extends BaseAdapter {

    private static String TAG = "AdapterHolder";

    private LayoutInflater myInflater;
    private JSONArray mJsonArray;

    public AdapterHolder(Context context, JSONArray mJsonArray){
        myInflater = LayoutInflater.from(context);
        this.mJsonArray = mJsonArray;
    }

    public void setJSONArray(JSONArray mJsonArray) {
        this.mJsonArray = mJsonArray;
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

        JSONObject tobj = (JSONObject)getItem(position);
        //Log.d(TAG, tobj.toString());
        String ProductName = tobj.keys().next().toString();
        try {
            Name.setText(ProductName);
            Num.setText(String.valueOf(tobj.getJSONObject(ProductName).getInt("數量")));
            if( !tobj.getJSONObject(ProductName).isNull("加料")) {
                Iterator<?> keys = tobj.getJSONObject(ProductName).getJSONObject("加料").keys();
                while(keys.hasNext()) {
                    Addition.append( keys.next().toString() + " " );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
