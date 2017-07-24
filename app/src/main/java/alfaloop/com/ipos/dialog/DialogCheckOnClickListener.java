package alfaloop.com.ipos.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import alfaloop.com.ipos.MainActivity;
import alfaloop.com.ipos.R;
import alfaloop.com.ipos.StickPrinterThread;
import alfaloop.com.ipos.TicketPrinterThread;
import alfaloop.com.ipos.myclass.ClassAddition;
import alfaloop.com.ipos.myclass.ClassOrder;
import alfaloop.com.ipos.myclass.ClassOrderList;
import alfaloop.com.ipos.myclass.ClassProduct;

public class DialogCheckOnClickListener implements View.OnClickListener {

    private static final String TAG = "DialogCheck";

    private EditText et_RealPrice, et_GetPrice, et_PayBackPrice;
    private boolean isOnCheckNumChanged ;
    private JSONArray mJSONArray;
    private Dialog mDialog;

    private MediaPlayer mMediaPlayerOpenMoneyBox;

    private static DatabaseReference myRef ;

    private static SimpleDateFormat mDateFormatter ;
    private static SimpleDateFormat mTimeFormatter ;


    public DialogCheckOnClickListener(Dialog dialog, JSONArray mJSONArray) {

        dialog.findViewById(R.id.btn_Price50).setOnClickListener(this);
        dialog.findViewById(R.id.btn_Price100).setOnClickListener(this);
        dialog.findViewById(R.id.btn_Price500).setOnClickListener(this);
        dialog.findViewById(R.id.btn_Price1000).setOnClickListener(this);

        Button btn_tNumber[] = new Button[10];
        btn_tNumber[0] = (Button)dialog.findViewById(R.id.btn_NUM0);
        btn_tNumber[1] = (Button)dialog.findViewById(R.id.btn_NUM1);
        btn_tNumber[2] = (Button)dialog.findViewById(R.id.btn_NUM2);
        btn_tNumber[3] = (Button)dialog.findViewById(R.id.btn_NUM3);
        btn_tNumber[4] = (Button)dialog.findViewById(R.id.btn_NUM4);
        btn_tNumber[5] = (Button)dialog.findViewById(R.id.btn_NUM5);
        btn_tNumber[6] = (Button)dialog.findViewById(R.id.btn_NUM6);
        btn_tNumber[7] = (Button)dialog.findViewById(R.id.btn_NUM7);
        btn_tNumber[8] = (Button)dialog.findViewById(R.id.btn_NUM8);
        btn_tNumber[9] = (Button)dialog.findViewById(R.id.btn_NUM9);

        for (Button aBtn_tNumber : btn_tNumber) {
            aBtn_tNumber.setOnClickListener(this);
        }

        dialog.findViewById(R.id.btn_AC).setOnClickListener(this);


        dialog.findViewById(R.id.btn_In).setOnClickListener(this);
        dialog.findViewById(R.id.btn_Out).setOnClickListener(this);
        dialog.findViewById(R.id.btn_Deliver).setOnClickListener(this);

        //LayoutInflater myInflater = LayoutInflater.from(context);
        this.mJSONArray = mJSONArray;
        et_RealPrice = (EditText) dialog.findViewById(R.id.et_RealPrice);
        et_GetPrice = (EditText) dialog.findViewById(R.id.et_GetPrice);
        et_PayBackPrice = (EditText) dialog.findViewById(R.id.et_PayBackPrice);
        isOnCheckNumChanged = false;
        this.mDialog = dialog;


        mDateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        mTimeFormatter = new SimpleDateFormat("HH時mm分ss秒");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).child(mDateFormatter.format(new Date())).child(MainActivity.tv_SerialNumber.getText().toString());

        //mTicketPrinterThread = new TicketPrinterThread();

