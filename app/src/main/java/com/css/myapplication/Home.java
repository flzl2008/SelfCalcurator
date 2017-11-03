package com.css.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class Home extends Activity {
    TextView t1;
    TextView t4;
    TextView t2;
    TextView t3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        t1=(TextView) findViewById(R.id.tv1);
        t2=(TextView) findViewById(R.id.tv2);
        t3=(TextView) findViewById(R.id.tv3);
        t4=(TextView) findViewById(R.id.tv4);
        Typeface myfonts = Typeface.createFromAsset(getAssets(),"fonts/BM.ttf");
        t1.setTypeface(myfonts);
        t2.setTypeface(myfonts);
        t3.setTypeface(myfonts);
        t4.setTypeface(myfonts);
    }
    public void onClick(View view) {
        Intent intent = new Intent(this, UserLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void onClick1(View view){
        Intent intent = new Intent(this, Searchproduct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void onClick2(View view){
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}