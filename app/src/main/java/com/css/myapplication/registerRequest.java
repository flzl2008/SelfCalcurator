package com.css.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
public class registerRequest extends StringRequest{

    private static final String REGISTET_REQUEST_URL = "http://2016ccsmycart.iptime.org:8080/register.php";
    private Map<String, String> params;

    public registerRequest(String name, String price, String floor ,String group ,String weight ,String barcode , Response.Listener<String> listener){
        super(Method.POST, REGISTET_REQUEST_URL, listener , null);
        params = new HashMap<>();
        params.put("name",name);
        params.put("price",price);
        params.put("floor",floor);
        params.put("group",group);
        params.put("weight",weight);
        params.put("barcode",barcode);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
