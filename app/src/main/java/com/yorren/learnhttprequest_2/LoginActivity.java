package com.yorren.learnhttprequest_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends AppCompatActivity {
    Button btnSubmit, btnRegist;
    EditText edtEmail, edtPassword;
    private String final_token;
    JSONObject jsonData;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnRegist = findViewById(R.id.btnRegist);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        new checkConnection().execute();

        btnRegist.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        btnSubmit.setOnClickListener(v -> Login());
    }

    private void Login() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        if (email.isEmpty()){
            edtEmail.setError("Please input email");
        }
        if (password.isEmpty()){
            edtPassword.setError("Please input password");
        }
        try {
            jsonData = new JSONObject();
            jsonData.put("email", email);
            jsonData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new PostLogin().execute();
    }

    private class checkConnection extends AsyncTask<String, Void, Boolean>{
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                java.net.URL myUrl = new URL(HostURL.URL);
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(6000);
                connection.connect();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                progressDialog.dismiss();
                SharedPreferences token = getApplicationContext().getSharedPreferences("access-token", Context.MODE_PRIVATE);
                String access_token = token.getString("TOKEN", null);
                if (access_token != null){
                   startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }else{
                progressDialog.dismiss();
                Snackbar.make(findViewById(R.id.parentLayout), "Connection failed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private class PostLogin extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Please wait..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            String jsonResponse = httpHandler.AuthLogin(HostURL.URL + "auth/login/", jsonData.toString());
            final_token = null;
            if (jsonResponse != null){
                try {
                    JSONObject res = new JSONObject(jsonResponse);
                    final_token = res.getString("access_token");

                    Log.d("TOKEN", "tokeen" + final_token);
                } catch (JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }else {
                Log.e("TAG", "Could not get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Could not get json from server.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
          //  super.onPostExecute(s);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if (final_token != null){
                Snackbar.make(findViewById(R.id.parentLayout), "Login success", Snackbar.LENGTH_SHORT).show();
                SharedPreferences token = getApplicationContext().getSharedPreferences("access-token", Context.MODE_PRIVATE);
                        token.edit().putString("TOKEN", final_token).apply();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }else{
                Snackbar.make(findViewById(R.id.parentLayout), "Wrong username or password", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}