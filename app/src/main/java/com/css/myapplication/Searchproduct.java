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

public class Searchproduct extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        Typeface myfonts = Typeface.createFromAsset(getAssets(),"fonts/BM.ttf");


        final EditText etwrite = (EditText) findViewById(R.id.etwrite);
        final Button bsearch = (Button) findViewById(R.id.bsearch);
        final TextView atv1 = (TextView)findViewById(R.id.atv1) ;
        etwrite.setTypeface(myfonts);
        bsearch.setTypeface(myfonts);
        atv1.setTypeface(myfonts);

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String write = etwrite.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                String name =  jsonResponse.getString("name");
                                String price =  jsonResponse.getString("price");
                                String floor =  jsonResponse.getString("floor");
                                String group =  jsonResponse.getString("group");

                                Intent intent = new Intent(Searchproduct.this, Product.class);
                                intent.putExtra("name",name);
                                intent.putExtra("price",price);
                                intent.putExtra("floor",floor);
                                intent.putExtra("group",group);

                                Searchproduct.this.startActivity(intent);

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(Searchproduct.this);
                                builder.setMessage("없는 물품입니다")
                                        .setNegativeButton("다시입력",null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                SearchproductRequest searchproductRequest = new SearchproductRequest( write,responseListener );
                RequestQueue queue = Volley.newRequestQueue(Searchproduct.this);
                queue.add(searchproductRequest);
            }
        });

    }
}