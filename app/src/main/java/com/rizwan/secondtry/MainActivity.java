package com.rizwan.secondtry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private boolean isItemClicked = false;

    private boolean spinnerStart = true;
    private boolean stocksBuyingStart = true;

    private int lengthOfRecyclerView;
    private int localIndex = 0;

    private static final String TAG = "MainActivity";
    List<MainRecyclerViewSection> mainRecyclerViewSectionList = new ArrayList<>();
    RecyclerView mainRecyclerView;
    MainRecyclerAdapter mainRecyclerAdapter;

    ConstraintLayout spinnerLayout;
    NestedScrollView originalScrollView;

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    //    For Shared Preferences
    private static final String mySharedPreference = "mySharedPreference";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String sectionOneName = "PORTFOLIO";
    List<FavoriteRecyclerItem> sectionOneItems = new ArrayList<>();
    String sectionTwoName = "FAVORITES";
    List<FavoriteRecyclerItem> sectionTwoItems = new ArrayList<>();
    private RequestQueue requestQueue;

    DecimalFormat df2 = new DecimalFormat("0.00");
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        sharedPreferences = getApplicationContext().getSharedPreferences(mySharedPreference, Context.MODE_PRIVATE);

        initData();

//      Creating the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(myToolbar);

        turnSpinnerOn();

