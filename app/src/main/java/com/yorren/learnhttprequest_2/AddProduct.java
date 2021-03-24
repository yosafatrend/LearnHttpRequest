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

public class AddProduct extends AppCompatActivity {
    EditText edtName, edtCost, edtQuantity;
    Button btnSubmit;
    JSONObject jsonData;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        btnSubmit = findViewById(R.id.btnSubmit);
        edtName = findViewById(R.id.edtNameAdd);
        edtCost = findViewById(R.id.edtCostAdd);
        edtQuantity = findViewById(R.id.edtQuantityAdd);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductAdd();
            }
        });
    }

    private void ProductAdd() {
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

        new PostAdd().execute();
    }

    private class PostAdd extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AddProduct.this);
            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            httpHandler.performPostCall(HostURL.URL +"products", jsonData.toString(), AddProduct.this);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            startActivity(new Intent(AddProduct.this, MainActivity.class));
        }
    }
}