package com.css.myapplication;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class AddRequest extends StringRequest {
    private static final String ADD_REQUEST_URL="http://2016ccsmycart.iptime.org:8080/addamount.php";
    private Map<String, String> params;

    public AddRequest(String product,String id, Response.Listener<String> listener){
        super(Method.POST,ADD_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("name",product);
        params.put("id",id);
    }
    @Override
    public Map<String,String> getParams() {return  params;}

}