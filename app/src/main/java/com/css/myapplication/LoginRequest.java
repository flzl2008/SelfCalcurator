package com.css.myapplication;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest{
    private static final String LOGIN_REQUEST_URL = "http://2016ccsmycart.iptime.org:8080/login.php";
    private Map<String, String> params;

    public LoginRequest(String id, String pw, Response.Listener<String> listener){
        super(Method.POST, LOGIN_REQUEST_URL, listener , null);
        params = new HashMap<>();
        params.put("id",id);
        params.put("pw",pw);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