        mMediaPlayerOpenMoneyBox = MediaPlayer.create(dialog.getContext(), R.raw.openmoneybox);
    }

    @Override
    public void onClick(View v) {

        Button tBtn = (Button)v;

        switch (v.getId()) {
            case R.id.btn_AC:
                et_GetPrice.setText(et_RealPrice.getText().toString());
                isOnCheckNumChanged = false;
                break;

            case R.id.btn_NUM0:
                if(isOnCheckNumChanged){
                    et_GetPrice.append(tBtn.getText().toString());
                }
                break;

            case R.id.btn_NUM1:
            case R.id.btn_NUM2:
            case R.id.btn_NUM3:
            case R.id.btn_NUM4:
            case R.id.btn_NUM5:
            case R.id.btn_NUM6:
            case R.id.btn_NUM7:
            case R.id.btn_NUM8:
            case R.id.btn_NUM9:
                if(isOnCheckNumChanged)
                    et_GetPrice.append(tBtn.getText().toString());
                else{
                    et_GetPrice.setText(tBtn.getText().toString());
                    isOnCheckNumChanged = true;
                }
                break;

            case R.id.btn_Price50:
            case R.id.btn_Price100:
            case R.id.btn_Price500:
            case R.id.btn_Price1000:
                et_GetPrice.setText(tBtn.getText().toString());
                isOnCheckNumChanged = false;
                break;

            case R.id.btn_Deliver:
            case R.id.btn_In:
            case R.id.btn_Out:

                MainActivity.mJsonObjHoldAddition = new JSONObject();
                mMediaPlayerOpenMoneyBox.start();

                String tArea = MainActivity.mAreaName;
                String tDate = mDateFormatter.format(new Date());
                String tTime = mTimeFormatter.format(new Date());
                String SerialNumber = MainActivity.tv_SerialNumber.getText().toString();

                // Write a message to the database
                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DatabaseReference myRef = database.getReference("訂單/" + tArea + "/" + tDate + "/" + SerialNumber );

                try {

                    ClassOrderList mClassOrderList = new ClassOrderList();
                    JSONArray tJSONOrderList = new JSONArray();

                    int TotalPrice = 0;
                    for(int i = 0 ; i < mJSONArray.length() ; i++) {
                        String keyName = mJSONArray.getJSONObject(i).keys().next().toString();

                        ClassAddition mClassAddition = new ClassAddition();

                        Iterator<?> tAdditionKeys = mJSONArray.getJSONObject(i).getJSONObject(keyName).getJSONObject("加料").keys();
                        while (tAdditionKeys.hasNext()) {
                            String tAdditionName = tAdditionKeys.next().toString();
                            int tAdditionPrice = mJSONArray.getJSONObject(i).getJSONObject(keyName).getJSONObject("加料").getInt(tAdditionName);
                            mClassAddition.setMap(tAdditionName,tAdditionPrice);
                        }


                        TotalPrice += mJSONArray.getJSONObject(i).getJSONObject(keyName).getInt("總額");

                        JSONObject tJSONProduct = new JSONObject();

                        tJSONProduct.put("名稱", keyName);
                        tJSONProduct.put("售價", mJSONArray.getJSONObject(i).getJSONObject(keyName).getInt("售價"));
                        tJSONProduct.put("數量", mJSONArray.getJSONObject(i).getJSONObject(keyName).getInt("數量"));
                        tJSONProduct.put("總額", mJSONArray.getJSONObject(i).getJSONObject(keyName).getInt("總額"));
                        tJSONProduct.put("加料", mJSONArray.getJSONObject(i).getJSONObject(keyName).getJSONObject("加料"));
                        tJSONProduct.put("狀態", "未完成");
                        tJSONOrderList.put(tJSONProduct);

                        mClassOrderList.setMap( "序號" + i,
                                new ClassProduct(keyName,
                                    mJSONArray.getJSONObject(i).getJSONObject(keyName).getInt("售價"),
                                    mJSONArray.getJSONObject(i).getJSONObject(keyName).getInt("數量"),
                                    mJSONArray.getJSONObject(i).getJSONObject(keyName).getInt("總額"),
                                    "未完成",
                                    mClassAddition.toMap()
                                ).toMap()
                        );
                    }
                    ClassOrder mClassOrder = new ClassOrder();
                    mClassOrder.setMap( mTimeFormatter.format(new Date()), tBtn.getText().toString(), Integer.parseInt(et_RealPrice.getText().toString()), TotalPrice, mClassOrderList.toMap());

                    JSONObject mJSONOrder = new JSONObject();
                    mJSONOrder.put("時間", mTimeFormatter.format(new Date()));
                    mJSONOrder.put("用餐方式", tBtn.getText().toString());
                    mJSONOrder.put("實收總額", Integer.parseInt(et_RealPrice.getText().toString()));
                    mJSONOrder.put("銷售總額", TotalPrice);
                    mJSONOrder.put("訂單明細", tJSONOrderList);


                    if(v.getId() == R.id.btn_Deliver) {
                        Dialog CellPhoneNumberDialog = new Dialog(mDialog.getContext(), R.style.MyDialog);
                        CellPhoneNumberDialog.setContentView(R.layout.dialog_phone);
                        new CellPhoneCheckOnClickListener(mClassOrder, mJSONOrder, CellPhoneNumberDialog, mDialog);
                        CellPhoneNumberDialog.show();
                    }else {

                        myRef.setValue(mClassOrder.toMap());

                        if(MainActivity.isTicketPrintEnable)
                            new TicketPrinterThread(String.format("%s %s", tDate, tTime), SerialNumber, mJSONOrder).start();
                        if(MainActivity.isStickPrintEnable && tBtn.getId() != R.id.btn_In)
                            new StickPrinterThread(String.format("%s %s", tDate, tTime), SerialNumber, mJSONOrder).start();

                        mDialog.dismiss();

                        MainActivity.mJsonObjAddition = new JSONObject();
                        MainActivity.mJsonArrOrder = new JSONArray();
                        MainActivity.mAdapterHolder.setJSONArray(MainActivity.mJsonArrOrder);
                        MainActivity.mAdapterHolder.notifyDataSetChanged();


                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
        }

        int PayBackPrice = (Integer.parseInt(et_RealPrice.getText().toString()) - Integer.parseInt(et_GetPrice.getText().toString()))*-1;
        et_PayBackPrice.setText( String.valueOf(PayBackPrice) );

        if(PayBackPrice > 0 ) {
            et_PayBackPrice.setTextColor(Color.BLACK);
        }else if(PayBackPrice == 0 ) {
            et_PayBackPrice.setTextColor(Color.GREEN);
        }else{
            et_PayBackPrice.setTextColor(Color.RED);
        }
    }

    public class CellPhoneCheckOnClickListener implements View.OnClickListener {

        private TextView tv_CellPhoneNumber ;
        private Dialog mDialog, mMainDialog;
        private ClassOrder mClassOrder;
        private JSONObject mJSONOrder;
        private boolean isOnCellPhoneNumberChanged;

        public CellPhoneCheckOnClickListener(ClassOrder mClassOrder, JSONObject mJSONOrder, Dialog mDialog, Dialog mMainDialog) {
            setButtonListener(mDialog);
            this.tv_CellPhoneNumber = (TextView) mDialog.findViewById(R.id.tv_CellPhoneNumber);

            this.isOnCellPhoneNumberChanged = false;
            this.mClassOrder = mClassOrder;
            this.mJSONOrder = mJSONOrder;
            this.mDialog = mDialog;
            this.mMainDialog = mMainDialog;
        }

        private void setButtonListener(Dialog mDialog) {
            Button btn_tNumber[] = new Button[10];
            btn_tNumber[0] = (Button)mDialog.findViewById(R.id.btn_NUM0);
            btn_tNumber[1] = (Button)mDialog.findViewById(R.id.btn_NUM1);
            btn_tNumber[2] = (Button)mDialog.findViewById(R.id.btn_NUM2);
            btn_tNumber[3] = (Button)mDialog.findViewById(R.id.btn_NUM3);
            btn_tNumber[4] = (Button)mDialog.findViewById(R.id.btn_NUM4);
            btn_tNumber[5] = (Button)mDialog.findViewById(R.id.btn_NUM5);
            btn_tNumber[6] = (Button)mDialog.findViewById(R.id.btn_NUM6);
            btn_tNumber[7] = (Button)mDialog.findViewById(R.id.btn_NUM7);
            btn_tNumber[8] = (Button)mDialog.findViewById(R.id.btn_NUM8);
            btn_tNumber[9] = (Button)mDialog.findViewById(R.id.btn_NUM9);

            for (Button aBtn_tNumber : btn_tNumber) {
                aBtn_tNumber.setOnClickListener(this);
            }

            Button btn_tDel = (Button)mDialog.findViewById(R.id.btn_Del);
            Button btn_tEnter = (Button)mDialog.findViewById(R.id.btn_Enter);
            Button btn_tClear = (Button)mDialog.findViewById(R.id.btn_Clear);

            btn_tDel.setOnClickListener(this);
            btn_tEnter.setOnClickListener(this);
            btn_tClear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Button tBtn = (Button)v;
            switch(v.getId()) {
                case R.id.btn_Enter:
                    //String tArea = MainActivity.mAreaName;
                    String tDate = mDateFormatter.format(new Date());
                    String tTime = mTimeFormatter.format(new Date());
                    String SerialNumber = MainActivity.tv_SerialNumber.getText().toString();


                    mClassOrder.addCellPhoneNumber(tv_CellPhoneNumber.getText().toString());
                    myRef.setValue(mClassOrder.toMap());

                    if(MainActivity.isTicketPrintEnable)
                        new TicketPrinterThread( String.format("%s %s", tDate, tTime), SerialNumber, mJSONOrder).start();
                    if(MainActivity.isStickPrintEnable)
                        new StickPrinterThread(String.format("%s %s", tDate, tTime), SerialNumber, mJSONOrder).start();


                    mDialog.dismiss();
                    mMainDialog.dismiss();

                    MainActivity.mJsonObjAddition = new JSONObject();
                    MainActivity.mJsonArrOrder = new JSONArray();
                    MainActivity.mAdapterHolder.setJSONArray(MainActivity.mJsonArrOrder);
                    MainActivity.mAdapterHolder.notifyDataSetChanged();

                    break;

                case R.id.btn_Clear:
                    tv_CellPhoneNumber.setText("09");

                    break;
                case R.id.btn_Del:
                    if(tv_CellPhoneNumber.getText().toString().length() > 2 ) {
                        tv_CellPhoneNumber.setText(tv_CellPhoneNumber.getText().toString().substring(0,tv_CellPhoneNumber.getText().toString().length()-1));
                    }

                    break;
                case R.id.btn_NUM0:
                case R.id.btn_NUM1:
                case R.id.btn_NUM2:
                case R.id.btn_NUM3:
                case R.id.btn_NUM4:
                case R.id.btn_NUM5:
                case R.id.btn_NUM6:
                case R.id.btn_NUM7:
                case R.id.btn_NUM8:
                case R.id.btn_NUM9:
                    if(isOnCellPhoneNumberChanged)
                        tv_CellPhoneNumber.append(tBtn.getText().toString());
                    else{
                        tv_CellPhoneNumber.setText("09"+tBtn.getText().toString());
                        isOnCellPhoneNumberChanged = true;
                    }
                    break;
            }
        }
    }
}
