package com.css.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterRequest extends StringRequest{

    private static final String USERREGISTET_REQUEST_URL = "http://2016ccsmycart.iptime.org:8080/userregister.php";
    private Map<String, String> params;

    public UserRegisterRequest(String u_id, String u_pw, Response.Listener<String> listener){
        super(Method.POST, USERREGISTET_REQUEST_URL, listener , null);
        params = new HashMap<>();
        params.put("u_id",u_id);
        params.put("u_pw",u_pw);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
