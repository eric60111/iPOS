package alfaloop.com.ipos.myclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiec on 2016/8/18.
 */
public class ClassRemindItem {

    public int 位置;
    public int 價格;

    private HashMap<String, Object> result = new HashMap<String, Object>();
    public ClassRemindItem() {}

    public ClassRemindItem(int 位置, int 價格) {
        result.put("價格", 價格);
        result.put("位置", 位置);
    }

    public void setMap(int 位置, int 價格) {
        result.put("價格", 價格);
        result.put("位置", 位置);
    }

    @Exclude
    public Map<String, Object> toMap() {
        return result;
    }

}
