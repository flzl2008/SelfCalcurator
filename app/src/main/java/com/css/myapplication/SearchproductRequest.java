package com.css.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchproductRequest extends StringRequest {
    private static final String SEARCHPRODUCT_REQUEST_URL = "http://2016ccsmycart.iptime.org:8080/searchcart.php";
    private Map<String, String> params;

    public SearchproductRequest(String write , Response.Listener<String> listener){
        super(Method.POST, SEARCHPRODUCT_REQUEST_URL, listener , null);
        params = new HashMap<>();
        params.put("write",write);
    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}