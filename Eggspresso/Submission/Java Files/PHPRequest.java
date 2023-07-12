package com.example.eggspresso;

import android.app.Activity;
import android.content.ContentValues;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

    public class PHPRequest{
        String url;
        public PHPRequest(String prefix){
            url=prefix;
        }
        public void doRequest(Activity a, String method, ContentValues params, RequestHandler rh){
            OkHttpClient client;
            client = new OkHttpClient();

            FormBody.Builder builder = new FormBody.Builder();

            for (String key:params.keySet()){
                builder.add(key, params.getAsString(key));
            }

            Request request = new Request.Builder().url(url+method+".php").post(builder.build()).build();
            client.newCall (request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String responseData = response.body().string();
                    a.runOnUiThread(new Runnable(){
                        public void run(){
                            rh.processResponse(responseData);
                        }
                    });
                }

                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
            });

        }
    }

