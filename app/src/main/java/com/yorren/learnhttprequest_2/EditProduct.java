package com.yorren.learnhttprequest_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProduct extends AppCompatActivity {
    Button btnSubmit;
    EditText edtName, edtCost, edtQuantity;
    String id;
    JSONObject jsonData;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        edtName = findViewById(R.id.edtName);
        edtCost = findViewById(R.id.edtCost);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnSubmit = findViewById(R.id.btnSubmit);

        id =getIntent().getStringExtra("id");
        edtName.setText(getIntent().getStringExtra("name"));
        edtCost.setText(getIntent().getStringExtra("cost"));
        edtQuantity.setText(getIntent().getStringExtra("quantity"));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
    }

    private void Update() {
        String name = edtName.getText().toString();
        String cost = edtCost.getText().toString();
        String quantity = edtQuantity.getText().toString();

        if (name.isEmpty()){
            edtName.setError("Please insert name");
        }
        if (cost.isEmpty()){
            edtCost.setError("Please insert cost");
        }
        if (quantity.isEmpty()){
            edtQuantity.setError("Please insert quantity");
        }
        try {
            jsonData = new JSONObject();
            jsonData.put("name", name);
            jsonData.put("cost", cost);
            jsonData.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new PostUpdate().execute();
    }

    private class PostUpdate extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditProduct.this);
                        progressDialog.setMessage("Please wait..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            //httpHandler.performPutCall(HostURL.URL +"products/" +id, jsonData.toString(), EditProduct.this);
            httpHandler.performDeleteCall(HostURL.URL +"products/" +id, EditProduct.this);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            startActivity(new Intent(EditProduct.this, MainActivity.class));
        }
    }
}