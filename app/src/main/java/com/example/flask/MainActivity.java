package com.example.flask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String url = "http://" + "112.150.84.136" + ":" + 5000 + "/query/TEST";
    private Button connect;
    private String postBodyString;
    private MediaType mediaType;
    private RequestBody requestBody;
    private EditText text;
    private TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect = (Button)findViewById(R.id.btn_Send);
        text = (EditText)findViewById(R.id.edit_query);
        view = (TextView)findViewById(R.id.txt_view);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String meg = text.getText().toString();
                postRequest(meg, url);
            }
        });
    }
    private RequestBody buildRequestBody(String msg) {

            JSONObject jsonInput = new JSONObject();
            try {
                jsonInput.put("query", msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody reqBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonInput.toString()
            );

        /*postBodyString = jsonInput.toString();
        mediaType = MediaType.parse("application/json; charset=utf-8");
        requestBody = RequestBody.create(postBodyString, mediaType);*/
        return reqBody;
    }
    private void postRequest(String message, String URL)
    {
        RequestBody requestBody = buildRequestBody(message);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(URL)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();

                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String message = response.body().string();
                            JSONObject jObject = new JSONObject(message);
                            String title = jObject.getString("Answer");

                            view.setText(title);
                            Toast.makeText(MainActivity.this,title, Toast.LENGTH_LONG).show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                }
                });
            }
        });
    }

}