//      Set date on the recycler view
        TextView dateText = findViewById(R.id.dateText);
        dateText.setText(setDate());

    }

    private String setDate() {
        String date_pattern = "MMMM d, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern, Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public void initData() {

        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        editor.putString("netWorth", "20000.00");
        editor.putString("cash", "20000");
        editor.putString("stockValue", "0");
        editor.putString("portfolio", "AAPL:1.00,TSLA:1.00");
        editor.putString("favorites", "MSFT:0.00:Microsoft Corporation,NVDA:0.00:NVIDIA Corp");
        editor.commit();

        mainRecyclerViewSectionList = new ArrayList<>();
        mainRecyclerAdapter = new MainRecyclerAdapter(mainRecyclerViewSectionList, MainActivity.this);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mainRecyclerView.setAdapter(mainRecyclerAdapter);

    }

    public void buyStocksOnStart(String tickerString){

    }

    @SuppressLint("RestrictedApi")  //Linked to setThreshold method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.searchButton);
        SearchView searchView = (SearchView) searchItem.getActionView();

        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setThreshold(3);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = autoSuggestAdapter.getObject(position).getName();
                String ticker = autoSuggestAdapter.getObject(position).getTicker();

                searchAutoComplete.setText(ticker + " - " + name);
                isItemClicked = true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String[] queryArray = query.split(" - ");
                String ticker = queryArray[0];
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("ticker", ticker);
                startActivity(detailsIntent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (searchAutoComplete.getText().length() == 0) {
                        isItemClicked = false;
                    }
                    if (!TextUtils.isEmpty(searchAutoComplete.getText()) && searchAutoComplete.getText().length() >= 3 && !isItemClicked) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void makeApiCall(String text) {

        AutocompleteApiCall.make(this, text, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<AutocompleteItem> autocompleteList = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject autocompleteResult = response.getJSONObject(i);
                        String name = autocompleteResult.getString("name");
                        String ticker = autocompleteResult.getString("ticker");
                        autocompleteList.add(new AutocompleteItem(ticker, name));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                autoSuggestAdapter.setData(autocompleteList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

//    public void setDataFromSharedPreferences() {
//
//        String netWorth = sharedPreferences.getString("netWorth", null);
//
//        mainRecyclerAdapter = new MainRecyclerAdapter(mainRecyclerViewSectionList, MainActivity.this);
//        mainRecyclerView = findViewById(R.id.mainRecyclerView);
//        mainRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        mainRecyclerView.setAdapter(mainRecyclerAdapter);
//
//        if (sharedPreferences.getString("portfolio", null) != null || sharedPreferences.getString("portfolio", null) != null ) {
//            String portfolioStringArray = sharedPreferences.getString("portfolio", null);
//            String favoritesStringArray = sharedPreferences.getString("favorites", null);
//
//            System.out.println("Portfolio String is " + portfolioStringArray);
//            System.out.println("Favorites String is " + favoritesStringArray);
//
//            String[] portfolioTickersArray = portfolioStringArray.split(",");
//            String[] favoritesTickerArray = favoritesStringArray.split(",");
//            StringBuilder tickerString = new StringBuilder();
//
//            int tempPortfolioLength = 0;
//            int tempFavoritesLength = 0;
//            if(portfolioTickersArray[0].length() > 0){
//                tempPortfolioLength = portfolioTickersArray.length;
//            }
//            if(favoritesTickerArray[0].length() > 0){
//                tempFavoritesLength = favoritesTickerArray.length;
//            }
//
//            lengthOfRecyclerView = tempPortfolioLength + tempFavoritesLength;
//            System.out.println("Length of recyclerview array " + lengthOfRecyclerView);
//            for (int i = 0; i < portfolioTickersArray.length; i++) {
//                String[] tickerDetails = portfolioTickersArray[i].split(":");
//                String ticker = tickerDetails[0];
//                if(ticker.length() > 0){
////                    getRecyclerViewData(ticker);
//                    tickerString.append(",").append(ticker);
//                }
//            }
//            for (int i = 0; i < favoritesTickerArray.length; i++) {
//                String[] tickerDetails = favoritesTickerArray[i].split(":");
//                String ticker = tickerDetails[0];
//                if(ticker.length() > 0){
////                    getRecyclerViewData(ticker);
//                    tickerString.append(",").append(ticker);
//                }
//            }
//
//            getMultipleData(String.valueOf(tickerString));
//
//        } else {
//            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionOneName, "Net Worth", netWorth, sectionOneItems));
//            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionTwoName, "Net Worth", netWorth, sectionTwoItems));
//            mainRecyclerAdapter.notifyDataSetChanged();
//        }
//    }

    public void setDataFromSharedPreferences() {

        String netWorth = sharedPreferences.getString("netWorth", null);


//        mainRecyclerViewSectionList = new ArrayList<>();
//        mainRecyclerAdapter = new MainRecyclerAdapter(mainRecyclerViewSectionList, MainActivity.this);
//        mainRecyclerView = findViewById(R.id.mainRecyclerView);
//        mainRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        mainRecyclerView.setAdapter(mainRecyclerAdapter);

        if ((sharedPreferences.getString("portfolio", null) != null && !sharedPreferences.getString("portfolio", null).trim().isEmpty()) || (sharedPreferences.getString("favorites", null) != null && !sharedPreferences.getString("favorites", null).trim().isEmpty())) {
            String portfolioStringArray = sharedPreferences.getString("portfolio", null);
            String favoritesStringArray = sharedPreferences.getString("favorites", null);

//            System.out.println("Portfolio String is " + portfolioStringArray);
//            System.out.println("Favorites String is " + favoritesStringArray);

            String[] portfolioTickersArray = portfolioStringArray.split(",");
            String[] favoritesTickerArray = favoritesStringArray.split(",");
            StringBuilder tickerString = new StringBuilder();

            for (int i = 0; i < portfolioTickersArray.length; i++) {
                String[] tickerDetails = portfolioTickersArray[i].split(":");
                String ticker = tickerDetails[0];
                if (ticker.length() > 0) {
                    tickerString.append(",").append(ticker);
                }
            }
            for (int i = 0; i < favoritesTickerArray.length; i++) {
                String[] tickerDetails = favoritesTickerArray[i].split(":");
                String ticker = tickerDetails[0];
                if (ticker.length() > 0) {
                    tickerString.append(",").append(ticker);
                }
            }

            getMultipleData(String.valueOf(tickerString));

        } else {
            mainRecyclerViewSectionList = new ArrayList<>();
            mainRecyclerAdapter = new MainRecyclerAdapter(mainRecyclerViewSectionList, MainActivity.this);
            mainRecyclerView = findViewById(R.id.mainRecyclerView);
            mainRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            mainRecyclerView.setAdapter(mainRecyclerAdapter);
            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionOneName, "Net Worth", netWorth, sectionOneItems));
            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionTwoName, "Net Worth", netWorth, sectionTwoItems));
            mainRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public void getRecyclerViewData(String ticker) {
        String url = "http://rizwan-csci571.us-east-1.elasticbeanstalk.com/price-data/" + ticker;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // Loop through the array elements
                    JSONObject priceDataItem = response.getJSONObject(0);

                    String prevClose = priceDataItem.getString("prevClose");
                    String last = priceDataItem.getString("last");
                    String ticker = priceDataItem.getString("ticker");
                    String change = Double.toString(Double.parseDouble(last) - Double.parseDouble(prevClose));

                    setRecyclerViewData(ticker, last, change);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    public void getMultipleData(String tickerString) {
        String url = "http://rizwan-csci571.us-east-1.elasticbeanstalk.com/multiple-tickers-data/" + tickerString;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // Loop through the array elements

                    List<String> responseArrayList = new ArrayList<>();
                    JSONObject priceDataItem;

                    for (int i = 0; i < response.length(); i++) {
                        priceDataItem = response.getJSONObject(i);
                        String prevClose = priceDataItem.getString("prevClose");
                        String last = priceDataItem.getString("last");
                        String ticker = priceDataItem.getString("ticker");
                        double tempChange = Double.parseDouble(last) - Double.parseDouble(prevClose);
                        String change = df2.format(tempChange);
                        responseArrayList.add(ticker + "," + last + "," + change);
                    }

                    setMultiplePriceData(responseArrayList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    public void setMultiplePriceData(List<String> responseArrayList) {

        List<String> justTickersList = new ArrayList<>();
        for (int i = 0; i < responseArrayList.size(); i++) {
            String[] receivedTicker = responseArrayList.get(i).split(",");
            justTickersList.add(receivedTicker[0].toUpperCase());
        }

        String netWorth = sharedPreferences.getString("netWorth", null);
        String cash = sharedPreferences.getString("cash", null);
        String stockValue = sharedPreferences.getString("stockValue", null);
        double netWorthDecimal = Double.parseDouble(netWorth);
        double cashDecimal = Double.parseDouble(cash);
        double stockValueDecimal = Double.parseDouble(stockValue);


        if ((sharedPreferences.getString("portfolio", null) != null && !sharedPreferences.getString("portfolio", null).trim().isEmpty())){
            String portfolioStringArray = sharedPreferences.getString("portfolio", null);
            String[] portfolioTickersArray = portfolioStringArray.split(",");
            for (int i = 0; i < portfolioTickersArray.length; i++) {
                String[] storedTickerItem = portfolioTickersArray[i].split(":");
                int tickerIndex = justTickersList.indexOf(storedTickerItem[0].toUpperCase());
                String[] receivedTickerItem = responseArrayList.get(tickerIndex).split(",");

                FavoriteRecyclerItem favoriteRecyclerItem = new FavoriteRecyclerItem(storedTickerItem[1] + " shares", receivedTickerItem[0], receivedTickerItem[1], receivedTickerItem[2]);
                sectionOneItems.remove(favoriteRecyclerItem);
                sectionOneItems.add(favoriteRecyclerItem);

                if (stocksBuyingStart){
                    double receivedTickerPrice = Double.parseDouble(receivedTickerItem[1]);
                    cashDecimal = cashDecimal - receivedTickerPrice;
                    stockValueDecimal = stockValueDecimal + receivedTickerPrice;
                    netWorthDecimal = cashDecimal + stockValueDecimal;

                    editor.putString("netWorth", Double.toString(netWorthDecimal));
                    editor.putString("stockValue", Double.toString(stockValueDecimal));
                    editor.putString("cash", Double.toString(cashDecimal));
                    editor.commit();
                }
            }
            if (stocksBuyingStart){
                stocksBuyingStart = false;
            }

        }

        if ((sharedPreferences.getString("favorites", null) != null && !sharedPreferences.getString("favorites", null).trim().isEmpty())) {
            String favoritesStringArray = sharedPreferences.getString("favorites", null);
            String[] favoritesTickerArray = favoritesStringArray.split(",");


            for (int i = 0; i < favoritesTickerArray.length; i++) {
                String[] storedTickerItem = favoritesTickerArray[i].split(":");
                int tickerIndex = justTickersList.indexOf(storedTickerItem[0].toUpperCase());
//                System.out.println("Hellooooooooooo the ticker index is" + storedTickerItem[0]);

                String[] receivedTickerItem = responseArrayList.get(tickerIndex).split(",");

                if (Double.parseDouble(storedTickerItem[1]) > 0) {
                    FavoriteRecyclerItem favoriteRecyclerItem = new FavoriteRecyclerItem(storedTickerItem[1] + " shares", receivedTickerItem[0], receivedTickerItem[1], receivedTickerItem[2]);
                    sectionTwoItems.remove(favoriteRecyclerItem);
                    sectionTwoItems.add(favoriteRecyclerItem);
                } else {
                    FavoriteRecyclerItem favoriteRecyclerItem = new FavoriteRecyclerItem(storedTickerItem[2], receivedTickerItem[0], receivedTickerItem[1], receivedTickerItem[2]);
                    sectionTwoItems.remove(favoriteRecyclerItem);
                    sectionTwoItems.add(favoriteRecyclerItem);
                }
            }
        }

        netWorthDecimal = cashDecimal + stockValueDecimal;

        editor.putString("netWorth", Double.toString(netWorthDecimal));

        mainRecyclerViewSectionList = new ArrayList<>();
        mainRecyclerAdapter = new MainRecyclerAdapter(mainRecyclerViewSectionList, MainActivity.this);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mainRecyclerView.setAdapter(mainRecyclerAdapter);

        if (mainRecyclerViewSectionList.size() == 0) {
            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionOneName, "Net Worth", netWorth, sectionOneItems));
            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionTwoName, "Net Worth", netWorth, sectionTwoItems));
        } else {
            mainRecyclerViewSectionList.set(0, new MainRecyclerViewSection(sectionOneName, "Net Worth", netWorth, sectionOneItems));
            mainRecyclerViewSectionList.set(1, new MainRecyclerViewSection(sectionTwoName, "Net Worth", netWorth, sectionTwoItems));
        }
        mainRecyclerAdapter.notifyDataSetChanged();
        if (spinnerStart) {
            turnSpinnerOff();
            spinnerStart = !spinnerStart;
        }
    }

//    public void getRecyclerViewData(String ticker) {
//        String url = "http://10.0.2.2:3000/price-data/" + ticker;
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    // Loop through the array elements
//                    JSONObject priceDataItem = response.getJSONObject(0);
//
//                    String prevClose = priceDataItem.getString("prevClose");
//                    String last = priceDataItem.getString("last");
//                    String ticker = priceDataItem.getString("ticker");
//                    String change = Double.toString(Double.parseDouble(last) - Double.parseDouble(prevClose));
//
////                    Calling API to get name
//                    String url = "http://10.0.2.2:3000/meta-data/" + ticker;
//                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                String name = response.getString("name");
////                              Setting RecyclerView values
//                                setRecyclerViewData(ticker, name, last, change);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                        }
//                    });
//
//                    requestQueue.add(request);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//
//        requestQueue.add(request);
//    }

    public void setRecyclerViewData(String ticker, String current, String change) {
        localIndex++;
//        portfolio = ticker:numShares,ticker:numShares
//        favorites = ticker:numShares:companyName,ticker:numShares:companyName

        String portfolioStringArray = sharedPreferences.getString("portfolio", null);
        String favoritesStringArray = sharedPreferences.getString("favorites", null);

        String[] portfolioTickersArray = portfolioStringArray.split(",");
        String[] favoritesTickerArray = favoritesStringArray.split(",");

        String netWorth = sharedPreferences.getString("netWorth", null);

//        Updating section one items list (portfolio)
        for (int i = 0; i < portfolioTickersArray.length; i++) {
            String[] tickerDetails = portfolioTickersArray[i].split(":");
            if (ticker.equals(tickerDetails[0])) {
                FavoriteRecyclerItem favoriteRecyclerItem = new FavoriteRecyclerItem(tickerDetails[1] + " shares", ticker, current, change);
                sectionOneItems.remove(favoriteRecyclerItem);
                sectionOneItems.add(favoriteRecyclerItem);
            }
        }
        for (int i = 0; i < favoritesTickerArray.length; i++) {
            String[] tickerDetails = favoritesTickerArray[i].split(":");
            if (ticker.equals(tickerDetails[0])) {
                if (Double.parseDouble(tickerDetails[1]) > 0) {
                    FavoriteRecyclerItem favoriteRecyclerItem = new FavoriteRecyclerItem(tickerDetails[1] + " shares", ticker, current, change);
                    sectionTwoItems.remove(favoriteRecyclerItem);
                    sectionTwoItems.add(favoriteRecyclerItem);
                } else {
                    FavoriteRecyclerItem favoriteRecyclerItem = new FavoriteRecyclerItem(tickerDetails[2], ticker, current, change);
                    sectionTwoItems.remove(favoriteRecyclerItem);
                    sectionTwoItems.add(favoriteRecyclerItem);
                }
            }
        }

        if (localIndex == lengthOfRecyclerView) {

            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionOneName, "Net Worth", netWorth, sectionOneItems));
            mainRecyclerViewSectionList.add(new MainRecyclerViewSection(sectionTwoName, "Net Worth", netWorth, sectionTwoItems));
            mainRecyclerAdapter.notifyDataSetChanged();
        }

    }

    public void onFooterClick(View view) {
        String url = "https://www.tiingo.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
     protected void onResume() {
        super.onResume();
        sectionTwoItems.clear();
        sectionOneItems.clear();
        mainRecyclerViewSectionList.clear();
//        System.out.println("Updating the portfolio and favorites now");
        setDataFromSharedPreferences();
//
        timer = new Timer();
        int begin = 0;
        int timeInterval = 15000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Updating favorites and portfolio price data now!!!");
                        setDataFromSharedPreferences();
                    }
                });

            }
        }, begin, timeInterval);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void turnSpinnerOn() {
        spinnerLayout = findViewById(R.id.mainSpinnerLayout);
        originalScrollView = findViewById(R.id.originalScrollView);

        originalScrollView.setVisibility(View.INVISIBLE);

        spinnerLayout.setVisibility(View.VISIBLE);
    }

    public void turnSpinnerOff() {

        originalScrollView.setVisibility(View.VISIBLE);

        spinnerLayout.setVisibility(View.GONE);
    }

}

