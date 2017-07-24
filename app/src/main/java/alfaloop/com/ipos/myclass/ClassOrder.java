package alfaloop.com.ipos.myclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiec on 2016/8/18.
 */
public class ClassOrder {

    public String 時間;
    public String 用餐方式;
    public int 實收總額;
    public int 銷售總額;
    HashMap<String, Object> result = new HashMap<String, Object>();
    public Map<String, Object> 訂單明細;

    public ClassOrder() {

    }

    public ClassOrder(String 時間, String 用餐方式, int 實收總額, int 銷售總額, Map<String, Object> 訂單明細) {
        this.時間 = 時間;
        this.用餐方式 = 用餐方式;
        this.實收總額 = 實收總額;
        this.銷售總額 = 銷售總額;
        this.訂單明細 = 訂單明細;
    }


    public void setMap(String 時間, String 用餐方式, int 實收總額, int 銷售總額, Map<String, Object> 訂單明細) {
        result.put("時間", 時間);
        result.put("用餐方式", 用餐方式);
        result.put("實收總額", 實收總額);
        result.put("銷售總額", 銷售總額);
        result.put("訂單明細", 訂單明細);
    }

    public void addCellPhoneNumber(String 電話) {
        result.put("電話",電話);
    }

    @Exclude
    public Map<String, Object> toMap() {
        return result;
    }

}
