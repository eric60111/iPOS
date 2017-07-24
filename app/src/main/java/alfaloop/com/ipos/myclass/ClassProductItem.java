package alfaloop.com.ipos.myclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiec on 2016/8/18.
 */
public class ClassProductItem {
    public int 售價;
    public String 狀態;
    private HashMap<String, Object> result = new HashMap<String, Object>();
    public Map<String, Object> onsale;

    public ClassProductItem() {}

    public ClassProductItem(int 售價, String 狀態, Map<String, Object> onsale) {

        result.put("售價", 售價);
        result.put("狀態", 狀態);
        result.put("onsale", onsale);
    }


    public void setMap(int 售價, String 狀態, Map<String, Object> onsale) {
        result.put("售價", 售價);
        result.put("狀態", 狀態);
        result.put("onsale", onsale);
    }

    @Exclude
    public Map<String, Object> toMap() {
        return result;
    }
}
