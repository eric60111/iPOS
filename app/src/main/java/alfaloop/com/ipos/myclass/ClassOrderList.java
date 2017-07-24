package alfaloop.com.ipos.myclass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiec on 2016/8/18.
 */
public class ClassOrderList {

    private HashMap<String, Object> result = new HashMap<String, Object>();

    //public String 產品名稱;
    public String 序號;
    public Map<String, Object> 產品明細;

    public ClassOrderList() {  }

    public ClassOrderList(String 序號, Map<String, Object> 產品明細) {
        //this.產品名稱 = 產品名稱;
        this.序號 = 序號;
        this.產品明細 = 產品明細;
    }

    public void setMap(String 序號, Map<String, Object> 產品明細) {
        result.put(序號, 產品明細);
    }

    @Exclude
    public Map<String, Object> toMap() {
        return result;
    }
}
