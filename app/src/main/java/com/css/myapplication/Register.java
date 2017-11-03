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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends Activity {

     EditText etbarcode ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Typeface myfonts = Typeface.createFromAsset(getAssets(),"fonts/BM.ttf");

        final EditText etname = (EditText) findViewById(R.id.etname);
        final EditText etprice = (EditText) findViewById(R.id.etprice);
        final EditText etfloor = (EditText) findViewById(R.id.etfloor);
        final EditText etgroup = (EditText) findViewById(R.id.etgroup);
        final EditText etweight = (EditText) findViewById(R.id.etweight);
        etbarcode = (EditText) findViewById(R.id.etbarcode);
        final Button bRegister = (Button) findViewById(R.id.bRegister);
        final Button camera = (Button) findViewById(R.id.camera);
        final TextView tv5 = (TextView) findViewById(R.id.tv5);
        etname.setTypeface(myfonts);
        etprice.setTypeface(myfonts);
        etfloor.setTypeface(myfonts);
        etgroup.setTypeface(myfonts);
        etweight.setTypeface(myfonts);
        etbarcode.setTypeface(myfonts);
        bRegister.setTypeface(myfonts);
        tv5.setTypeface(myfonts);
        camera.setTypeface(myfonts);


        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etname.getText().toString();
                final String price = etprice.getText().toString();
                final String floor = etfloor.getText().toString();
                final String group = etgroup.getText().toString();
                final String weight = etweight.getText().toString();
                final String barcode = etbarcode.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                Toast.makeText(getApplicationContext(), "제품이 추가되었습니다,", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this , Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Register.this.startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                builder.setMessage("항목을 다시 입력하시오")
                                        .setNegativeButton("다시입력",null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                registerRequest registerRequest = new registerRequest(name, price , floor ,group ,weight ,barcode, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(registerRequest);
            }
        });
    }
    public void onClick4(View view) {   //추가버튼
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("input","input");//id값 메인엑티비티로전달
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent,1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if( resultCode == RESULT_OK){
            if( requestCode == 1) {
              etbarcode.setText(data.getStringExtra("barcode"));
            }
        }
    }
}