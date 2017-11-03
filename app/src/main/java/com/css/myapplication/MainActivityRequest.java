package com.css.myapplication;

/**
 * Created by kim on 2016-05-29.
 */


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class MainActivityRequest extends StringRequest {
    private static final String MAINACTIVITY_REQUEST_URL="http://2016ccsmycart.iptime.org:8080/addcart.php";
    private Map<String, String> params;

    public MainActivityRequest(String barcode,String id, Response.Listener<String> listener){
        super(Method.POST,MAINACTIVITY_REQUEST_URL,listener,null);
        params = new HashMap<>();
        params.put("barcode",barcode);
        params.put("id",id);
    }
    @Override
    public Map<String,String> getParams() {return  params;}

}