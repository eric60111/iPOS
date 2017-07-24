package alfaloop.com.ipos;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

public class TicketPrinterThread extends Thread {

    private static final int TYPE_ORDER = 0;
    private static final int TYPE_TEXT = 1;

    private static final String GS = String.valueOf((char)29);
    private static final String ESC = String.valueOf((char)27);
    private static final String HT = String.valueOf((char)9);

    private static JSONObject mJSONObj;
    private static String mAreaName, mStoreName, mCurrentTime, mSerailNumber;
    private static String mPrintText;
    private static int TYPE;
    private String mTicketPrinterIP;

    public TicketPrinterThread(String mCurrentTime, String mSerialNumber, JSONObject mJSONObjStr) {
        Log.d("TAG",mJSONObjStr.toString());
        this.mJSONObj = mJSONObjStr;
        this.mAreaName = MainActivity.mAreaName;
        this.mStoreName = MainActivity.mStoreName;
        this.mCurrentTime = mCurrentTime;
        this.mSerailNumber = mSerialNumber;
        this.mTicketPrinterIP = MainActivity.mTicketPrinterIP;
        TYPE = TYPE_ORDER;

        Log.d("TicketPrinterThread", "TicketPrinter Init");
    }



    public TicketPrinterThread(String mPrintText) {
        this.mPrintText = mPrintText;
        TYPE = TYPE_TEXT;
        this.mTicketPrinterIP = MainActivity.mTicketPrinterIP;
    }

    @Override
    public void run(){

        try {

            Socket socket = new Socket(mTicketPrinterIP, 9100);
            //Socket socket = new Socket("192.168.4.119", 9100);
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(),"BIG5");

            switch(TYPE) {
                case TYPE_ORDER:
                    int tRepeat;
                    if (mJSONObj.get("用餐方式").toString().equals("外送")) {
                        tRepeat = 1;
                    } else {
                        tRepeat = 2;
                        out.write(ESC + String.valueOf((char) 112) + String.valueOf((char) 0) + String.valueOf((char) 50) + String.valueOf((char) 250) + "\n"); //Open the Cash Box
                    }

                    for(int i = 0 ; i < tRepeat ; i++) {
                        out.write(GS + String.valueOf((char) 33) + (char) 17); //GS ! 17
                        out.write(mStoreName + "(" + mAreaName + ")\n");

                        out.write(ESC + String.valueOf((char) 74) + String.valueOf((char) 50) + "\n"); //Padding Bottom 50

                        out.write(GS + String.valueOf((char) 33) + (char) 17); //GS ! 17
                        out.write("等候號碼:" + String.format("%03d", Integer.parseInt(mSerailNumber)) + "  (" + mJSONObj.get("用餐方式").toString() + ")\n\n");

                        if (mJSONObj.get("用餐方式").toString().equals("外送") && mJSONObj.has("電話"))
                            out.write("聯絡電話:" + mJSONObj.get("電話").toString() + "\n\n");

                        //out.write(String.format("等候號碼:%03d(%s)\n\n", Integer.parseInt(mSerailNumber), mJSONObj.getString("用餐方式")));

                        out.write(GS + String.valueOf((char) 33) + (char) 0); //GS ! 00
                        out.write(String.format("%s\n", mCurrentTime));

                        out.write(GS + String.valueOf((char) 33) + (char) 17); //GS ! 17
                        out.write("- - - - - - - - - - - - \n");

                        JSONArray tJSONArray = mJSONObj.getJSONArray("訂單明細");

                        for(int j = 0 ; j < tJSONArray.length(); j++) {

                            String mProductName = tJSONArray.getJSONObject(j).getString("名稱");
                            int mProductNum = tJSONArray.getJSONObject(j).getInt("數量");
                            int mProductPrice = tJSONArray.getJSONObject(j).getInt("總額");

                            out.write(GS + String.valueOf((char) 33) + (char) 17); //GS ! 17
                            out.write(ESC + String.valueOf((char) 68) + String.valueOf((char) 21) + "\n"); //Set HT Width Right 21
                            out.write(mProductName + "x" + String.valueOf(mProductNum) + HT + String.valueOf(mProductPrice) + "\n");

                            out.write(GS + String.valueOf((char) 33) + (char) 0); //GS ! 00


                            if (tJSONArray.getJSONObject(j).has("加料")) {
                                String mAdditionName = "";
                                JSONObject mJsonAddition = tJSONArray.getJSONObject(j).getJSONObject("加料");
                                Iterator<?> mJsonAdditionKeys = mJsonAddition.keys();
                                while (mJsonAdditionKeys.hasNext()) {
                                    mAdditionName += mJsonAdditionKeys.next().toString() + " ";
                                }

                                out.write(mAdditionName + "\n");

                            }


                        }

                        //Total
                        out.write(GS + String.valueOf((char) 33) + (char) 17); //GS ! 17
                        out.write("- - - - - - - - - - - - \n");

                        out.write(ESC + String.valueOf((char) 68) + String.valueOf((char) 17) + "\n"); //Set HT Width Right 17

                        int mTotalPrice = (Integer) mJSONObj.get("實收總額");
                        out.write("共計" + HT + "＄ " + ((mTotalPrice != 0) ? String.valueOf(mTotalPrice) : "招待") + "\n");

                        // Cut Paper
                        out.write(GS + String.valueOf((char) 86) + String.valueOf((char) 66) + String.valueOf((char) 0) + "\n"); // ESC J 66 0
                    }

                    // Repeat
                    //out.write(GS + String.valueOf((char) 94) + (char)tRepeat + (char)0 +  (char)0 + "\n"); // GS  ^  r t m


                    break;


                case TYPE_TEXT:

                    String[] tPrintText = mPrintText.split(String.format("%n"));
                    out.write(GS + String.valueOf((char) 33) + (char) 17); //GS ! 17

                    for(int i = 0 ; i < tPrintText.length ; i++) {
                        out.write(String.format("%s\n",tPrintText[i]) );
                    }
                    // Cut Paper
                    out.write(GS + String.valueOf((char) 86) + String.valueOf((char) 66) + String.valueOf((char) 0) + "\n"); // ESC J 66 0

                    break;
            }

            out.flush();
            out.close();
            socket.close();

        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
