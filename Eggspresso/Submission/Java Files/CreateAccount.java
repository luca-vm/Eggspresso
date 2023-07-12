package com.example.eggspresso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.androidgamesdk.gametextinput.Listener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAccount extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    Boolean foundAccount;
    String CAType;
    String RestOrName, CreateUsername,CreatePassword, RepPass;
    EditText edtCreateUsername;

    PHPRequest reqCreate = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
    PHPRequest reqCheckExists = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
        getSupportActionBar().hide();

        Intent j = getIntent();
        CAType = j.getStringExtra("s");


        TextView txtCreate = findViewById(R.id.txtCreate);
        txtCreate.setText("Create " + CAType + " Account");

        TextView txtRestOrName = findViewById(R.id.txtRestOrName);
        if (CAType.equals("Customer")) {
            txtRestOrName.setText("Real Name:");
        } else {
            txtRestOrName.setText("Resturant Name:");
        }

        TextView txtReturn = findViewById(R.id.txtReturn);
        Button btnCreate = findViewById(R.id.btnCreate);
        EditText edtRestOrName = findViewById(R.id.edtRestOrName);
        EditText edtCreatePassword = findViewById(R.id.edtCreatePassword);
        EditText edtRepPass = findViewById(R.id.edtRepPass);
        edtCreateUsername = findViewById(R.id.edtCreateUsername);


        txtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(CreateAccount.this, Login.class);
                startActivity(k);
            }
        });



        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtRestOrName.getText().length() == 0) {
                    edtRestOrName.setError("Field cannot be left blank.");
                    return;
                }

                if (edtCreateUsername.getText().length() == 0) {
                    edtCreateUsername.setError("Field cannot be left blank.");
                    return;
                }

                if (edtCreatePassword.getText().length() == 0) {
                    edtCreatePassword.setError("Field cannot be left blank.");
                    return;
                }

                if (edtCreatePassword.getText().length() < 8) {
                    edtCreatePassword.getText().clear();
                    edtRepPass.getText().clear();
                    edtCreatePassword.setError("Password is too short (min 8 characters).");
                    return;
                }

                if (edtRepPass.getText().length() == 0) {
                    edtRepPass.setError("Field cannot be left blank.");
                    return;
                }

                if (!((edtRepPass.getText().toString()).equals(edtCreatePassword.getText().toString()))) {
                    edtRepPass.getText().clear();
                    edtCreatePassword.getText().clear();
                    edtRepPass.setError("Passwords do not match");
                    return;
                }

                 RestOrName = edtRestOrName.getText().toString();
                 CreateUsername = edtCreateUsername.getText().toString();
                 CreatePassword = edtCreatePassword.getText().toString();
                 RepPass = edtRepPass.getText().toString();

                //Check if account already exits
                ContentValues cvCheck = new ContentValues();

                if (CAType.equals("Customer")){
                    cvCheck.put("CustUsername", CreateUsername);
                    reqCheckExists.doRequest(CreateAccount.this, "LoginCustomer", cvCheck,
                            new RequestHandler() {
                                public void processResponse(String response) {
                                    processCheckExists(response);
                                    Insert();
                                }
                            });
                }
                else
                {
                    cvCheck.put("StaffUsername", CreateUsername);
                    reqCheckExists.doRequest(CreateAccount.this, "LoginStaff", cvCheck,
                            new RequestHandler() {
                                public void processResponse(String response) {
                                    processCheckExists(response);
                                    Insert();

                                }
                            });
                }


            }
        });

    }

    private void processCreate(String response) {
        Context context = getApplicationContext();
        CharSequence text = "Account Created";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    private void Insert() {

        if (!foundAccount) {


            //Insert new account
            ContentValues cv = new ContentValues();


            if (CAType.equals("Customer")) {

                cv.put("CustUsername", CreateUsername);
                cv.put("CustPassword", CreatePassword);
                cv.put("CustName", RestOrName);

                reqCreate.doRequest(CreateAccount.this, "CreateAccountCustomer", cv,
                        new RequestHandler() {
                            public void processResponse(String response) {

                                processCreate(response);
                            }
                        });

                Intent c = new Intent(CreateAccount.this, Customer.class);
                c.putExtra("s", CreateUsername);
                startActivity(c);
            } else {
                cv.put("StaffUsername", CreateUsername);
                cv.put("StaffPassword", CreatePassword);
                cv.put("AvgRating", 1);
                cv.put("Restaurant", RestOrName);

                reqCreate.doRequest(CreateAccount.this, "CreateAccountStaff", cv,
                        new RequestHandler() {
                            public void processResponse(String response) {

                                processCreate(response);
                            }
                        });
                Intent s = new Intent(CreateAccount.this, Staff.class);
                s.putExtra("s", CreateUsername);
                startActivity(s);
            }

        }
        else
        {
            edtCreateUsername.setError("Username already exists.");
            return;
        }

        return;

    }


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Call post(String url, String json, Callback callback){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }

    private void processCheckExists(String json) {
        JSONArray ja = null;
        try {
            ja = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ja.length() != 0){
            foundAccount = true;
            return;
        }
        else
        {
            foundAccount = false;
            return;
        }
    }

}