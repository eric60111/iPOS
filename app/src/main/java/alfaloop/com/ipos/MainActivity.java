package alfaloop.com.ipos;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import alfaloop.com.ipos.adapter.AdapterCheck;
import alfaloop.com.ipos.adapter.AdapterHolder;
import alfaloop.com.ipos.dialog.DialogCheckOnClickListener;
import alfaloop.com.ipos.dialog.DialogSettingOnCheckedChangedListener;
import alfaloop.com.ipos.dialog.DialogSettingOnClickListener;


public class MainActivity extends Activity implements  View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity" ;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static AdapterHolder mAdapterHolder;
    public static DataSnapshot mDataSnapshot;
    private static JSONObject mJsonObjProduct ;

    private static boolean isOnOrderNumChanged ;
    public static Button onClickTypeButton;
    public static JSONObject mJsonObjHoldAddition, mJsonObjAddition, mJsonObjReminder;
    public static JSONArray mJsonArrOrder ;

    private static ListView lv_HolderList;
    private static LinearLayout ll_ProductType, ll_ProductItem, ll_AdditionOther, ll_Reminder, ll_Remark, ll_Remark2;
    private static EditText et_HoldProductName, et_HoldProductAddition, et_HoldProductNum;
    private static Button btn_AC, btn_Clear, btn_Enter, btn_DEL, btn_CLS, btn_CHECK, btn_SET, btn_FREE;
    //private static Spinner sp_StoreName, sp_AreaName, sp_Employee;
    public static TextView tv_Area, tv_Personnel, tv_SerialNumber;
    //private static ToggleButton tb_Addition1, tb_Addition2, tb_Addition3, tb_Addition4, tb_BigSize, tb_LHot, tb_MHot, tb_SHot;
    private static Button btn_Number[];
    private static int btn_NumberID[] = {R.id.btn_NUM0, R.id.btn_NUM1 , R.id.btn_NUM2, R.id.btn_NUM3, R.id.btn_NUM4, R.id.btn_NUM5, R.id.btn_NUM6, R.id.btn_NUM7, R.id.btn_NUM8, R.id.btn_NUM9};
    private static ToggleButton tb_Remark[], tb_AdditionOther[], tb_ReminderOther[];
    private static int tb_AdditionID[] = {R.id.tb_BigSize, R.id.tb_LHot, R.id.tb_MHot, R.id.tb_SHot};

    public static int NUM_PRE_LINE;
    public static String mStoreName, mAreaName;
    public static JSONObject mStoreJsonData;
    public static String mTicketPrinterIP, mStickPrinterIP;
    public static boolean isTicketPrintEnable, isStickPrintEnable, isPriceDivEnable;
    //public static int mPageNum;
    private static Dialog mSettingDialog, mAdditionDialog, mRemindDialog, mCheckRecordDialog;

    private static String mRFIDReadNumber;

    public static FirebaseDatabase database;
    private static SimpleDateFormat mDateFormatter, mMonthFormatter, mTimeFormatter ;

    public static String STICK_MODEL ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //取得SharedPreference設定("Preference"為設定檔的名稱)
        SharedPreferences settings = getSharedPreferences("iPOS", 0);
        //取出name屬性的字串
        mStoreName = settings.getString("店名", "未指定");
        mAreaName = settings.getString("分店", "未知");
        mTicketPrinterIP = settings.getString("出單機IP", "192.168.123.100");
        mStickPrinterIP = settings.getString("貼紙機IP", "192.168.123.101");
        STICK_MODEL = settings.getString("貼紙機型號", "GoDEX");
        isTicketPrintEnable = settings.getBoolean("出單機開關", false);
        isStickPrintEnable = settings.getBoolean("貼紙機開關", false);
        NUM_PRE_LINE = settings.getInt("每行顯示數量", 3);
        isPriceDivEnable = settings.getBoolean("價格分類顯示", false);

        Init();
        AdapterInit();
        ViewInit();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                database = FirebaseDatabase.getInstance();
                /** 查資料庫全部資料**/
                database.getReference().addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                mDataSnapshot = dataSnapshot;
                                SettingDialogInit();
                                btn_SET.setEnabled(true);
                                if(mStoreName.equals("未指定") || mAreaName.equals("未知")) {
                                    mSettingDialogShow(mDataSnapshot);
                                }else{
                                    ParserData();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        }
                );

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void Init() {
        mDateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        mMonthFormatter = new SimpleDateFormat("yyyy/MM");
        mTimeFormatter = new SimpleDateFormat("HH時mm分ss秒");
        mJsonArrOrder = new JSONArray();
        mJsonObjHoldAddition = new JSONObject();

        mAdditionDialog = new Dialog(MainActivity.this, R.style.MyDialog);
        mAdditionDialog.setContentView(R.layout.dialog_addition);

        mRemindDialog = new Dialog(MainActivity.this, R.style.MyDialog);
        mRemindDialog.setContentView(R.layout.dialog_remind);

        mRFIDReadNumber = "";
    }


    private void SettingDialogInit() {
        mSettingDialog = new Dialog(MainActivity.this,R.style.MyDialog);
        mSettingDialog.setContentView(R.layout.dialog_setting);
        mSettingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ParserData();
            }
        });

        ((EditText)mSettingDialog.findViewById(R.id.et_TicketIP)).setText(mTicketPrinterIP);
        ((EditText)mSettingDialog.findViewById(R.id.et_StickIP)).setText(mStickPrinterIP);

        //TextView
        ((TextView)mSettingDialog.findViewById(R.id.tv_DayVolume)).setText(mDateFormatter.format(new Date()));
        ((TextView)mSettingDialog.findViewById(R.id.tv_MonthVolume)).setText(mMonthFormatter.format(new Date()));
        ((TextView)mSettingDialog.findViewById(R.id.tv_RecordMonth)).setText(mMonthFormatter.format(new Date()));

        ((EditText)mSettingDialog.findViewById(R.id.et_StickIP)).setEnabled(isStickPrintEnable);
        ((CheckBox)mSettingDialog.findViewById(R.id.cb_StickEnable)).setChecked(isStickPrintEnable);
        ((EditText)mSettingDialog.findViewById(R.id.et_TicketIP)).setEnabled(isTicketPrintEnable);
        ((CheckBox)mSettingDialog.findViewById(R.id.cb_TicketEnable)).setChecked(isTicketPrintEnable);
        ((CheckBox)mSettingDialog.findViewById(R.id.cb_PriceDiv)).setChecked(isPriceDivEnable);

        new DialogSettingOnClickListener(mSettingDialog);
        new DialogSettingOnCheckedChangedListener(mSettingDialog) ;



    }

    public void mSettingDialogShow(final DataSnapshot tDataSnapshot) {
        mSettingDialog.findViewById(R.id.btn_StoreDel).setEnabled(false);
        mSettingDialog.findViewById(R.id.btn_AreaDel).setEnabled(false);
        mSettingDialog.findViewById(R.id.btn_RecordMonthPicker).setEnabled(false);
        mSettingDialog.findViewById(R.id.btn_RecordSearch).setEnabled(false);
        mSettingDialog.findViewById(R.id.btn_Exit).setEnabled(false);

        //跳出設定選單

        ArrayAdapter<String> tAdapterStoreName;
        List<String> tListStoreName;

        int tListStoreNameSelection = 0;
        tListStoreName = new ArrayList<String>();
        tListStoreName.add("請選擇");
        Iterator<DataSnapshot> tStoreKeys = tDataSnapshot.getChildren().iterator();
        int tStoreNameIndex = 0;
        while(tStoreKeys.hasNext()) {
            String tStoreName = tStoreKeys.next().getKey();
            tListStoreName.add(tStoreName);
            tStoreNameIndex++;
            if(tStoreName.equals(mStoreName)) {
                tListStoreNameSelection = tStoreNameIndex;
            }
        }


        tAdapterStoreName = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListStoreName);
        tAdapterStoreName.setDropDownViewResource(R.layout.myspinner);
        ((Spinner)mSettingDialog.findViewById(R.id.sp_StoreName)).setAdapter(tAdapterStoreName);
        ((Spinner)mSettingDialog.findViewById(R.id.sp_StoreName)).setSelection(tListStoreNameSelection);

        ((Spinner)mSettingDialog.findViewById(R.id.sp_StoreName)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView)view).getText().toString();

                ArrayAdapter<String> tAdapterAreaName;
                List<String> tListAreaName = new ArrayList<String>();

                ArrayAdapter<String> tAdapterEmployeeName;
                List<String> tListEmployeeName = new ArrayList<String>();


                if(text.equals("請選擇")) {
                    mSettingDialog.findViewById(R.id.btn_Exit).setEnabled(false);
                    mSettingDialog.findViewById(R.id.btn_StoreDel).setEnabled(false);

                    tAdapterAreaName = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListAreaName);
                    tAdapterAreaName.setDropDownViewResource(R.layout.myspinner);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_AreaName)).setAdapter(tAdapterAreaName);
                    mSettingDialog.findViewById(R.id.btn_AreaDel).setEnabled(false);

                    tAdapterEmployeeName = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListEmployeeName);
                    tAdapterEmployeeName.setDropDownViewResource(R.layout.myspinner);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_Employee)).setAdapter(tAdapterEmployeeName);
                    mSettingDialog.findViewById(R.id.btn_RecordSearch).setEnabled(false);
                    mSettingDialog.findViewById(R.id.btn_RecordMonthPicker).setEnabled(false);

                }else{
                    mSettingDialog.findViewById(R.id.btn_StoreDel).setEnabled(true);

                    int tListAreaNameSelection = 0;
                    //分店資料查詢
                    tListAreaName.add("請選擇");

                    Iterator<DataSnapshot> tAreaKeys = tDataSnapshot.child(text).child("分店資訊").getChildren().iterator();
                    int tListAreaNameIndex = 0;
                    while(tAreaKeys.hasNext()) {
                        String tAreaName = tAreaKeys.next().getKey();
                        tListAreaName.add(tAreaName);
                        tListAreaNameIndex ++;
                        if(tAreaName.equals(mAreaName)) {
                            tListAreaNameSelection = tListAreaNameIndex;
                        }
                    }

                    tAdapterAreaName = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListAreaName);
                    tAdapterAreaName.setDropDownViewResource(R.layout.myspinner);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_AreaName)).setAdapter(tAdapterAreaName);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_AreaName)).setSelection(tListAreaNameSelection);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_AreaName)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            String text = ((TextView)view).getText().toString();
                            //Log.d(TAG, text);
                            if(text.equals("請選擇")) {
                                mSettingDialog.findViewById(R.id.btn_Exit).setEnabled(false);
                                mSettingDialog.findViewById(R.id.btn_AreaDel).setEnabled(false);
                            }else{
                                mSettingDialog.findViewById(R.id.btn_Exit).setEnabled(true);
                                mSettingDialog.findViewById(R.id.btn_AreaDel).setEnabled(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {  }
                    });


                    //員工資料查詢
                    tListEmployeeName.add("請選擇");
                    for (DataSnapshot dataSnapshot : tDataSnapshot.child(text).child("員工").getChildren())
                        tListEmployeeName.add(dataSnapshot.getKey());

                    tAdapterEmployeeName = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListEmployeeName);
                    tAdapterEmployeeName.setDropDownViewResource(R.layout.myspinner);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_Employee)).setAdapter(tAdapterEmployeeName);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_Employee)).setSelection(0);
                    ((Spinner)mSettingDialog.findViewById(R.id.sp_Employee)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(((TextView)view).getText().toString().equals("請選擇")) {
                                mSettingDialog.findViewById(R.id.btn_RecordMonthPicker).setEnabled(false);
                                mSettingDialog.findViewById(R.id.btn_RecordSearch).setEnabled(false);
                            }else{
                                mSettingDialog.findViewById(R.id.btn_RecordMonthPicker).setEnabled(true);
                                mSettingDialog.findViewById(R.id.btn_RecordSearch).setEnabled(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {  }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        mSettingDialog.setCanceledOnTouchOutside(false);
        mSettingDialog.show();
    }

    private void ViewInit() {
        lv_HolderList = (ListView)findViewById(R.id.lv_HolderList);
        lv_HolderList.setAdapter(mAdapterHolder);
        lv_HolderList.setChoiceMode(View.FOCUSABLES_TOUCH_MODE);
        lv_HolderList.setOnItemClickListener(this);

        tv_Area = (TextView)findViewById(R.id.tv_Area);
        tv_Area.setText(mAreaName);
        tv_Personnel = (TextView)findViewById(R.id.tv_Personnel);
        tv_SerialNumber = (TextView)findViewById(R.id.tv_SerialNumber);

        ll_ProductType = (LinearLayout)findViewById(R.id.ll_ProductType);
        ll_ProductItem = (LinearLayout)findViewById(R.id.ll_ProductItem);
        ll_AdditionOther = (LinearLayout)findViewById(R.id.ll_AdditionOther);
        ll_Reminder = (LinearLayout)findViewById(R.id.ll_Remind);
        ll_Remark = (LinearLayout)findViewById(R.id.ll_Remark);
        ll_Remark2 = (LinearLayout)findViewById(R.id.ll_Remark2);

        et_HoldProductName = (EditText)findViewById(R.id.et_HoldProductName);
        et_HoldProductNum = (EditText)findViewById(R.id.et_HoldProductNum);
        et_HoldProductAddition = (EditText)findViewById(R.id.et_HoldProductAddition);

        btn_Number = new Button[10];
        for(int i = 0 ; i < btn_Number.length ; i ++) {
            btn_Number[i] = (Button)findViewById(btn_NumberID[i]);
            btn_Number[i].setOnClickListener(this);
        }

        btn_AC = (Button)findViewById(R.id.btn_AC);
        btn_Clear = (Button)findViewById(R.id.btn_Clear);

        btn_AC.setOnClickListener(this);
        btn_Clear.setOnClickListener(this);
        btn_Enter = (Button)findViewById(R.id.btn_Enter);
        btn_Enter.setOnClickListener(this);

        tb_Remark = new ToggleButton[4];
        for (int i = 0 ; i < tb_Remark.length ; i++) {
            tb_Remark[i] = (ToggleButton)findViewById(tb_AdditionID[i]);
            tb_Remark[i].setOnCheckedChangeListener(this);
        }


        btn_DEL = (Button)findViewById(R.id.btn_DEL);
        btn_CLS = (Button)findViewById(R.id.btn_CLS);
        btn_CHECK = (Button)findViewById(R.id.btn_CHECK);
        btn_SET = (Button)findViewById(R.id.btn_SET);
        btn_FREE = (Button)findViewById(R.id.btn_FREE);

        btn_DEL.setOnClickListener(this);
        btn_CLS.setOnClickListener(this);
        btn_CHECK.setOnClickListener(this);
        btn_SET.setOnClickListener(this);
        btn_FREE.setOnClickListener(this);


        mCheckRecordDialog = new Dialog(MainActivity.this);
        mCheckRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCheckRecordDialog.setContentView(R.layout.dialog_employee_record);
        mCheckRecordDialog.setCanceledOnTouchOutside(false);
        mCheckRecordDialog.findViewById(R.id.btn_onWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tEmployeeName = ((TextView)mCheckRecordDialog.findViewById(R.id.tv_Employee)).getText().toString();
                String tCheckTime = mTimeFormatter.format(new Date());
                mDataSnapshot.getRef().child(mStoreName).child("員工").child(tEmployeeName).child("打卡紀錄").child(mDateFormatter.format(new Date())).child("上班").child(tCheckTime).setValue(mAreaName);
                Toast.makeText(v.getContext(), String.format("%s 上班(%S)打卡", tEmployeeName, tCheckTime), Toast.LENGTH_LONG).show();
                mCheckRecordDialog.dismiss();
            }
        });
        mCheckRecordDialog.findViewById(R.id.btn_offWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tEmployeeName = ((TextView)mCheckRecordDialog.findViewById(R.id.tv_Employee)).getText().toString();
                String tCheckTime = mTimeFormatter.format(new Date());
                mDataSnapshot.getRef().child(mStoreName).child("員工").child(tEmployeeName).child("打卡紀錄").child(mDateFormatter.format(new Date())).child("下班").child(tCheckTime).setValue(mAreaName);
                Toast.makeText(v.getContext(), String.format("%s 下班(%S)打卡", tEmployeeName, tCheckTime), Toast.LENGTH_LONG).show();
                mCheckRecordDialog.dismiss();
            }
        });
    }

    private void AdapterInit() {
        mAdapterHolder = new AdapterHolder(MainActivity.this, mJsonArrOrder);
        mAdapterHolder.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //Log.d(TAG, mAdapterHolder.getCount() + "");
                HolderListOnSelectedView = null;
                if(mAdapterHolder.getCount() > 0 ) {
                    btn_control_enable(true);
                    btn_DEL.setEnabled(false);

                }else{
                    btn_control_enable(false);
                }

            }
        });
    }


    private void btn_number_enable(boolean value){

        for (Button aBtn_Number : btn_Number) {
            aBtn_Number.setEnabled(value);
        }

        btn_AC.setEnabled(value);
        btn_Clear.setEnabled(value);
    }

    private void btn_addition_enable(boolean value) {
        for (ToggleButton aTb_Remark : tb_Remark) {
            aTb_Remark.setChecked(false);
            aTb_Remark.setEnabled(value);
        }

        for(ToggleButton aTb_AdditionOther :tb_AdditionOther) {
            aTb_AdditionOther.setChecked(false);
            aTb_AdditionOther.setEnabled(value);
            //Log.d(TAG, bTb_Addition.getText().toString() + ":" + value);
        }

        for(ToggleButton aTb_ReminderOther :tb_ReminderOther) {
            aTb_ReminderOther.setChecked(false);
            aTb_ReminderOther.setEnabled(value);
            //Log.d(TAG, bTb_Addition.getText().toString() + ":" + value);
        }

        btn_Enter.setEnabled(value);
    }

    private void btn_control_enable(boolean value) {
        btn_DEL.setEnabled(value);
        btn_CLS.setEnabled(value);
        btn_CHECK.setEnabled(value);
        btn_FREE.setEnabled(value);
    }

    private void BottomBar_enable(boolean value) {
        isOnOrderNumChanged = false;
        mJsonObjHoldAddition = new JSONObject();
        et_HoldProductName.setText("");
        et_HoldProductAddition.setText("");
        et_HoldProductNum.setText("1");

        btn_number_enable(value);
        btn_addition_enable(value);
    }

    private Button CreateButton(String Text, int Resource) {
        Button btn = new Button(getApplicationContext());
        btn.setText(Text);
        btn.setBackgroundResource(Resource);
        btn.setTextAppearance(getApplicationContext(), R.style.ButtonText);
        btn.setHeight(80);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.setMargins(5,5,5,5);

        btn.setLayoutParams(layoutParams );
        if(Text.equals(""))
            btn.setEnabled(false);

        return btn;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getAction()) {
            case KeyEvent.ACTION_UP:


                //Log.d(TAG, mRFIDReadNumber);
                if (event.getKeyCode() == 66) { //ENTER {

                    if(mDataSnapshot.child(mStoreName).hasChild("員工")) {

                        for (DataSnapshot dataSnapshot : mDataSnapshot.child(mStoreName).child("員工").getChildren()) {
                            String tEmployeeName = dataSnapshot.getKey();
                            if (mDataSnapshot.child(mStoreName).child("員工").child(tEmployeeName).child("編號").getValue().toString().equals(mRFIDReadNumber)) {
                                Toast.makeText(getApplicationContext(), "(" + mRFIDReadNumber + ")" + tEmployeeName, Toast.LENGTH_SHORT).show();
                                ((TextView) mCheckRecordDialog.findViewById(R.id.tv_Employee)).setText(tEmployeeName);
                                mCheckRecordDialog.show();
                                mRFIDReadNumber = "";
                                break;
                            }
                        }
                    }


                }else{
                    mRFIDReadNumber += String.valueOf(event.getKeyCode()-7);
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {

        Button tBtn = (Button) v;

        switch(v.getId()) {
            case R.id.btn_CLS:
                //mJsonObjHoldAddition = new JSONObject();
                mJsonArrOrder = new JSONArray();
                mAdapterHolder.setJSONArray(mJsonArrOrder);
                mAdapterHolder.notifyDataSetChanged();
                HolderListOnSelectedView = null;
                break;
            case R.id.btn_DEL:
                mJsonArrOrder.remove(lv_HolderList.getPositionForView(HolderListOnSelectedView));
                mAdapterHolder.notifyDataSetChanged();
                btn_DEL.setEnabled(false);
                lv_HolderList.clearChoices();
                HolderListOnSelectedView = null;
                break;
            case R.id.btn_CHECK:
            case R.id.btn_FREE:

                Dialog CheckDialog = new Dialog(MainActivity.this,R.style.MyDialog);
                CheckDialog.setContentView(R.layout.dialog_check);

                ListView lv_OrderList = (ListView)CheckDialog.findViewById(R.id.lv_OrderList);
                AdapterCheck mAdapterCheck = new AdapterCheck(MainActivity.this, mJsonArrOrder);
                lv_OrderList.setAdapter(mAdapterCheck);

                TextView tv_OrderPrice = (TextView)CheckDialog.findViewById(R.id.tv_OrderPrice);

                EditText et_RealPrice = (EditText)CheckDialog.findViewById(R.id.et_RealPrice);
                EditText et_GetPrice = (EditText)CheckDialog.findViewById(R.id.et_GetPrice);
                EditText et_PayBackPrice = (EditText)CheckDialog.findViewById(R.id.et_PayBackPrice);

                if(v.getId() == R.id.btn_CHECK) {
                    tv_OrderPrice.setText(String.format("%d 元", mAdapterCheck.getTotalPrice()));
                    et_RealPrice.setText(String.valueOf(mAdapterCheck.getTotalPrice()));
                }else if(v.getId() == R.id.btn_FREE) {
                    tv_OrderPrice.setText("招待");
                    et_RealPrice.setText("0");
                }

                et_GetPrice.setText(String.valueOf(mAdapterCheck.getTotalPrice()));
                et_PayBackPrice.setText("0");

                new DialogCheckOnClickListener(CheckDialog, mJsonArrOrder);
                CheckDialog.show();
                break;
            case R.id.btn_SET:

                mSettingDialogShow(mDataSnapshot);
                //mSettingDialog.show();
                break;

            case R.id.btn_Clear:
                BottomBar_enable(false);
                break;
            case R.id.btn_AC:
                et_HoldProductNum.setText("1");
                isOnOrderNumChanged = false;
                break;
            case R.id.btn_NUM0:
                if(isOnOrderNumChanged){
                    et_HoldProductNum.append(tBtn.getText().toString());
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
                if(isOnOrderNumChanged)
                    et_HoldProductNum.append(tBtn.getText().toString());
                else{
                    et_HoldProductNum.setText(tBtn.getText().toString());
                    isOnOrderNumChanged = true;
                }
                break;
            case R.id.btn_Enter:

                try {

                    int isRepeat = -1;
                    for (int i = 0; i < mJsonArrOrder.length(); i++) {
                        //  比較新增的產品名稱是否與現有清單中的重複
                        String tProductName = mJsonArrOrder.getJSONObject(i).keys().toString();
                        if (tProductName.equals(et_HoldProductName.getText().toString())) {
                            JSONObject tobj = mJsonArrOrder.getJSONObject(i).getJSONObject(tProductName).getJSONObject("加料");

                            if (tobj.length() == 0 & mJsonObjHoldAddition.length() == 0) {
                                isRepeat = i;
                                break;
                            } else if (tobj.length() == mJsonObjHoldAddition.length()) {
                                Iterator<?> tobjKeys = tobj.keys();
                                while (tobjKeys.hasNext()) {
                                    String tAddtitionName = tobjKeys.next().toString();
                                    if (mJsonObjHoldAddition.has(tAddtitionName)) {
                                        isRepeat = i;
                                    } else {
                                        isRepeat = -1;
                                    }
                                }
                            }
                        }
                    }

                    JSONObject tJsonProductDetail = new JSONObject();
                    JSONObject tJsonProductItem = new JSONObject();

                    tJsonProductDetail.put("加料", mJsonObjHoldAddition);
                    tJsonProductDetail.put("售價", mJsonObjProduct.getJSONObject(onClickTypeButton.getText().toString()).getJSONObject(et_HoldProductName.getText().toString()).getInt("售價"));
                    tJsonProductItem.put(et_HoldProductName.getText().toString(), tJsonProductDetail); //名稱


                    if (isRepeat > -1) {
                        //有重複

                        //確認是否已經選擇Hold List當中的物件
                        if(HolderListOnSelectedView != null) {
                            //有選擇

                            int HolderListOnSelectedPosition = lv_HolderList.getPositionForView(HolderListOnSelectedView);

                            if(HolderListOnSelectedPosition == isRepeat) {
                                tJsonProductDetail.put("數量", Integer.parseInt(et_HoldProductNum.getText().toString()));
                                mJsonArrOrder.put(isRepeat, tJsonProductItem);
                            }else{
                                tJsonProductDetail.put("數量", Integer.parseInt(et_HoldProductNum.getText().toString()) + mJsonArrOrder.getJSONObject(isRepeat).getJSONObject(mJsonArrOrder.getJSONObject(isRepeat).keys().toString()).getInt("數量"));
                                mJsonArrOrder.put(isRepeat, tJsonProductItem);
                                mJsonArrOrder.remove(HolderListOnSelectedPosition);
                            }

                            //lv_HolderList.removeView(HolderListOnSelectedView);
                            //mJsonArrOrder.remove()
                        }else{
                            tJsonProductDetail.put("數量", Integer.parseInt(et_HoldProductNum.getText().toString()) + mJsonArrOrder.getJSONObject(isRepeat).getJSONObject(mJsonArrOrder.getJSONObject(isRepeat).keys().toString()).getInt("數量"));
                            mJsonArrOrder.put(isRepeat, tJsonProductItem);
                        }

                    } else {
                        //沒重複
                        tJsonProductDetail.put("數量", Integer.parseInt(et_HoldProductNum.getText().toString()));

                        //確認是否已經選擇Hold List當中的物件
                        if(HolderListOnSelectedView != null) {
                            //有選擇
                            mJsonArrOrder.put(lv_HolderList.getPositionForView(HolderListOnSelectedView), tJsonProductItem);
                        }else{
                            //沒選擇
                            mJsonArrOrder.put(tJsonProductItem);
                        }

                    }
                    Log.d(TAG, tJsonProductItem.toString());

                    mAdapterHolder.notifyDataSetChanged();
                    //mJsonObjHoldAddition = new JSONObject();
                    BottomBar_enable(false);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
        }

    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //ToggleButton tbtn = (ToggleButton) buttonView;
        try {
            ToggleButton tTB = (ToggleButton) buttonView;

            if(isChecked) {
                tTB.setBackgroundResource(R.drawable.btn_deepblue);
                if(tTB.getParent() == ll_AdditionOther || tTB.getParent() == mAdditionDialog.findViewById(R.id.ll_AdditionList) ||
                        tTB.getParent() == ll_Remark || tTB.getParent() == ll_Remark2) {
                    mJsonObjHoldAddition.put(tTB.getText().toString(), mJsonObjAddition.getJSONObject(tTB.getText().toString()).getInt("價格"));
                }else if(tTB.getParent() == ll_Reminder || tTB.getParent() == mRemindDialog.findViewById(R.id.ll_RemindList)) {
                    mJsonObjHoldAddition.put(tTB.getText().toString(), mJsonObjReminder.getJSONObject(tTB.getText().toString()).getInt("價格"));
                }
                et_HoldProductAddition.append(tTB.getText().toString() + " ");
            }else{
                tTB.setBackgroundResource(R.drawable.btn_blue);
                mJsonObjHoldAddition.remove(tTB.getText().toString());
                et_HoldProductAddition.setText(et_HoldProductAddition.getText().toString().replace(tTB.getText().toString() + " ", ""));
            }

            //Log.d(TAG, "JSONAddition:" +JsonAddition.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static View HolderListOnSelectedView ;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        if( HolderListOnSelectedView != null) {
            HolderListOnSelectedView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        view.setBackgroundColor(Color.argb(255, 153, 204, 255));
        HolderListOnSelectedView = view;

        TextView tv_HolderName = (TextView)view.findViewById(R.id.tv_HolderName);
        TextView tv_HolderNum = (TextView)view.findViewById(R.id.tv_HolderNum);
        TextView tv_HolderAddition = (TextView)view.findViewById(R.id.tv_HolderAddition);

        BottomBar_enable(true);
        btn_DEL.setEnabled(true);

        et_HoldProductName.setText(tv_HolderName.getText().toString());
        et_HoldProductNum.setText(tv_HolderNum.getText().toString());
        //et_HoldProductAddition.setText(tv_HolderAddition.getText().toString());

        for (ToggleButton aTb_AdditionOther : tb_AdditionOther) {
            if (tv_HolderAddition.getText().toString().contains(aTb_AdditionOther.getText().toString())) {
                try {
                    if (mJsonObjAddition.getJSONObject(aTb_AdditionOther.getText().toString()).getInt("位置") > 0) {
                        aTb_AdditionOther.setChecked(true);
                    } else {
                        for (ToggleButton aTb_Remark : tb_Remark) {
                            if (tv_HolderAddition.getText().toString().contains(aTb_Remark.getText()))
                                aTb_Remark.setChecked(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "tb_AdditionOther: " + tv_HolderAddition.getText().toString());
            }
        }
        for (ToggleButton aTb_ReminderOther : tb_ReminderOther) {
            if (tv_HolderAddition.getText().toString().contains(aTb_ReminderOther.getText().toString())) {
                aTb_ReminderOther.setChecked(true);
                Log.d(TAG, "tb_ReminderOther: " + tv_HolderAddition.getText().toString());
            }
        }
/*
        for(int i = 0 ; i< tb_Remark.length ; i++) {
            if(tv_HolderAddition.getText().toString().contains(tb_Remark[i].getText())){
                tb_Remark[i].setChecked(true);
                Log.d(TAG, "tb_Remark: "+ tv_HolderAddition.getText().toString());
            }

        }*/

    }

    private void ParserData () {
        try {


            database.getReference(mStoreName).child("分店資訊").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        mStoreJsonData = new JSONObject(dataSnapshot.getValue().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /**查詢訂單序號**/
            database.getReference(mStoreName).child("訂單").child(mAreaName).child(mDateFormatter.format(new Date())).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                tv_SerialNumber.setText(String.valueOf(dataSnapshot.getChildrenCount() + 1));
                            } else {
                                tv_SerialNumber.setText("1");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    }
            );

            ll_AdditionOther.removeAllViews();
            ((LinearLayout)mAdditionDialog.findViewById(R.id.ll_AdditionList)).removeAllViews();
            ll_Reminder.removeAllViews();
            ((LinearLayout)mRemindDialog.findViewById(R.id.ll_RemindList)).removeAllViews();
            ll_ProductType.removeAllViews();
            ll_ProductItem.removeAllViews();

            int tll_BottomControl_Weight = 3 ;

            /** 查詢叮嚀資訊**/
            LinearLayout.LayoutParams tReminderLayoutParams;
            if(mDataSnapshot.child(mStoreName).hasChild("叮嚀")) {
                tReminderLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

                ll_Reminder.setLayoutParams(new LinearLayout.LayoutParams(0,0,0));
                mJsonObjReminder = new JSONObject(mDataSnapshot.child(mStoreName).child("叮嚀").getValue().toString());
                int tReminderCount = mJsonObjReminder.length();
                tb_ReminderOther = new ToggleButton[tReminderCount];

                int tRemindIndex = 0;
                LinearLayout.LayoutParams tLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
                tLayoutParams.setMargins(5,5,5,5);

                Iterator<?> tJsonObjReminderKeys = mJsonObjReminder.keys();
                while(tJsonObjReminderKeys.hasNext()) {

                    String tReminderName = tJsonObjReminderKeys.next().toString();
                    //Log.d(TAG, tAdditionName);

                    tb_ReminderOther[tRemindIndex] = new ToggleButton(getApplicationContext());
                    tb_ReminderOther[tRemindIndex].setTextOff(tReminderName);
                    tb_ReminderOther[tRemindIndex].setTextOn(tReminderName);
                    tb_ReminderOther[tRemindIndex].setText(tReminderName);
                    tb_ReminderOther[tRemindIndex].setLayoutParams(tLayoutParams);
                    tb_ReminderOther[tRemindIndex].setBackgroundResource(R.drawable.btn_blue);
                    tb_ReminderOther[tRemindIndex].setTextAppearance(getApplicationContext(), R.style.ButtonText);
                    tb_ReminderOther[tRemindIndex].setOnCheckedChangeListener(this);
                    tb_ReminderOther[tRemindIndex].setEnabled(false);

                    int tRemindSerial = mJsonObjReminder.getJSONObject(tReminderName).getInt("位置");
                    if(tRemindSerial < 4) {
                        ll_Reminder.addView(tb_ReminderOther[tRemindIndex], 0);
                    }else if(tRemindSerial == 4){
                        if (tReminderCount == 4) {
                            ll_Reminder.addView(tb_ReminderOther[tRemindIndex]);
                        } else if (tReminderCount > 4) {
                            Button tBtn = CreateButton("更多", R.drawable.btn_deepblue);
                            tBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mRemindDialog.show();
                                }
                            });
                            tBtn.setLayoutParams(tLayoutParams);
                            ll_Reminder.addView(tBtn);
                        }
                    }else if(tRemindSerial > 4) {
                        ((LinearLayout) mRemindDialog.findViewById(R.id.ll_RemindList)).addView(tb_ReminderOther[tRemindIndex]);
                    }


                    tRemindIndex ++;
                }
            }else{
                tReminderLayoutParams = new LinearLayout.LayoutParams(0, 0, 0);
                tll_BottomControl_Weight -= 1;
            }
            ll_Reminder.setLayoutParams(tReminderLayoutParams);

            /**查詢加料資訊 **/
            LinearLayout.LayoutParams tAdditionLayoutParams;
            if(mDataSnapshot.child(mStoreName).hasChild("加料")) {
                tAdditionLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

                mJsonObjAddition = new JSONObject(mDataSnapshot.child(mStoreName).child("加料").getValue().toString());
                int mAdditionCount = mJsonObjAddition.length();
                tb_AdditionOther = new ToggleButton[mAdditionCount];

                Comparator comparator = Collator.getInstance(Locale.TRADITIONAL_CHINESE);
                int tAdditionIndex = 0;
                int tAdditionCount = 1;
                LinearLayout.LayoutParams tLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
                tLayoutParams.setMargins(5,5,5,5);

                Iterator<?> tJsonObjAdditionKeys = mJsonObjAddition.keys();
                while(tJsonObjAdditionKeys.hasNext()) {

                    String tAdditionName = tJsonObjAdditionKeys.next().toString();

                    //Log.d(TAG, tAdditionName);

                    tb_AdditionOther[tAdditionIndex] = new ToggleButton(getApplicationContext());
                    tb_AdditionOther[tAdditionIndex].setTextOff(tAdditionName);
                    tb_AdditionOther[tAdditionIndex].setTextOn(tAdditionName);
                    tb_AdditionOther[tAdditionIndex].setText(tAdditionName);
                    tb_AdditionOther[tAdditionIndex].setLayoutParams(tLayoutParams);
                    tb_AdditionOther[tAdditionIndex].setBackgroundResource(R.drawable.btn_blue);
                    tb_AdditionOther[tAdditionIndex].setTextAppearance(getApplicationContext(), R.style.ButtonText);
                    tb_AdditionOther[tAdditionIndex].setOnCheckedChangeListener(this);
                    tb_AdditionOther[tAdditionIndex].setEnabled(false);

                    if (mAdditionCount <= 4 ){
                        if(mJsonObjAddition.getJSONObject(tAdditionName).has("位置")) {
                            int tPosition = mJsonObjAddition.getJSONObject(tAdditionName).getInt("位置");
                            if(tPosition > 0) {
                                ll_AdditionOther.addView(tb_AdditionOther[tAdditionIndex]);
                            }
                        }

                    } else if (mAdditionCount > 4 ){
                        if(mJsonObjAddition.getJSONObject(tAdditionName).has("位置")) {

                            int tPosition = mJsonObjAddition.getJSONObject(tAdditionName).getInt("位置");

                            if (tAdditionCount < 4 && tPosition < 4  && tPosition > 0) {
                                ll_AdditionOther.addView(tb_AdditionOther[tAdditionIndex]);
                                tAdditionCount++;
                            } else if( tPosition >= 4 ) {
                                if (tAdditionCount == 4) {
                                    Button tBtn = CreateButton("更多", R.drawable.btn_deepblue);
                                    tBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAdditionDialog.show();
                                        }
                                    });
                                    tBtn.setLayoutParams(tLayoutParams);
                                    ll_AdditionOther.addView(tBtn);
                                    tAdditionCount++;
                                }

                                if (((LinearLayout) mAdditionDialog.findViewById(R.id.ll_AdditionList)).getChildCount() == 0) {
                                    ((LinearLayout) mAdditionDialog.findViewById(R.id.ll_AdditionList)).addView(tb_AdditionOther[tAdditionIndex]);
                                } else {
                                    for (int i = 0; i < ((LinearLayout) mAdditionDialog.findViewById(R.id.ll_AdditionList)).getChildCount(); i++) {
                                        if (comparator.compare(((ToggleButton) ((LinearLayout) mAdditionDialog.findViewById(R.id.ll_AdditionList)).getChildAt(i)).getText().toString(), tAdditionName) < 0) {
                                            ((LinearLayout) mAdditionDialog.findViewById(R.id.ll_AdditionList)).addView(tb_AdditionOther[tAdditionIndex], i);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tAdditionIndex ++;
                }

            }else{
                tAdditionLayoutParams = new LinearLayout.LayoutParams(0, 0, 0);
                tll_BottomControl_Weight -= 1;
            }

            if(ll_AdditionOther.getChildCount() == 0){
                tAdditionLayoutParams = new LinearLayout.LayoutParams(0, 0, 0);
                tll_BottomControl_Weight -= 1;
            }
            ll_AdditionOther.setLayoutParams(tAdditionLayoutParams);

            findViewById(R.id.ll_BottomControl).setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, (float)tll_BottomControl_Weight));

            /**查詢商品資料**/
            if(mDataSnapshot.child(mStoreName).hasChild("商品")) {

                mJsonObjProduct = new JSONObject(mDataSnapshot.child(mStoreName).child("商品").getValue().toString());
                //Log.d(TAG, "jsonProduct::" + mJsonObjProduct.toString());

                Iterator<?> tProductTypeKeys = mJsonObjProduct.keys();
                while (tProductTypeKeys.hasNext()) {

                    String tProductTypeName = (String) tProductTypeKeys.next();

                    if (mJsonObjProduct.get(tProductTypeName) instanceof JSONObject) {

                        /** Create Product Type Button to ll_ProductType LinearLayout**/
                        Button btn = CreateButton(tProductTypeName, R.drawable.btn_blue);
                        btn.setOnClickListener(new ProductTypeButtonOnClickListener());
                        ll_ProductType.addView(btn);

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ProductTypeButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            onClickTypeButton = (Button) v;
            try {
                /** Create Product Item Button to ll_ProductItem LinearLayout**/


                final LinearLayout tProductItemLinearLayout = new LinearLayout(getApplicationContext());
                tProductItemLinearLayout.setOrientation(LinearLayout.VERTICAL);

                JSONObject jobj = mJsonObjProduct.getJSONObject(((Button) v).getText().toString());

                final Iterator<?> jsonProductItemKeys = jobj.keys();

                //判斷是否需要價格分頁
                if(jobj.length() / NUM_PRE_LINE > 4  && isPriceDivEnable) {

                    final LinearLayout tLinearLayout_PriceDiv = new LinearLayout(getApplicationContext());
                    final JSONObject tJsonObject = new JSONObject();
                    while(jsonProductItemKeys.hasNext()) {
                        String jsonProductItemKey = (String) jsonProductItemKeys.next();
                        if (jobj.get(jsonProductItemKey) instanceof JSONObject) {
                            int tPrice = jobj.getJSONObject(jsonProductItemKey).getInt("售價");

                            if(tJsonObject.has(String.valueOf(tPrice))) {
                                tJsonObject.getJSONArray(String.valueOf(tPrice)).put(jsonProductItemKey);
                            }else{
                                JSONArray tJsonArray = new JSONArray();
                                tJsonArray.put(jsonProductItemKey);
                                tJsonObject.put(String.valueOf(tPrice), tJsonArray);
                            }
                        }
                    }

                    Iterator<?> tJsonObjectKeys = tJsonObject.keys();
                    while(tJsonObjectKeys.hasNext()) {
                        String tPriceStr = tJsonObjectKeys.next().toString();
                        Button tBtn = CreateButton(tPriceStr, R.drawable.btn_deepblue);
                        tBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LinearLayout tLinearLayout_Product = new LinearLayout(getApplicationContext());
                                tLinearLayout_Product.setOrientation(LinearLayout.VERTICAL);
                                try {
                                    JSONArray tJsonArrayProductNames = tJsonObject.getJSONArray(((Button)v).getText().toString());
                                    LinearLayout tLinearLayout = new LinearLayout(getApplicationContext());
                                    tLinearLayout.setOrientation(LinearLayout.HORIZONTAL);


                                    int index = 1;

                                    for(int i = 0 ; i < tJsonArrayProductNames.length() ; i++){

                                        Button tBtn = CreateButton(tJsonArrayProductNames.getString(i), R.drawable.btn_blue);
                                        tBtn.setOnClickListener(new ProductItemButtonOnClickListener());
                                        tLinearLayout.addView(tBtn);

                                        if (index++ % NUM_PRE_LINE == 0) {
                                            tLinearLayout_Product.addView(tLinearLayout);
                                            tLinearLayout = new LinearLayout(getApplicationContext());
                                            tLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                        }
                                    }

                                    if (tLinearLayout.getChildCount() != 0)  {
                                        int tCount = NUM_PRE_LINE - tLinearLayout.getChildCount();
                                        for (int i = 0; i < tCount ; i++)
                                            tLinearLayout.addView(CreateButton("", R.drawable.btn_blue));
                                        tLinearLayout_Product.addView(tLinearLayout);
                                    }

                                    ScrollView tScrollView = new ScrollView(getApplicationContext());
                                    tScrollView.addView(tLinearLayout_Product);
                                    if(tProductItemLinearLayout.getChildCount() == 2)
                                        tProductItemLinearLayout.removeView(tProductItemLinearLayout.getChildAt(1));
                                    tProductItemLinearLayout.addView(tScrollView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        //Log.d("TAG",tBtn.getText().toString() + tLinearLayout_PriceDiv.getChildCount());
                        if(tLinearLayout_PriceDiv.getChildCount() == 0) {
                            tLinearLayout_PriceDiv.addView(tBtn);
                        }else {
                            for (int i = 0; i < tLinearLayout_PriceDiv.getChildCount(); i++) {

                                if (Integer.parseInt(tPriceStr) < Integer.parseInt(((Button) tLinearLayout_PriceDiv.getChildAt(i)).getText().toString())) {
                                    tLinearLayout_PriceDiv.addView(tBtn, i);
                                    break;
                                } else if (i == tLinearLayout_PriceDiv.getChildCount()-1) {
                                    tLinearLayout_PriceDiv.addView(tBtn, i+1);
                                    break;
                                }
                            }
                        }
                        //tLinearLayout_PriceDiv.addView(tBtn);

                    }

                    tProductItemLinearLayout.addView(tLinearLayout_PriceDiv);
                    ll_ProductItem.removeAllViews();
                    ll_ProductItem.addView(tProductItemLinearLayout);

                }else {

                    int index = 1;
                    LinearLayout tLinearLayout_Product = new LinearLayout(getApplicationContext());
                    tLinearLayout_Product.setOrientation(LinearLayout.HORIZONTAL);

                    while (jsonProductItemKeys.hasNext()) {
                        String jsonProductItemKey = (String) jsonProductItemKeys.next();
                        Button tBtn = CreateButton(jsonProductItemKey, R.drawable.btn_blue);
                        tBtn.setOnClickListener(new ProductItemButtonOnClickListener());
                        tLinearLayout_Product.addView(tBtn);
                        if (index++ % NUM_PRE_LINE == 0) {
                            tProductItemLinearLayout.addView(tLinearLayout_Product);
                            tLinearLayout_Product = new LinearLayout(getApplicationContext());
                            tLinearLayout_Product.setOrientation(LinearLayout.HORIZONTAL);
                        }
                    }
                    if (tLinearLayout_Product.getChildCount() != 0) {
                        int tCount = NUM_PRE_LINE - tLinearLayout_Product.getChildCount();
                        for (int i = 0; i < tCount; i++)
                            tLinearLayout_Product.addView(CreateButton("", R.drawable.btn_blue));
                        tProductItemLinearLayout.addView(tLinearLayout_Product);
                    }
                    ScrollView tScrollView = new ScrollView(getApplicationContext());
                    tScrollView.addView(tProductItemLinearLayout);

                    ll_ProductItem.removeAllViews();
                    ll_ProductItem.addView(tScrollView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BottomBar_enable(false);
        }
    }

    private class ProductItemButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            BottomBar_enable(true);
            et_HoldProductName.setText(((Button) v).getText().toString());
            et_HoldProductNum.setText(String.valueOf(1));
            et_HoldProductAddition.setText("");
        }
    }
}
