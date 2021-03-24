package com.yorren.learnhttprequest_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView listView;
    ArrayList<HashMap<String, String>> todoJsonList;
    JSONObject jsonData;
    int res_code = 0;
    FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoJsonList = new ArrayList<>();
        listView = findViewById(R.id.listview);
        btnAdd = findViewById(R.id.floatingButton);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddProduct.class));
            }
        });

        new GetTodo().execute();
    }

    private class GetTodo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, todoJsonList,
                    R.layout.list_item, new String[]{"name", "cost", "quantity", "id"},
                    new int[]{R.id.userId, R.id.id, R.id.title, R.id.idd}
            );

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                    String name = item.get("name");
                    String cost = item.get("cost");
                    String quantity = item.get("quantity");
                    String idd = item.get("id");

                    Intent intent = new Intent(MainActivity.this, EditProduct.class);
                    intent.putExtra("name", name);
                    intent.putExtra("cost", cost);
                    intent.putExtra("quantity", quantity);
                    intent.putExtra("id", idd);
                    startActivity(intent);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();

            String jsonString = httpHandler.makeServiceCall(HostURL.URL+"products/", MainActivity.this);
            Log.e(TAG, "Response from url: " + jsonString);

            if (jsonString != null) {
                try {
                    if (jsonString == "401"){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Login expired",
                                        Toast.LENGTH_LONG)
                                        .show();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        });
                    }
                    JSONArray jsonArray = new JSONArray(jsonString);

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject a = jsonArray.getJSONObject(i);
                        String id = a.getString("id");
                        String name = a.getString("name");
                        String isComplete = a.getString("cost");
                        String completedAt = a.getString("quantity");

                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("cost", isComplete);
                        contact.put("quantity", completedAt);

                        // adding contact to contact list
                        todoJsonList.add(contact);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                Log.e(TAG, "Could not get json from server.");
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
    }
}