package com.yorren.learnhttprequest_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    Button btnSubmit, btnLogin;
    EditText edtUsername, edtEmail, edtPassword;
    JSONObject jsonData;
    ProgressDialog progressDialog;
    String final_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogin = findViewById(R.id.btnLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register() {
        String username = edtUsername.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (username.isEmpty()) {
            edtUsername.setError("Please insert username");
        }
        if (email.isEmpty()) {
            edtEmail.setError("Please insert email");
        }
        if (password.isEmpty()) {
            edtPassword.setError("Please insert password");
        }
        try {
            jsonData = new JSONObject();
            jsonData.put("username", username);
            jsonData.put("email", email);
            jsonData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new PostRegist().execute();
    }

    private class PostRegist extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RegisterActivity.this);
                        progressDialog.setMessage("Please wait..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            String jsonResponse = httpHandler.AuthRegist(HostURL.URL + "auth/register", jsonData.toString());
            final_token = null;

            if (jsonResponse != null) {
                try {
                    JSONObject res = new JSONObject(jsonResponse);
                    String access_token = res.getString("access_token");
                    final_token = access_token;
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
            } else {
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
            if (final_token!= null){
                progressDialog.dismiss();
                Snackbar.make(findViewById(R.id.parentLayout), "Register success", Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
              //  finish();
            }else{
                progressDialog.dismiss();
                Snackbar.make(findViewById(R.id.parentLayout), "Email already taken", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}