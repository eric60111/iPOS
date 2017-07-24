package alfaloop.com.ipos;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Iterator;

import static alfaloop.com.ipos.MainActivity.mTicketPrinterIP;

/**
 * Created by kiec on 2016/9/9.
 */
public class StickPrinterThread extends Thread {

    private static final int PortNumber = 9100;

    private static JSONObject mJSONObj;
    private static String mAreaName, mStoreName, mCurrentTime, mSerailNumber;

    private String mSticketPrinterIP;

    public StickPrinterThread(String mCurrentTime, String mSerialNumber, JSONObject mJSONObjStr) {
        this.mJSONObj = mJSONObjStr;
        this.mAreaName = MainActivity.mAreaName;
        this.mStoreName = MainActivity.mStoreName;
        this.mCurrentTime = mCurrentTime;
        this.mSerailNumber = mSerialNumber;
        this.mSticketPrinterIP = MainActivity.mStickPrinterIP;
        Log.d("SticketPrinterThread", "SticketPrinter Init");
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(mSticketPrinterIP , PortNumber);
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(),"BIG5");

            String tStrWay = mJSONObj.getString("用餐方式");
            String tStrStorePhoneNumber = MainActivity.mStoreJsonData.getJSONObject(mAreaName).getString("電話");

            JSONArray mJSONArray = mJSONObj.getJSONArray("訂單明細");
            for(int i = 0 ; i < mJSONArray.length() ; i ++) {
                String mProductName = mJSONArray.getJSONObject(i).getString("名稱");
                int mProductNum = mJSONArray.getJSONObject(i).getInt("數量");
                int mProductPrice = mJSONArray.getJSONObject(i).getInt("總額") / mProductNum ;

                for(int j = 0 ; j < mProductNum ; j++) {

                    if (MainActivity.STICK_MODEL.equals("PL3150")) {
                        out.write("CLS\n");
                        out.write("DIRECTION 1\n");
                        out.write("SIZE 32 mm, 25 mm\n");
                        out.write("GAP 2mm, 0\n");
                        out.write("TEXT 0,5,\"TST24.BF2\",0,1,1,\"編號：" + String.format("%03d-%d/%d(%s)\n", Integer.parseInt(mSerailNumber), j+1, mProductNum, tStrWay));
                        out.write("TEXT 0,40,\"TST24.BF2\",0,1,1,\"" + mProductName + "\" \n");
                        if( mJSONArray.getJSONObject(i).has("加料")) {
                            String mAdditionName = "";
                            JSONObject mJsonAddition =  mJSONArray.getJSONObject(i).getJSONObject("加料");
                            Iterator<?> mJsonAdditionKeys = mJsonAddition.keys();
                            while (mJsonAdditionKeys.hasNext()) {
                                mAdditionName += mJsonAdditionKeys.next().toString() + " ";
                            }

                            out.write("TEXT 0,70,\"TST24.BF2\",0,1,1,\"" + mAdditionName + " $" + mProductPrice + "\" \n");

                        }else {
                            out.write("TEXT 0,70,\"TST24.BF2\",0,1,1,\" $" + mProductPrice + "\" \n");
                        }
                        out.write("TEXT 0,100,\"TST24.BF2\",0,1,1,\"" + mCurrentTime + "\" \n");
                        out.write("TEXT 0,130,\"TST24.BF2\",0,1,1,\""+MainActivity.mStoreName+"("+MainActivity.mAreaName+")\" \n");
                        out.write("TEXT 0,165,\"TST24.BF2\",0,1,1,\"外送專線:"+tStrStorePhoneNumber+"\" \n");

                    } else if (MainActivity.STICK_MODEL.equals("GoDEX")) {

                        out.write("^Q25,3\n");
                        out.write("^W35\n");
                        out.write("^H5\n");
                        out.write("^P1\n");
                        out.write("^S2\n");
                        out.write("^AD\n");
                        out.write("^C1\n");
                        out.write("^R24\n");
                        out.write("~Q+0\n");
                        out.write("^O0\n");
                        out.write("^D0\n");
                        out.write("^E12\n");
                        out.write("~R200\n");
                        out.write("^XSET,ROTATION,0\n");
                        out.write("^L\n");
                        out.write("Dy2-me-dd\n");
                        out.write("Th:m:s\n");
                        out.write("Dy2-me-dd\n");
                        out.write("Th:m:s\n");
                        out.write("AZ2,0,12,1,1,0,0,編號： "+ String.format("%03d-%d/%d(%s)\n", Integer.parseInt(mSerailNumber), j+1, mProductNum, tStrWay));
                        out.write("AZ2,0,46,1,1,0,0," + mProductName + "\n");
                        if( mJSONArray.getJSONObject(i).has("加料")) {
                            String mAdditionName = "";
                            JSONObject mJsonAddition =  mJSONArray.getJSONObject(i).getJSONObject("加料");
                            Iterator<?> mJsonAdditionKeys = mJsonAddition.keys();
                            while (mJsonAdditionKeys.hasNext()) {
                                mAdditionName += mJsonAdditionKeys.next().toString() + " ";
                            }
                            out.write("AZ2,0,79,1,1,0,0," + mAdditionName + " $" + mProductPrice + "\n");

                        }else {
                            out.write("AZ2,0,79,1,1,0,0,$" + mProductPrice + "\n");
                        }

                        out.write("AZ2,0,105,1,1,0,0," + mCurrentTime + "\n");
                        out.write("AZ2,0,138,1,1,0,0,"+mStoreName+"("+mAreaName+")\n");
                        out.write("AZ2,0,172,1,1,0,0,外送專線:"+tStrStorePhoneNumber+"\n");
                        out.write("E\n");
                    }

                }

            }
            out.flush();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}

