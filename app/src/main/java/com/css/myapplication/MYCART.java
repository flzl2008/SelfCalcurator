package com.css.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MYCART extends Activity {
    TextView t1;
    TextView t2;
    TextView t3;
    TextView t4;
    TextView t5;
    TextView t6;
    TextView sum;
    Button bt2;
    Button bt3;
    Button bt1;
    Button bt4;

    public int total=0;
    static boolean select=false;
    static boolean all_select=false;
    static boolean all_cancle=false;
    static boolean delete=false;
    String name;
    String price;
    String amount;
    String myJSON;
    String id;
    JSONArray product = null;

    ArrayList<ArrayList<String>> productList;
    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mycart);
        select=false;
        t1 = (TextView) findViewById(R.id.txt1);
        t2 = (TextView) findViewById(R.id.txt2);
        t3 = (TextView) findViewById(R.id.txt3);
        t4 = (TextView) findViewById(R.id.t_name);
        t5 = (TextView) findViewById(R.id.t_price);
        t6 = (TextView) findViewById(R.id.t_count);
        sum = (TextView) findViewById(R.id.sum);
        bt2=(Button) findViewById((R.id.bt2)) ;
        bt3=(Button) findViewById((R.id.bt3)) ;
        bt1=(Button) findViewById((R.id.bt1)) ;
        bt4=(Button) findViewById((R.id.bt4)) ;

        Typeface myfonts = Typeface.createFromAsset(getAssets(), "fonts/BM.ttf");
        t1.setTypeface(myfonts);
        t2.setTypeface(myfonts);
        t3.setTypeface(myfonts);
        t4.setTypeface(myfonts);
        t5.setTypeface(myfonts);
        t6.setTypeface(myfonts);
        sum.setTypeface(myfonts);


        list = (ListView) findViewById(R.id.listView);
        productList = new ArrayList<ArrayList<String>>();
        id= getIntent().getStringExtra("name");
        Log.i("id",id);
        getData("http://2016ccsmycart.iptime.org:8080/showcart.php",id);

        // Custom Adapter Instance 생성 및 ListView에 Adapter 지정
        final CustomAdapter adapter = new CustomAdapter(this, productList);
        list.setAdapter(adapter);
        // ListView의 아이템 클릭 이벤트
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomAdapter adapter = (CustomAdapter) list.getAdapter();
                name=adapter.items.get(position).get(0);
                price=adapter.items.get(position).get(1);
                amount=adapter.items.get(position).get(2);

                view.setSelected(true);
                Log.d("item",name);
            }
        });


        bt2.setOnClickListener(new View.OnClickListener() {   //선텍모드 클릭
            public void onClick(View v) {
                //선택모드
                select=true;
                bt1.setVisibility(View.VISIBLE);   //전체선택 가시
                bt4.setVisibility(View.VISIBLE);   //전체취소 가시
                bt3.setVisibility(View.VISIBLE);
                bt2.setVisibility(View.GONE);   //선택모드 비가시
                CustomAdapter adapter = (CustomAdapter)list.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {   //모드해제 클릭
            public void onClick(View v) {
                //모드해체
                select=false;
                bt1.setVisibility(View.GONE);   //전체선택 비가시
                bt4.setVisibility(View.GONE);   //전체취소 비가시
                bt3.setVisibility(View.GONE);
                bt2.setVisibility(View.VISIBLE);   //선택모드 가시
                all_cancle=true;  //다시 체크리스트 초기화
                all_select=false;
                delete=false;
                CustomAdapter adapter = (CustomAdapter)list.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });

        // 전체 체크 버튼 클릭시 Listener
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_select=true;
                delete=false;
                // Adapter에 Data에 변화가 생겼을때 Adapter에 알려준다.
                CustomAdapter adapter = (CustomAdapter)list.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });

        //  취소 버튼 클릭시 Listener
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_cancle=true;
                all_select=false;
                delete=false;
                // Adapter에 Data에 변화가 생겼을때 Adapter에 알려준다.
                CustomAdapter adapter = (CustomAdapter)list.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });
        // 스레즈 생성하고 시작
        BackThread thread = new BackThread();
        thread.setDaemon(true);
        thread.start();
    }

    class BackThread extends Thread{
        @Override
        public void run() {
            while(true){
                // 메인에서 생성된 Handler 객체의 sendEmpryMessage 를 통해 Message 전달
                handler.sendEmptyMessage(0);

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } // end while
        } // end run()
    } // end class BackThread

    // '메인스레드' 에서 Handler 객체를 생성한다.
    // Handler 객체를 생성한 스레드 만이 다른 스레드가 전송하는 Message나 Runnable 객체를
    // 수신할수 있다.
    // 아래 생성된 Handler 객체는 handlerMessage() 를 오버라이딩 하여
    // Message 를 수신
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){   // Message id 가 0 이면
                CustomAdapter adapter = (CustomAdapter)list.getAdapter();
                adapter.notifyDataSetChanged();
            }
        }
    };

    public class CustomAdapter extends BaseAdapter  {
        Context context;
        ArrayList<ArrayList<String>> items;
        LayoutInflater inflater;

        public CustomAdapter(Context context,  ArrayList<ArrayList<String>> items){
            this.context = context;
            this.items = items;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int key_p=position;
            View v = convertView;
            if(v == null){
                v = inflater.inflate(R.layout.list_item, null);
            }

            final TextView text1 = (TextView)v.findViewById(R.id.name);
            final TextView text2 = (TextView)v.findViewById(R.id.price);
            final TextView text3 = (TextView)v.findViewById(R.id.count);
            Typeface myfonts = Typeface.createFromAsset(getAssets(), "fonts/BM.ttf");
            text1.setTypeface(myfonts);
            text2.setTypeface(myfonts);
            text3.setTypeface(myfonts);

            String nm = items.get(position).get(0);
            String pri = items.get(position).get(1);
            String cnt = items.get(position).get(2);
            text1.setText(nm);
            text2.setText(pri);
            text3.setText(cnt);

            final Button button1 = (Button)v.findViewById(R.id.button1);
            final Button button2= (Button)v.findViewById(R.id.button2);
            final CheckBox ck = (CheckBox) v.findViewById(R.id.checkBox);


            if(select==false){   //선택모드가 아닐때
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                ck.setVisibility(View.GONE);
                button1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {


                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        Toast.makeText(MYCART.this,"+", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(MYCART.this,"100이상은 넣을수 없습니다", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        String product = items.get(key_p).get(0);
                        AddRequest addRequest = new AddRequest(product,id, responseListener);
                        Log.i("product", product);
                        RequestQueue queue = Volley.newRequestQueue(MYCART.this);
                        queue.add(addRequest);
                        Intent intent = new Intent(MYCART.this, MYCART.class);
                        intent.putExtra("name",id);
                        MYCART.this.finish();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }

                });

                button2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        Toast.makeText(MYCART.this,"-", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(MYCART.this,"0이하로는 떨어질수 없습니다", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        String getproduct = items.get(key_p).get(0);
                        SubRequest subRequest = new SubRequest(getproduct,id, responseListener);

                        Log.i("product",getproduct);
                        RequestQueue queue = Volley.newRequestQueue(MYCART.this);
                        queue.add(subRequest);
                        Intent intent = new Intent(MYCART.this, MYCART.class);
                        intent.putExtra("name",id);
                        MYCART.this.finish();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }

                });

            }
            else {   //선택모드일때

                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                ck.setVisibility(View.VISIBLE);

                if(all_select) ck.setChecked(true);  // 전체선택시
                else if(all_cancle)  ck.setChecked(false);  //전체취소시
                if(delete==true&&ck.isChecked()) {//Toast.makeText(MYCART.this,items.get(key_p).get(1), Toast.LENGTH_SHORT).show();  //제거시
                    final String d_name =items.get(key_p).get(0) ;
                    final String d_id = id ;
                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if(!success){
                                    Toast.makeText(MYCART.this,"제거되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MYCART.this, MYCART.class);
                                    intent.putExtra("name",id);
                                    MYCART.this.finish();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MYCART.this);
                                    builder.setMessage(" 카트에 존재하지 않는 물품입니다. ")
                                            .setNegativeButton("확인",null)
                                            .create()
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    MYCARTRequest MYCARTREQUEST = new MYCARTRequest(d_name,d_id,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MYCART.this);
                    queue.add(MYCARTREQUEST);

                }

                ck.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        all_select=false;
                        all_cancle=false;
                        delete=false;
                    }
                });
            }
            return v;
        }
    }

    protected void showList(final String id){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            product = jsonObj.getJSONArray("result");

            for(int i=0;i<product.length();i++){
                JSONObject c = product.getJSONObject(i);
                String name = c.getString("name");
                String price = c.getString("price");
                String amount=c.getString("amount");
                if(id.equals(c.getString("id"))) {
                    total += Integer.parseInt(price) * Integer.parseInt(amount);
                    ArrayList<String> persons = new ArrayList<String>();
                    persons.add(name);
                    persons.add(price);
                    persons.add(amount);
                    productList.add(persons);
                }
            }
            sum.setText("합계 "+Integer.toString(total)+" 원");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String url,final String id){
        class GetDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                try {
                    String uri = params[0];
                    BufferedReader bufferedReader = null;
                    URL url = new URL(uri);
                    HttpURLConnection dbc = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(dbc.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList(id);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void onClick(View view) {   //담기버튼
        Log.d("id",id);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id",id);//id값 메인엑티비티로전달
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY );
        startActivity(intent);
        CustomAdapter adapter = (CustomAdapter)list.getAdapter();
        adapter.notifyDataSetChanged();
    }
    public void onClick1(View view){    //제거버튼
        if(select==true) {  //선택모드일때
            all_select = false;
            all_cancle = false;
            delete = true;
            CustomAdapter adapter = (CustomAdapter) list.getAdapter();
            adapter.notifyDataSetChanged();
        }
        else{     // 선택모드가 아닐때
            Log.d("item","눌렀음");
            if(name==null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MYCART.this);
                builder.setMessage("제거할 제품을 선택하여주십시오")
                        .setNegativeButton("다시입력",null)
                        .create()
                        .show();
                Log.d("item","없음");
            }
            else {
                //  Toast.makeText(this, name, Toast.LENGTH_SHORT);
                final String d_name = name ;
                final String d_id = id ;

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(!success){
                                Toast.makeText(MYCART.this,"제거되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MYCART.this, MYCART.class);
                                intent.putExtra("name",id);
                                MYCART.this.finish();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(MYCART.this);
                                builder.setMessage(" 카트에 존재하지 않는 물품입니다. ")
                                        .setNegativeButton("확인",null)
                                        .create()
                                        .show();
                            }

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                MYCARTRequest MYCARTREQUEST = new MYCARTRequest(d_name,d_id,responseListener);
                RequestQueue queue = Volley.newRequestQueue(MYCART.this);
                queue.add(MYCARTREQUEST);
                Log.d("item",name+"을 출력");
            }
        }
    }

    public void onClick5(View view){    //새로고침
        CustomAdapter adapter = (CustomAdapter)list.getAdapter();
        adapter.notifyDataSetChanged();

    }

}
