package alfaloop.com.ipos.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import alfaloop.com.ipos.MainActivity;
import alfaloop.com.ipos.R;
import alfaloop.com.ipos.SettingActivity;
import alfaloop.com.ipos.TicketPrinterThread;


public class DialogSettingOnClickListener implements View.OnClickListener{
    private static Dialog mDialog, mDialogPrintReview;
    private static Spinner sp_StoreName, sp_AreaName, sp_Employee;
    private static EditText et_StickIP, et_TicketIP, et_PageNum;
    private static TextView tv_DayVolume, tv_MonthVolume, tv_RecordMonth, tv_PrintReview;
    private static RadioGroup rg_StickType;
    //private static DataSnapshot mDataSnapshot;
    private static CheckBox cb_StickEnable, cb_TicketEnable, cb_PriceDiv;

    private static Calendar m_Calendar = Calendar.getInstance();

    public DialogSettingOnClickListener(Dialog mDialog) {
        DialogSettingOnClickListener.mDialog = mDialog;

        mDialog.findViewById(R.id.btn_StoreDel).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_StoreAdd).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_AreaDel).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_AreaAdd).setOnClickListener(this);

        mDialog.findViewById(R.id.btn_DayPicker).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_DaySearch).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_MonthPicker).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_MonthSearch).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_RecordMonthPicker).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_RecordSearch).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_Save).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_MoreSetting).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_Exit).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_PageNumUp).setOnClickListener(this);
        mDialog.findViewById(R.id.btn_PageNumDown).setOnClickListener(this);


        et_StickIP = (EditText)mDialog.findViewById(R.id.et_StickIP) ;
        et_TicketIP = (EditText)mDialog.findViewById(R.id.et_TicketIP) ;
        et_PageNum = (EditText)mDialog.findViewById(R.id.et_PageNum) ;
        sp_StoreName = (Spinner)mDialog.findViewById(R.id.sp_StoreName);
        sp_AreaName = (Spinner)mDialog.findViewById(R.id.sp_AreaName);
        sp_Employee = (Spinner)mDialog.findViewById(R.id.sp_Employee);
        tv_DayVolume = (TextView)mDialog.findViewById(R.id.tv_DayVolume);
        tv_MonthVolume = (TextView)mDialog.findViewById(R.id.tv_MonthVolume);
        tv_RecordMonth = (TextView)mDialog.findViewById(R.id.tv_RecordMonth);
        cb_StickEnable =(CheckBox)mDialog.findViewById(R.id.cb_StickEnable);
        cb_TicketEnable =(CheckBox)mDialog.findViewById(R.id.cb_TicketEnable);
        cb_PriceDiv = (CheckBox)mDialog.findViewById(R.id.cb_PriceDiv);
        rg_StickType = (RadioGroup) mDialog.findViewById(R.id.rg_StickType);

        mDialogPrintReview = new Dialog(mDialog.getContext(),R.style.MyDialog);
        mDialogPrintReview.setContentView(R.layout.dialog_printreview);
        mDialogPrintReview.setCanceledOnTouchOutside(false);

        mDialogPrintReview.findViewById(R.id.btn_Print).setOnClickListener(this);
        mDialogPrintReview.findViewById(R.id.btn_PrintCancel).setOnClickListener(this);
        tv_PrintReview = (TextView)mDialogPrintReview.findViewById(R.id.tv_PrintReview);
        et_PageNum.setText(String.valueOf(MainActivity.NUM_PRE_LINE));

        for(int i = 0 ; i< rg_StickType.getChildCount() ; i++ ){
            if(((TextView)rg_StickType.getChildAt(i)).getText().toString().equals(MainActivity.STICK_MODEL)) {
                rg_StickType.check(rg_StickType.getChildAt(i).getId());
                break;
            }
        }

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_StoreDel:
                Toast.makeText(mDialog.getContext(), "尚未啟用此功能!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_StoreAdd:
                Toast.makeText(mDialog.getContext(), "尚未啟用此功能!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_AreaDel:
                Toast.makeText(mDialog.getContext(), "尚未啟用此功能!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_AreaAdd:
                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DatabaseReference myRef = database.getReference(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).child(mDateFormatter.format(new Date())).child(MainActivity.tv_SerialNumber.getText().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(mDialog.getContext());
                builder.setTitle("新增分店");

                final EditText input = new EditText(mDialog.getContext());
                input.setWidth(100);
                input.setTextSize(30);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("新增" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tAreaText = input.getText().toString();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference(MainActivity.mStoreName).child("訂單").child(tAreaText).setValue("無資料");
                        mDialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                //Toast.makeText(mDialog.getContext(), "尚未啟用此功能!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_DayPicker:

                DatePickerDialog datePickerDialog = new DatePickerDialog(mDialog.getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        m_Calendar.set(Calendar.YEAR, year);
                        m_Calendar.set(Calendar.MONTH, monthOfYear);
                        m_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "yyyy/MM/dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                        tv_DayVolume.setText(sdf.format(m_Calendar.getTime()));
                    }
                },
                m_Calendar.get(Calendar.YEAR),
                m_Calendar.get(Calendar.MONTH),
                m_Calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setCalendarViewShown(false);

                datePickerDialog.show();
                break;
            case R.id.btn_DaySearch:
            case R.id.btn_MonthSearch:
            case R.id.btn_RecordSearch:

                try {
                    //Log.d("TAG", mDataSnapshot.toString());
                    String tempStr ;
                    if (v.getId() == R.id.btn_DaySearch && MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).hasChild(tv_DayVolume.getText().toString() )) {
                        //Log.d("TAG", mDataSnapshot.child(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).child(sdf.format(m_Calendar.getTime())).getValue().toString());
                        JSONObject tJsonResp = CalVolume(tv_DayVolume.getText().toString(), MainActivity.mDataSnapshot);

                        tempStr =  String.format("%s(%s)%n日營業報表%n時間:%s%n%n銷售總額: %d%n實收總額: %d",
                                MainActivity.mStoreName,
                                MainActivity.mAreaName,
                                tv_DayVolume.getText().toString(),
                                tJsonResp.getInt("銷售總額"),
                                tJsonResp.getInt("實收總額"));


                    }else if(v.getId() == R.id.btn_MonthSearch && MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).hasChild(tv_MonthVolume.getText().toString())) {
                        //Log.d("TAG", mDataSnapshot.child(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).child(sdf.format(m_Calendar.getTime())).getValue().toString());Set

                        int tRealPrice = 0, tGetPrice = 0;
                        Iterator<DataSnapshot> tDateKeys = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).child(tv_MonthVolume.getText().toString()).getChildren().iterator();
                        while (tDateKeys.hasNext()) {
                            String tDate = tDateKeys.next().getKey();
                            JSONObject tJsonResp = CalVolume(String.format("%s/%s", tv_MonthVolume.getText().toString(), tDate), MainActivity.mDataSnapshot);
                            tRealPrice += tJsonResp.getInt("銷售總額");
                            tGetPrice += tJsonResp.getInt("實收總額");
                        }

                        tempStr = String.format("%s(%s)%n月營業報表%n時間:%s%n%n銷售總額: %d%n實收總額: %d",
                                MainActivity.mStoreName,
                                MainActivity.mAreaName,
                                tv_MonthVolume.getText().toString(),
                                tRealPrice,
                                tGetPrice);

                    }else if(v.getId() == R.id.btn_RecordSearch && MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("員工").child(((TextView)sp_Employee.getSelectedView()).getText().toString()).child("打卡紀錄").hasChild(tv_RecordMonth.getText().toString())) {

                        String tEmployeeName = ((TextView)sp_Employee.getSelectedView()).getText().toString();
                        tempStr = tv_RecordMonth.getText().toString() + "打卡紀錄\n";
                        tempStr+= "人員:" + tEmployeeName + "\n\n";
                        tempStr+= String.format("%s%15s%25s\n", "時間", "上班", "下班");
                        Iterator<DataSnapshot> tEmployeeRecordDays = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("員工").child(tEmployeeName).child("打卡紀錄").child(tv_RecordMonth.getText().toString()).getChildren().iterator();

                        while(tEmployeeRecordDays.hasNext()) {
                            String tDate = tEmployeeRecordDays.next().getKey();
                            String tDay = String.format("%s/%s",tv_RecordMonth.getText().toString(), tDate);

                            String tOnWork = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("員工").child(tEmployeeName).child("打卡紀錄").child(tDay).child("上班").getChildren().iterator().next().getKey();
                            String tOffWork = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("員工").child(tEmployeeName).child("打卡紀錄").child(tDay).child("下班").getChildren().iterator().next().getKey();

                            tempStr += String.format("%3s%20s%15s\n", tDate, tOnWork, tOffWork);

                        }


                    }else{
                        tempStr = "查無資料";
                    }

                    tv_PrintReview.setText(tempStr);
                    mDialogPrintReview.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_MonthPicker:
                createDialogWithoutDateField(tv_MonthVolume).show();
                break;
            case R.id.btn_RecordMonthPicker:
                createDialogWithoutDateField(tv_RecordMonth).show();
                break;
            case R.id.btn_MoreSetting:

                //Toast.makeText(mDialog.getContext(), "尚未啟用此功能!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(mDialog.getContext(), SettingActivity.class);
                mDialog.getContext().startActivity(intent);
                mDialog.dismiss();

                break;
            case R.id.btn_Save:

                if(((TextView) sp_StoreName.getSelectedView()).getText().toString().equals("請選擇") || ((TextView) sp_AreaName.getSelectedView()).getText().toString().equals("請選擇") ) {
                    Toast.makeText(mDialog.getContext(), "請選擇正確選項", Toast.LENGTH_SHORT).show();
                }else{
                    //String tEmployeeName = ((TextView)sp_Employee.getSelectedView()).getText().toString();
                    MainActivity.mStoreName = ((TextView) sp_StoreName.getSelectedView()).getText().toString();
                    MainActivity.mAreaName = ((TextView) sp_AreaName.getSelectedView()).getText().toString();
                    MainActivity.mTicketPrinterIP = et_TicketIP.getText().toString();
                    MainActivity.mStickPrinterIP = et_StickIP.getText().toString();
                    MainActivity.isTicketPrintEnable = cb_TicketEnable.isChecked();
                    MainActivity.isStickPrintEnable = cb_StickEnable.isChecked();
                    MainActivity.NUM_PRE_LINE = Integer.parseInt(et_PageNum.getText().toString());
                    MainActivity.isPriceDivEnable = cb_PriceDiv.isChecked();

                    MainActivity.STICK_MODEL = ((TextView)mDialog.findViewById(rg_StickType.getCheckedRadioButtonId())).getText().toString();

                    MainActivity.tv_Area.setText(MainActivity.mAreaName);

                    //取得SharedPreference設定("Preference"為設定檔的名稱)
                    SharedPreferences settings = mDialog.getContext().getSharedPreferences("iPOS", 0);
                    //置入name屬性的字串
                    settings.edit().putString("店名", MainActivity.mStoreName).apply();
                    settings.edit().putString("分店", MainActivity.mAreaName).apply();
                    settings.edit().putString("出單機IP", MainActivity.mTicketPrinterIP).apply();
                    settings.edit().putString("貼紙機IP", MainActivity.mStickPrinterIP).apply();
                    settings.edit().putBoolean("出單機開關", MainActivity.isTicketPrintEnable).apply();
                    settings.edit().putBoolean("貼紙機開關", MainActivity.isStickPrintEnable).apply();
                    settings.edit().putInt("每行顯示數量", MainActivity.NUM_PRE_LINE).apply();
                    settings.edit().putBoolean("價格分類顯示", MainActivity.isPriceDivEnable).apply();
                    Toast.makeText(mDialog.getContext(), "資料已儲存", Toast.LENGTH_LONG).show();

                    mDialog.dismiss();
                }
                break;
            case R.id.btn_PageNumDown:
                if(!et_PageNum.getText().toString().equals("0")) {
                    et_PageNum.setText(String.valueOf(Integer.parseInt(et_PageNum.getText().toString()) - 1));
                }
                break;
            case R.id.btn_PageNumUp:
                et_PageNum.setText(String.valueOf(Integer.parseInt(et_PageNum.getText().toString()) + 1));
                break;
            case R.id.btn_Exit:
                mDialog.dismiss();
                break;
            case R.id.btn_Print:
                new TicketPrinterThread(tv_PrintReview.getText().toString()).start();
                break;
            case R.id.btn_PrintCancel:
                mDialogPrintReview.dismiss();
                break;
        }
    }

    private JSONObject CalVolume(String mDate, DataSnapshot mDataSnapshot) throws JSONException {

        JSONObject tJsonResp = new JSONObject();
        int tRealPrice = 0, tGetPrice = 0;


        JSONArray tJsonDayVolume = new JSONArray(mDataSnapshot.child(MainActivity.mStoreName).child("訂單").child(MainActivity.mAreaName).child(mDate).getValue().toString());

        for (int i = 1; i < tJsonDayVolume.length(); i++) {
            tRealPrice += tJsonDayVolume.getJSONObject(i).getInt("銷售總額");
            tGetPrice += tJsonDayVolume.getJSONObject(i).getInt("實收總額");
        }

        tJsonResp.put("銷售總額", tRealPrice);
        tJsonResp.put("實收總額", tGetPrice);

        return tJsonResp;
    }

    private DatePickerDialog createDialogWithoutDateField(final TextView tv) {
        DatePickerDialog dpd = new DatePickerDialog(mDialog.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                m_Calendar.set(Calendar.YEAR, year);
                m_Calendar.set(Calendar.MONTH, monthOfYear);
                String myFormat = "yyyy/MM";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                tv.setText(sdf.format(m_Calendar.getTime()));
            }
        }, m_Calendar.get(Calendar.YEAR), m_Calendar.get(Calendar.MONTH), m_Calendar.get(Calendar.DATE));
        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ignored) {

        }
        return dpd;
    }
}
