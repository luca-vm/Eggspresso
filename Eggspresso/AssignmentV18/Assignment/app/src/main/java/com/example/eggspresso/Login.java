package com.example.eggspresso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Login extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    String sType;
    String foundPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().hide();

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnStaff = findViewById(R.id.btnStaff);
        Button btnCust = findViewById(R.id.btnCust);
        EditText edtUsername = findViewById(R.id.edtCreateUsername);
        EditText edtPassword = findViewById(R.id.edtPassword);
        TextView txtLogin = findViewById(R.id.txtLogin);
        TextView txtMoveCreate = findViewById(R.id.txtMoveCreate);
        LinearLayout llLogin = findViewById(R.id.llLogin);

        llLogin.setVisibility(View.INVISIBLE);

        btnStaff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txtLogin.setText("Staff Login");
                llLogin.setVisibility(View.VISIBLE);
                sType = "Staff";
            }
        });

        btnCust.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txtLogin.setText("Customer Login");
                llLogin.setVisibility(View.VISIBLE);
                sType = "Customer";
            }
        });

        txtMoveCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Login.this, CreateAccount.class);
                i.putExtra("s",sType);
                startActivity(i);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                foundPassword = "";

                if(edtUsername.getText().length()==0)
                {
                    edtUsername.setError("Field cannot be left blank.");
                    return;
                }

                if(edtPassword.getText().length()==0)
                {
                    edtPassword.setError("Field cannot be left blank.");
                    return;
                }

                String Username = edtUsername.getText().toString();
                String Password = edtPassword.getText().toString();

                //Database check
                PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/~s2432389/");
                ContentValues cv = new ContentValues();


                if (sType.equals("Customer")){
                    cv.put("CustUsername", Username);
                    req.doRequest(Login.this, "LoginCustomer", cv,
                            new RequestHandler() {
                                public void processResponse(String response) {

                                    processLogin(response);

                                    if (foundPassword.equals("empty")){
                                        edtUsername.setError("Username not found");
                                        return;
                                    }

                                    if (!(foundPassword.equals(Password))){
                                        edtPassword.setError("Password incorrect");
                                        return;
                                    }

                                    Context context = getApplicationContext();
                                    CharSequence text = "Login Successful";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    Intent c = new Intent(Login.this, Customer.class);
                                    c.putExtra("s",Username);
                                    startActivity(c);
                                }
                            });

                }
                else
                {
                    cv.put("StaffUsername", Username);
                    req.doRequest(Login.this, "LoginStaff", cv,
                            new RequestHandler() {
                                public void processResponse(String response) {

                                    processLogin(response);

                                    if (foundPassword.equals("empty")){
                                        edtUsername.setError("Username not found");
                                        return;
                                    }

                                    if (!(foundPassword.equals(Password))){
                                        edtPassword.setError("Password incorrect");
                                      return;
                                    }

                                    Context context = getApplicationContext();
                                    CharSequence text = "Login Successful";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    Intent s = new Intent(Login.this, Staff.class);
                                    s.putExtra("s",Username);
                                    startActivity(s);
                                }
                            });

                }


                edtUsername.getText().clear();
                edtPassword.getText().clear();

            }
        });

    }
    private void processLogin(String json) {
        JSONArray ja = null;
        try {
            ja = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ja.length() == 0){
            foundPassword = "empty";
            return;
            }

        JSONObject jo = null;
        try {
            jo = ja.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String rawPassword = jo.toString();

        String[] vals = rawPassword.split(":");
        for (int i = 1; i < vals[1].length() - 2; i++){
            foundPassword = foundPassword + vals[1].charAt(i);
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

