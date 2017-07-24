package alfaloop.com.ipos.myclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiec on 2016/10/19.
 */
public class ClassOnsale {
    private String 分店;
    private String 狀態;
    private HashMap<String, Object> result = new HashMap<String, Object>();

    public ClassOnsale() {}

    public ClassOnsale(String 分店, String 狀態) {
        this.分店 = 分店;
        this.狀態 = 狀態;
    }

    public void setMap(String 分店, String 狀態) {
        result.put(分店, 狀態);
    }

    @Exclude
    public Map<String, Object> toMap() {
        return result;
    }
}
