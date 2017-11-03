package com.css.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class Product extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        final TextView editname = (TextView) findViewById(R.id.editname);
        final TextView editprice = (TextView) findViewById(R.id.editprice);
        final TextView editfloor = (TextView) findViewById(R.id.editfloor);
        final TextView editgroup = (TextView) findViewById(R.id.editgroup);
        final TextView stv1 = (TextView) findViewById(R.id.stv1);
        final TextView stv2 = (TextView) findViewById(R.id.stv2);
        final TextView stv3 = (TextView) findViewById(R.id.stv3);
        final TextView stv4 = (TextView) findViewById(R.id.stv4);
        final TextView atv1 = (TextView) findViewById(R.id.atv1);
        Typeface myfonts = Typeface.createFromAsset(getAssets(),"fonts/BM.ttf");
        editname.setTypeface(myfonts);
        editprice.setTypeface(myfonts);
        editfloor.setTypeface(myfonts);
        editgroup.setTypeface(myfonts);
        stv1.setTypeface(myfonts);
        stv2.setTypeface(myfonts);
        stv3.setTypeface(myfonts);
        stv4.setTypeface(myfonts);
        atv1.setTypeface(myfonts);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String floor= intent.getStringExtra("floor");
        String group = intent.getStringExtra("group");

        editname.setText(name);
        editprice.setText(price);
        editfloor.setText(floor);
        editgroup.setText(group);

    }
}