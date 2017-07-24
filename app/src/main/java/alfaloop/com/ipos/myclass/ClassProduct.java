package alfaloop.com.ipos.myclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiec on 2016/8/18.
 */
public class ClassProduct {
    public int 售價;
    public int 數量;
    public int 總額;
    public String 狀態;
    private HashMap<String, Object> result = new HashMap<String, Object>();
    public Map<String, Object> 加料;

    public ClassProduct() {}

    public ClassProduct(String 名稱, int 售價, int 數量, int 總額, String 狀態, Map<String, Object> 加料) {
        result.put("名稱", 名稱);
        result.put("售價", 售價);
        result.put("數量", 數量);
        result.put("總額", 總額);
        result.put("狀態", 狀態);
        result.put("加料", 加料);
    }


    public void setMap(String 名稱, int 售價, int 數量, int 總額, String 狀態, Map<String, Object> 加料) {
        result.put("名稱", 名稱);
        result.put("售價", 售價);
        result.put("數量", 數量);
        result.put("總額", 總額);
        result.put("狀態", 狀態);
        result.put("加料", 加料);
    }

    @Exclude
    public Map<String, Object> toMap() {
        return result;
    }
}
