package alfaloop.com.ipos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Iterator;

import alfaloop.com.ipos.myclass.ClassAdditionItem;
import alfaloop.com.ipos.myclass.ClassOnsale;
import alfaloop.com.ipos.myclass.ClassProductItem;
import alfaloop.com.ipos.myclass.ClassRemindItem;


/**
 * Created by kiec on 2016/9/10.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    private static String TAG = "SettingActivity";

    private static Spinner sp_AdditionList, sp_AdditionMain[], sp_RemindList, sp_RemindMain[], sp_ProductList, sp_ProductTypeList, sp_EmployeeList, sp_EmployeeLevel;
    private static int id_AdditionMain[] = {R.id.sp_AdditionMain01, R.id.sp_AdditionMain02, R.id.sp_AdditionMain03},
            id_RemindMain[] = {R.id.sp_RemindMain01, R.id.sp_RemindMain02, R.id.sp_RemindMain03};


    private static EditText et_AdditionPriceModify, et_AdditionName, et_AdditionPrice,
            et_RemindPriceModify, et_RemindName, et_RemindPrice,
            et_ProductPriceModify, et_ProductName, et_ProductPrice, et_ProductTypeName,
            et_EmployeeSN, et_EmployeeName;

    private ArrayList<String> tListType, tListProduct, tListEmployee;
    private ArrayAdapter<String> tTypeListAdapter, tProductListAdapter, tEmployeeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

    }

    private void initView(){

        int tAdditionMainSelection[] = new int[id_AdditionMain.length];
        int tAdditionIndex = 0;
        ArrayList<String> tListAddition = new ArrayList<String>();
        Iterator<DataSnapshot> tAdditionNames = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("加料").getChildren().iterator();
        while(tAdditionNames.hasNext()) {
            String tAdditionName = tAdditionNames.next().getKey();
            tListAddition.add(tAdditionName);
            int tAdditionPosition = Integer.parseInt(MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("加料").child(tAdditionName).child("位置").getValue().toString());
            if(tAdditionPosition > 0 && tAdditionPosition <= id_AdditionMain.length) {
                tAdditionMainSelection[tAdditionPosition-1] = tAdditionIndex;
            }
            tAdditionIndex++;
        }
        ArrayAdapter<String> tAdapterAddition = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListAddition);
        tAdapterAddition.setDropDownViewResource(R.layout.myspinner);
        sp_AdditionList = (Spinner)findViewById(R.id.sp_AdditionList);
        sp_AdditionList.setAdapter(tAdapterAddition);
        sp_AdditionMain = new Spinner[id_AdditionMain.length];
        for(int i = 0 ; i < id_AdditionMain.length ; i ++) {
            sp_AdditionMain[i] = (Spinner) findViewById(id_AdditionMain[i]);
            sp_AdditionMain[i].setAdapter(tAdapterAddition);
            sp_AdditionMain[i].setSelection(tAdditionMainSelection[i]);
        }


        int tRemindMainSelection[] = new int[id_RemindMain.length];
        int tRemindIndex = 0;
        ArrayList<String> tListRemind = new ArrayList<String>();
        Iterator<DataSnapshot> tRemindNames = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("叮嚀").getChildren().iterator();
        while(tRemindNames.hasNext()) {
            String tReminName = tRemindNames.next().getKey();
            tListRemind.add(tReminName);
            int tRemindPosition = Integer.parseInt(MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("叮嚀").child(tReminName).child("位置").getValue().toString());
            if(tRemindPosition > 0 && tRemindPosition <= id_RemindMain.length) {
                tRemindMainSelection[tRemindPosition-1] = tRemindIndex;
            }
            tRemindIndex++;
        }
        ArrayAdapter<String> tAdapterRemind = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListRemind);
        sp_RemindList = (Spinner)findViewById(R.id.sp_RemindList);
        sp_RemindList.setAdapter(tAdapterRemind);
        sp_RemindMain = new Spinner[id_RemindMain.length];
        for(int i = 0 ; i < id_RemindMain.length ; i ++) {
            sp_RemindMain[i] = (Spinner) findViewById(id_RemindMain[i]);
            sp_RemindMain[i].setAdapter(tAdapterRemind);
            sp_RemindMain[i].setSelection(tRemindMainSelection[i]);

        }


        tListType = new ArrayList<String>();
        tListProduct = new ArrayList<String>();

        Iterator<DataSnapshot> tTypeNames = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("商品").getChildren().iterator();
        while(tTypeNames.hasNext()) {
            String tTypeName = tTypeNames.next().getKey();
            tListType.add(tTypeName);
            Iterator<DataSnapshot> tProductNames = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("商品").child(tTypeName).getChildren().iterator();
            while(tProductNames.hasNext())
                tListProduct.add(tProductNames.next().getKey());
        }

        sp_ProductTypeList = (Spinner)findViewById(R.id.sp_ProductTypeList);
        tTypeListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListType);
        sp_ProductTypeList.setAdapter(tTypeListAdapter);

        sp_ProductList = (Spinner)findViewById(R.id.sp_ProductList);
        tProductListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListProduct);
        sp_ProductList.setAdapter(tProductListAdapter);


        tListEmployee = new ArrayList<String>();
        ArrayList<String> tListEmployeeLevel = new ArrayList<String>();

        Iterator<DataSnapshot> tEmployeeNames = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("員工").getChildren().iterator();
        while(tEmployeeNames.hasNext()) {
            String tEmployeeName = tEmployeeNames.next().getKey();
            tListEmployee.add(tEmployeeName);
        }

        sp_EmployeeList = (Spinner)findViewById(R.id.sp_EmployeeList);
        tEmployeeListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListEmployee);
        sp_EmployeeList.setAdapter(tEmployeeListAdapter);


        tListEmployeeLevel.add("員工");
        tListEmployeeLevel.add("管理者");

        sp_EmployeeLevel = (Spinner)findViewById(R.id.sp_EmployeeLevel);
        sp_EmployeeLevel.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, tListEmployeeLevel));

        et_AdditionPriceModify = (EditText) findViewById(R.id.et_AdditionPriceModify);
        et_AdditionName = (EditText) findViewById(R.id.et_AdditionName);
        et_AdditionPrice = (EditText) findViewById(R.id.et_AdditionPrice);
        et_RemindPriceModify = (EditText) findViewById(R.id.et_RemindPriceModify);
        et_RemindName = (EditText) findViewById(R.id.et_RemindName);
        et_RemindPrice = (EditText) findViewById(R.id.et_RemindPrice);
        et_ProductPriceModify = (EditText) findViewById(R.id.et_ProductPriceModify);
        et_ProductName = (EditText) findViewById(R.id.et_ProductName);
        et_ProductPrice = (EditText) findViewById(R.id.et_ProductPrice);
        et_ProductTypeName = (EditText) findViewById(R.id.et_ProductTypeName);
        et_EmployeeSN = (EditText) findViewById(R.id.et_EmployeeSN);
        et_EmployeeName = (EditText) findViewById(R.id.et_EmployeeName);

        findViewById(R.id.btn_AdditionPriceModify).setOnClickListener(this);
        findViewById(R.id.btn_AdditionDelete).setOnClickListener(this);
        findViewById(R.id.btn_AdditionAdd).setOnClickListener(this);
        findViewById(R.id.btn_RemindPriceModify).setOnClickListener(this);
        findViewById(R.id.btn_RemindDelete).setOnClickListener(this);
        findViewById(R.id.btn_RemindAdd).setOnClickListener(this);
        findViewById(R.id.btn_ProductPriceModify).setOnClickListener(this);
        findViewById(R.id.btn_ProductOff).setOnClickListener(this);
        findViewById(R.id.btn_ProductOn).setOnClickListener(this);
        findViewById(R.id.btn_TypeOff).setOnClickListener(this);
        findViewById(R.id.btn_TypeOn).setOnClickListener(this);
        findViewById(R.id.btn_ProductAdd).setOnClickListener(this);
        findViewById(R.id.btn_TypeAdd).setOnClickListener(this);
        findViewById(R.id.btn_EmployeeOff).setOnClickListener(this);
        findViewById(R.id.btn_EmployeeAdd).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_AdditionPriceModify:
                if(! et_AdditionPriceModify.getText().toString().isEmpty() ) {
                    String tSelectAdditionName = ((TextView) sp_AdditionList.getSelectedView()).getText().toString();
                    int tModifyAdditionPrice = Integer.parseInt(et_AdditionPriceModify.getText().toString());
                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("加料")
                            .child(tSelectAdditionName).child("價格").setValue(tModifyAdditionPrice);
                    Toast.makeText(getApplicationContext(), tSelectAdditionName + " 價格修改為 " + tModifyAdditionPrice, Toast.LENGTH_LONG).show();
                    et_AdditionPriceModify.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), " 請勿輸入空值", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_AdditionDelete:
                Toast.makeText(getApplicationContext(),"尚未啟用此功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_AdditionAdd:
                if(! et_AdditionName.getText().toString().isEmpty() && ! et_AdditionPrice.getText().toString().isEmpty() ) {
                    String tSetAdditionName = et_AdditionName.getText().toString();
                    int tSetAdditionPrice = Integer.parseInt(et_AdditionPrice.getText().toString());
                    ClassAdditionItem tCA = new ClassAdditionItem(tSetAdditionPrice, 4);
                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("加料")
                            .child(tSetAdditionName).setValue(tCA);
                    Toast.makeText(getApplicationContext(), tSetAdditionName + " 已新增!", Toast.LENGTH_LONG).show();
                    et_AdditionName.setText("");
                    et_AdditionPrice.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), " 請勿輸入空值", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_RemindPriceModify:
                if(! et_RemindPriceModify.getText().toString().isEmpty() ) {
                    String tSelectRemindName = ((TextView) sp_RemindList.getSelectedView()).getText().toString();
                    int tModifyRemindPrice = Integer.parseInt(et_RemindPriceModify.getText().toString());
                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("叮嚀")
                            .child(tSelectRemindName).child("價格").setValue(tModifyRemindPrice);
                    Toast.makeText(getApplicationContext(), tSelectRemindName + " 價格修改為 " + tModifyRemindPrice, Toast.LENGTH_LONG).show();
                    et_RemindPriceModify.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), " 請勿輸入空值", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_RemindDelete:
                Toast.makeText(getApplicationContext(),"尚未啟用此功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_RemindAdd:
                if(! et_RemindName.getText().toString().isEmpty() && ! et_RemindPrice.getText().toString().isEmpty() ) {
                    String tSetRemindName = et_RemindName.getText().toString();
                    int tSetRemindPrice = Integer.parseInt(et_RemindPrice.getText().toString());
                    ClassRemindItem tCR = new ClassRemindItem(tSetRemindPrice, 4);
                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("叮嚀")
                            .child(tSetRemindName).setValue(tCR);
                    Toast.makeText(getApplicationContext(), tSetRemindName + " 已新增!", Toast.LENGTH_LONG).show();
                    et_RemindName.setText("");
                    et_RemindPrice.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), " 請勿輸入空值", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_ProductPriceModify:
            case R.id.btn_ProductOff:
            case R.id.btn_ProductOn:
                String tSelectProductName = ((TextView)sp_RemindList.getSelectedView()).getText().toString();
                int tModifyProductPrice = Integer.parseInt(et_RemindPriceModify.getText().toString());
                boolean isFind = false;
                Iterator<DataSnapshot> tTypeNames = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("產品").getChildren().iterator();
                while(tTypeNames.hasNext()) {
                    String tTypeName = tTypeNames.next().getKey();
                    Iterator<DataSnapshot> tProductNames = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("產品").child(tTypeName).getChildren().iterator();
                    while(tProductNames.hasNext()) {
                        String tProductName = tProductNames.next().getKey();
                        if(tProductName.equals(tSelectProductName)) {
                            switch(v.getId()) {
                                case R.id.btn_ProductPriceModify:
                                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("產品").child(tTypeName)
                                            .child(tSelectProductName).child("售價").setValue(tModifyProductPrice);
                                    Toast.makeText(getApplicationContext(), tSelectProductName + " 價格修改為 " + tModifyProductPrice, Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.btn_ProductOff:
                                case R.id.btn_ProductOn:
                                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("產品").child(tTypeName)
                                            .child(tSelectProductName).child("onsale").child(MainActivity.mAreaName).setValue(((Button)v).getText().toString());
                                    Toast.makeText(getApplicationContext(), tSelectProductName + " 修改為 " + ((Button)v).getText().toString(), Toast.LENGTH_LONG).show();
                                    break;

                            }

                            isFind = true;
                            break;
                        }
                    }
                    if(isFind) break;
                }

                break;
            case R.id.btn_TypeOff:
                Toast.makeText(getApplicationContext(),"尚未啟用此功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_TypeOn:
                Toast.makeText(getApplicationContext(),"尚未啟用此功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_ProductAdd:
                if(! et_ProductName.getText().toString().isEmpty() && ! et_ProductPrice.getText().toString().isEmpty() ) {
                    String tSetProductName = et_ProductName.getText().toString();
                    int tSetProductPrice = Integer.parseInt(et_ProductPrice.getText().toString());

                    ClassOnsale tCO = new ClassOnsale();

                    Iterator<DataSnapshot> tAreaKeys = MainActivity.mDataSnapshot.child(MainActivity.mStoreName).child("分店資訊").getChildren().iterator();
                    while (tAreaKeys.hasNext()) {
                        String tAreaName = tAreaKeys.next().getKey();
                        tCO.setMap(tAreaName, "上架");
                    }

                    ClassProductItem tCP = new ClassProductItem();
                    tCP.setMap(tSetProductPrice, "存在", tCO.toMap());

                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("叮嚀")
                            .child(tSetProductName).setValue(tCP);

                    tListProduct.add(tSetProductName);
                    tProductListAdapter.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), tSetProductName + " 已新增!", Toast.LENGTH_LONG).show();
                    et_ProductName.setText("");
                    et_ProductPrice.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), " 請勿輸入空值", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_TypeAdd:
                if(! et_ProductTypeName.getText().toString().isEmpty() ) {
                    String tTypeName = et_ProductTypeName.getText().toString();
                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("產品")
                            .child(tTypeName).setValue("未新增");
                }else{
                    Toast.makeText(getApplicationContext(), " 請勿輸入空值", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_EmployeeOff:

                break;
            case R.id.btn_EmployeeAdd:
                if(! et_EmployeeSN.getText().toString().isEmpty() && ! et_EmployeeName.getText().toString().isEmpty() ) {
                    String tEmpolyeeSN = et_EmployeeSN.getText().toString();
                    String tEmpolyeeName = et_EmployeeName.getText().toString();
                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("員工")
                            .child(tEmpolyeeName).child("編號").setValue(tEmpolyeeSN);

                    String tEmployeeLevel = ((TextView) sp_EmployeeLevel.getSelectedView()).getText().toString();
                    MainActivity.mDataSnapshot.getRef().child(MainActivity.mStoreName).child("員工")
                            .child(tEmpolyeeName).child("職位").setValue(tEmployeeLevel);

                    tListEmployee.add(tEmpolyeeName);
                    tEmployeeListAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), tEmployeeLevel + ":" + tEmpolyeeName + "(" + tEmpolyeeSN + ") 已新增!", Toast.LENGTH_LONG).show();
                    et_EmployeeSN.setText("");
                    et_EmployeeName.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), " 請勿輸入空值", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}
