package com.example.eggspresso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Customer extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    Spinner spnOrd;
    String CustUsername;
    ArrayList<String> Orders = new ArrayList<String>();
    String OrdDets[] = new String[7];
    int Rating;

    TextView txtRestName,txtTime, txtStatus, txtStaff, txtRating, txtNotice;
    ImageView imgThumbUp, imgThumbDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer);
        getSupportActionBar().hide();

        ImageView imgHome = findViewById(R.id.imgHome);

        imgHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent h = new Intent(Customer.this, Login.class);
                startActivity(h);
            }
        });


        Intent j = getIntent();
        CustUsername = j.getStringExtra("s");

        TextView txtCustUsername = findViewById(R.id.txtCustUsername);

        txtCustUsername.setText(CustUsername);

        SelectOrd();

        txtRestName = findViewById(R.id.txtRestName);
        txtTime = findViewById(R.id.txtTime);
        txtStatus = findViewById(R.id.txtStatus);
        txtStaff = findViewById(R.id.txtStaff);
        txtRating = findViewById(R.id.txtRating);
        txtNotice = findViewById(R.id.txtNotice);
        imgThumbUp = findViewById(R.id.imgThumbUp);
        imgThumbDown = findViewById(R.id.imgThumbDown);

        spnOrd = findViewById(R.id.spnOrd);
        spnOrd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                OrdDets();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            return;
            }

        });


        imgThumbUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rating = 1;
                Update();
            }
        });
        imgThumbDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rating = 0;
                Update();
            }
        });


    }
    private void Update() {

        if  (txtNotice.getVisibility() == View.VISIBLE){
        return;
        }

        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put("OrderNo", spnOrd.getSelectedItem().toString());
        cvUpdate.put("Rating", Rating);
        PHPRequest reqUpdate = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");

        reqUpdate.doRequest(Customer.this, "EditRating", cvUpdate,
                new RequestHandler() {
                    public void processResponse(String response) {
                        processUpdate(response);
                        OrdDets();
                    }
                });
    }

    private void processUpdate(String response) {
        Context context = getApplicationContext();
        CharSequence text = "Thank you for your feedback!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    private void OrdDets() {

        PHPRequest reqOrdDets = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
        ContentValues cvOrdDets = new ContentValues();
        cvOrdDets.put("OrderNo", spnOrd.getSelectedItem().toString());

       for (int i = 0; i < 7; i++){
           OrdDets[i] = "";
       }

        reqOrdDets.doRequest(Customer.this, "OrderDetails", cvOrdDets,
                new RequestHandler() {
                    public void processResponse(String response) {

                        processOrderDetails(response);
                        txtStaff.setText("Staff Member: " + OrdDets[2]);

                        if (OrdDets[3].equals("P")){
                            txtStatus.setText("Order Status: Pending");
                        }
                        else if (OrdDets[3].equals("R"))
                        {
                            txtStatus.setText("Order Status: Ready");
                        }
                        else {
                            txtStatus.setText("Order Status: Collected");
                        }

                        txtTime.setText("Order Date and Time: " + OrdDets[4]);
                        txtRestName.setText("Restaurant: " + OrdDets[5]);

                        if (OrdDets[6].equals("1")){
                            txtRating.setText("Customer Experience: Good");
                        }
                        else
                       {
                            txtRating.setText("Customer Experience: Poor");
                        }

                    }
                });



    }
    private void processOrderDetails(String json) {
        JSONArray ja = null;
        try {
            ja = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i <ja.length(); i++) {
            JSONObject  jo = null;
            try {
                jo = ja.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String rawString = jo.toString();
            String newrawString = "";

            for (int l = 1; l < rawString.length() - 1; l++){
                newrawString = newrawString + rawString.charAt(l);
            }

            for (int m = 0; m < 7; m++){
                OrdDets[m] = "";
            }

            String[] fields = newrawString.split(",");
            String date = "";
            int numColon = 0;
            for(int n = 0; n < fields[4].length(); n++){
                if (fields[4].charAt(n) != ':'){
                    date += fields[4].charAt(n);
                }
                else
                {
                    numColon += 1;
                    if (numColon != 1){
                        date += "-";
                    }
                    else
                    {
                        date += ':';
                    }
                }
            }
            fields[4] = date;
            for (int k = 0; k < 7; k++) {
                String[] vals = fields[k].split(":");
                for (int j = 1; j < vals[1].length() - 1; j++) {
                    OrdDets[k] = OrdDets[k] + vals[1].charAt(j);
                }
            }
        }

    }
    private void SelectOrd() {
        // Select order

        PHPRequest reqSpnOrd = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
        ContentValues cvSpnOrd = new ContentValues();
        cvSpnOrd.put("CustUsername", CustUsername);

        reqSpnOrd.doRequest(Customer.this, "PopCustList", cvSpnOrd,
                new RequestHandler() {
                    public void processResponse(String response) {

                        processOrders(response);

                        String OrdersArray[] = new String[Orders.size()];

                        for (int j = 0; j < Orders.size(); j++) {
                            OrdersArray[j] = Orders.get(j);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Customer.this, android.R.layout.simple_spinner_dropdown_item,  OrdersArray);
                        spnOrd.setAdapter(adapter);

                    }
                });



    }
    private void processOrders(String json) {
        JSONArray ja = null;
        try {
            ja = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ja.length() == 0){
            txtNotice.setVisibility(View.VISIBLE);
            return;
        }

        for (int i = 0; i <ja.length(); i++) {
            JSONObject jo = null;
            try {
                jo = ja.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String rawString = jo.toString();

            Orders.add("");
            String[] vals = rawString.split(":");
            for (int j = 1; j < vals[1].length() - 2; j++) {
                Orders.set(i, Orders.get(i) + vals[1].charAt(j));
            }
        }
    }
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Call post(String url, String json, Callback callback){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }
}