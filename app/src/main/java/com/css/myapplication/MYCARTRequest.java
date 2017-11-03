package com.css.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MYCARTRequest extends StringRequest {
    private static final String LINEDELE_REQUEST_URL = "http://2016ccsmycart.iptime.org:8080/linedelete.php";
    private Map<String, String> params;

    public MYCARTRequest(String d_name, String d_id, Response.Listener<String> listener){
        super(Method.POST, LINEDELE_REQUEST_URL, listener , null);
        params = new HashMap<>();
        params.put("name",d_name);
        params.put("id",d_id);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}