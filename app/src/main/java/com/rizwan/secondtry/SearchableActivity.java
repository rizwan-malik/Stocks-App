package com.rizwan.secondtry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchableActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Hellooooooooooooooo", "NVDA");
        setContentView(R.layout.activity_searchable);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String ticker = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(), ticker, Toast.LENGTH_SHORT).show();
            System.out.println(ticker);

            if (ticker.length() >= 3){
                doMySearch(ticker);
            }
        }
    }

    private void doMySearch(String ticker){

        requestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:3000/autocomplete/" + ticker;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try{
                // Loop through the array elements
                for(int i=0;i<response.length();i++){
                    // Get current json object
                    JSONObject searchedItem = response.getJSONObject(i);

                    // Get the autocomplete data and set the variables

                    String imageUrl = searchedItem.getString("urlToImage");
                    String newsTitle = searchedItem.getString("title");
                    String newsUrl = searchedItem.getString("url");
                    System.out.println("Hellooooooooooooooooooooooooooooooooooooooooooooooo" + '\n');



                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }, error -> error.printStackTrace());

        requestQueue.add(request);

    }

}