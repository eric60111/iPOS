package alfaloop.com.ipos.myclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiec on 2016/8/18.
 */
public class ClassAddition {
    public String AdditionName;
    public int AdditionPrice;
    private HashMap<String, Object> result = new HashMap<String, Object>();
    public ClassAddition () {}

    public ClassAddition(String AdditionName, int AdditionPrice) {
        this.AdditionName = AdditionName;
        this.AdditionPrice = AdditionPrice;
    }

    public void setMap(String AdditionName, int AdditionPrice) {
        result.put(AdditionName, AdditionPrice);
    }

    @Exclude
    public Map<String, Object> toMap() {
        return result;
    }

}
