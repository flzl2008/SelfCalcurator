package com.css.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserLogin extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);

        final EditText u_id = (EditText) findViewById(R.id.u_id);
        final EditText u_pw = (EditText) findViewById(R.id.u_pw);
        final Button u_login = (Button) findViewById(R.id.u_login);
        final Button u_register = (Button) findViewById(R.id.u_register);
        u_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = u_id.getText().toString();
                final String pw = u_pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if(success) {

                                String id = jsonObject.getString("id");
                                String pw = jsonObject.getString("pw");

                                Intent intent = new Intent(UserLogin.this, MYCART.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("name",id);
                                UserLogin.this.startActivity(intent);

                            } else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserLogin.this);
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

                UserLoginRequest UserLoginRequest = new UserLoginRequest(id,pw,responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserLogin.this);
                queue.add(UserLoginRequest);
            }
        });

    }
    public void onClick3(View view){
        Intent intent = new Intent(this, UserRegister.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}