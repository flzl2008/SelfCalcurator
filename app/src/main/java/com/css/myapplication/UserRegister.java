package com.css.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class UserRegister extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);

        final EditText etid = (EditText) findViewById(R.id.etid);
        final EditText etpw = (EditText) findViewById(R.id.etpw);
        final Button etregister = (Button) findViewById(R.id.etregister);
        final TextView ettv1 = (TextView) findViewById(R.id.ettv1);
        final TextView ettv2 = (TextView) findViewById(R.id.ettv2);

        etregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String u_id = etid.getText().toString();
                final String u_pw = etpw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(!success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserRegister.this);
                                Toast.makeText(getApplicationContext(), "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserRegister.this , UserLogin.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                UserRegister.this.startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserRegister.this);
                                builder.setMessage("이미 존재하는 ID 입니다. ")
                                        .setNegativeButton("다시입력",null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                UserRegisterRequest UserRegisterRequest = new UserRegisterRequest(u_id,u_pw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserRegister.this);
                queue.add(UserRegisterRequest);
            }
        });
    }
}