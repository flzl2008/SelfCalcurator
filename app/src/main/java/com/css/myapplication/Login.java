package com.css.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Typeface myfonts = Typeface.createFromAsset(getAssets(),"fonts/BM.ttf");

        final EditText etid = (EditText) findViewById(R.id.etid);
        final EditText etpw = (EditText) findViewById(R.id.etpw);
        final Button bLogin = (Button) findViewById(R.id.blogin);
        final TextView tv1 = (TextView)findViewById(R.id.tv1);
        etid.setTypeface(myfonts);
        etpw.setTypeface(myfonts);
        bLogin.setTypeface(myfonts);
        tv1.setTypeface(myfonts);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = etid.getText().toString();
                final String pw = etpw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if(success) {

                                String id = jsonObject.getString("id");
                                String pw = jsonObject.getString("pw");

                                Intent intent = new Intent(Login.this, Register.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Login.this.startActivity(intent);

                            } else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                builder.setMessage("로그인 실패")
                                        .setNegativeButton("다시입력",null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(id,pw,responseListener);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(loginRequest);
            }
        });

    }

}