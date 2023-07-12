package com.example.eggspresso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Staff extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    String StaffDets[] = new String[4];
    ArrayList<String> CustDets = new ArrayList<String>();
    ArrayList<String> StaffOrds = new ArrayList<String>();
    String StaffUsername, Status;
    RadioButton rbReadyEdit, rbCollectedEdit;
    Spinner spnEdit, spnOrd;
    TextView txtRest;
    ImageView imgThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff);
        getSupportActionBar().hide();

        ImageView imgHome = findViewById(R.id.imgHome);

        imgHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent h = new Intent(Staff.this, Login.class);
                startActivity(h);
            }
        });

        Intent j = getIntent();
        StaffUsername = j.getStringExtra("s");

        TextView txtStaffUsername = findViewById(R.id.txtStaffUsername);
        txtRest = findViewById(R.id.txtRest);
        imgThumb = findViewById(R.id.imgThumb);
        txtStaffUsername.setText(StaffUsername);
        rbReadyEdit = findViewById(R.id.rbReadyEdit);
        rbCollectedEdit = findViewById(R.id.rbCollectedEdit);

        StaffDetails();
        SelectCust();

        //add an order

        PHPRequest reqAdd = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnEdit = findViewById(R.id.btnEdit);
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String CustUsername = spnOrd.getSelectedItem().toString();

                ContentValues cv = new ContentValues();
                cv.put("StaffUsername", StaffUsername );
                cv.put("CustUsername", CustUsername);
                cv.put("Status", "P");
                cv.put("OrderTime",strDate);
                cv.put("Restaurant",StaffDets[3]);
                cv.put("Rating",1);
                reqAdd.doRequest(Staff.this, "PlaceOrder", cv,
                        new RequestHandler() {
                            public void processResponse(String response) {

                                processAdd(response);
                                StaffOrds.clear();
                                SelectOrd();
                            }
                        });
            }
        });

       SelectOrd();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Update();

            }
        });

    }
    private void StaffDetails() {
        //Staff Details
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
        ContentValues cv = new ContentValues();

        cv.put("StaffUsername", StaffUsername);
        req.doRequest(Staff.this, "StaffDetails", cv,
                new RequestHandler() {
                    public void processResponse(String response) {

                        processStaffDetails(response);

                        txtRest.setText(StaffDets[3]);


                        if (StaffDets[2].equals("1")){
                            imgThumb.setImageResource(R.drawable.thumbsup);

                        }
                        else
                        {
                            imgThumb.setImageResource(R.drawable.downthumb);
                        }
                    }
                });
    }
    private void SelectCust() {
        // Select customer
        spnOrd = findViewById(R.id.spnOrd);

        PHPRequest reqSpnOrd = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
        ContentValues cvSpnOrd = new ContentValues();

        reqSpnOrd.doRequest(Staff.this, "CustomerDetails", cvSpnOrd,
                new RequestHandler() {
                    public void processResponse(String response) {

                        processCustomerDetails(response);

                        String CustDetsArray[] = new String[CustDets.size()];

                        for (int j = 0; j < CustDets.size(); j++) {
                            CustDetsArray[j] = CustDets.get(j);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Staff.this, android.R.layout.simple_spinner_dropdown_item, CustDetsArray);
                        spnOrd.setAdapter(adapter);

                    }
                });
    }

    private void Update() {
        if(rbReadyEdit.isChecked())
        {
            Status = "R";  // is checked
        }
        else if (rbCollectedEdit.isChecked())
        {
            Status = "C";
        }
        else
        {

            Context context = getApplicationContext();
            CharSequence text = "No Status Selected";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return;

        }


        String EditDropDown = spnEdit.getSelectedItem().toString();
        String OrderNo = "" + EditDropDown.charAt(0) +  EditDropDown.charAt(1) + EditDropDown.charAt(2);
        ContentValues cv = new ContentValues();
        cv.put("Status", Status);
        cv.put("OrderNo", OrderNo);
        PHPRequest reqEdit = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
        reqEdit.doRequest(Staff.this, "EditOrder", cv,
                new RequestHandler() {
                    public void processResponse(String response) {

                        processEdit(response);
                    }
                });
    }

    private void processEdit(String response) {
        Context context = getApplicationContext();
        CharSequence text = "Order Status Updated";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    private void SelectOrd() {
        // Select Ord
        spnEdit = findViewById(R.id.spnEdit);

        PHPRequest reqSpnEdit = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
        ContentValues cvSpnEdit = new ContentValues();
        cvSpnEdit.put("StaffUsername", StaffUsername );

        reqSpnEdit.doRequest(Staff.this, "PopList", cvSpnEdit,
                new RequestHandler() {
                    public void processResponse(String response) {

                        processOrder(response);

                        String StaffOrdsArray[] = new String[StaffOrds.size()];

                        for (int j = 0; j < StaffOrds.size(); j++) {
                            StaffOrdsArray[j] = StaffOrds.get(j);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Staff.this, android.R.layout.simple_spinner_dropdown_item, StaffOrdsArray);
                        spnEdit.setAdapter(adapter);

                    }
                });

    }

    private void processOrder(String json) {
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

            StaffOrds.add("");
            String[] fields = rawString.split(",");
            for (int m = 0; m < 2; m++) {

                String[] vals = fields[m].split(":");
                for (int k = 1; k < vals[1].length() - 1; k++) {
                    if (!((m == 1)&&(k == vals[1].length() - 2))){
                        StaffOrds.set(i, StaffOrds.get(i) + vals[1].charAt(k));
                    }

                }

               if (m == 0){
                   StaffOrds.set(i, StaffOrds.get(i) + " - ");
                }
            }

        }



    }

    private void processAdd(String json) {
        Context context = getApplicationContext();
        CharSequence text = "Order added";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    private void processCustomerDetails(String json) {
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

             CustDets.add("");
                String[] vals = rawString.split(":");
                for (int j = 1; j < vals[1].length() - 2; j++) {
                    CustDets.set(i, CustDets.get(i) + vals[1].charAt(j));
                }
        }
    }

    private void processStaffDetails(String json) {
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

            for (int m = 0; m < 4; m++){
                StaffDets[m] = "";
            }

            String[] fields = newrawString.split(",");
            for (int k = 0; k < 4; k++) {
                String[] vals = fields[k].split(":");
                for (int j = 1; j < vals[1].length() - 1; j++) {
                    StaffDets[k] = StaffDets[k] + vals[1].charAt(j);
                }
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