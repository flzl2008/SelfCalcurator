package com.css.myapplication;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class SubRequest extends StringRequest {
    private static final String SUB_REQUEST_URL="http://2016ccsmycart.iptime.org:8080/subamount.php";
    private Map<String, String> params;

    public SubRequest(String getproduct, String id, Response.Listener<String> listener){
        super(Method.POST,SUB_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("name",getproduct);
        params.put("id",id);
    }
    @Override
    public Map<String,String> getParams() {return  params;}

}