package com.rizwan.secondtry;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private String ticker;
    private boolean isInPortfolio;
    private boolean isInFavorites;
    private double numShares;
    private String companyName;
    private double current;

    private double netWorth;
    private double cash;
    private double stockValue;

    private double stocksToBuySell = -1;  //Just so that it can be checked whether the user has entered a value or not
    private double stocksBuyPrice = 0.0;

    private int dataLoadingIndex = 0;
    private boolean isDataLoaded = false;

    TextView detailNameText, detailTickerText, detailCurrentText, detailChangeText, detailPortfolioShareOwnedText, detailPortfolioMarketValueText, statsCurrentPrice, statsLow, statsMid, statsBidPrice, statsOpenPrice, statsHigh, statsVolume, detailAboutText;
    Button detailPortfolioTradeButton;
    Toolbar fakeToolbar;
    ProgressBar spinner;
    TextView spinnerText;
    ConstraintLayout spinnerLayout, originalLayout;

    List<NewsSection> newsSectionList = new ArrayList<>();
    RecyclerView newsMainRecyclerView;
    WebView webView;

    private static final String mySharedPreference = "mySharedPreference";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private RequestQueue requestQueue;

    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static DecimalFormat dfComma = new DecimalFormat("#,###.00");

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        turnSpinnerOn();
//        turnSpinnerOff();


        Intent myIntent = getIntent(); // gets the previously created intent
        ticker = myIntent.getStringExtra("ticker");

        sharedPreferences = getApplicationContext().getSharedPreferences(mySharedPreference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        netWorth = Double.parseDouble(sharedPreferences.getString("netWorth", null));
        cash = Double.parseDouble(sharedPreferences.getString("cash", null));
        stockValue = Double.parseDouble(sharedPreferences.getString("stockValue", null));

        isInFavorites = checkInFavorites();
        isInPortfolio = checkInPortfolio();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.detailsToolbar);
        setSupportActionBar(myToolbar);
        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

//        Initializing with an empty adapter
        newsMainRecyclerView = findViewById(R.id.newsMainRecyclerView);
        newsMainRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));
        NewsMainRecyclerAdapter newsMainRecyclerAdapter = new NewsMainRecyclerAdapter(newsSectionList, DetailsActivity.this);
        newsMainRecyclerView.setAdapter(newsMainRecyclerAdapter);

        requestQueue = Volley.newRequestQueue(DetailsActivity.this);

//        To set TextView, ImageView etc
        setViewVariables();

        getMetaData(ticker);

        getPriceData(ticker);

//        getHistoricData(ticker);
        getNewsData(ticker);

        setHighcharts();

    }


    private void setViewVariables() {
        detailNameText = findViewById(R.id.detailNameText);
        detailTickerText = findViewById(R.id.detailTickerText);
        detailChangeText = findViewById(R.id.detailChangeText);
        detailCurrentText = findViewById(R.id.detailCurrentText);
        detailPortfolioShareOwnedText = findViewById(R.id.detailPortfolioShareOwnedText);
        detailPortfolioMarketValueText = findViewById(R.id.detailPortfolioMarketValueText);
        statsCurrentPrice = findViewById(R.id.statsCurrentPrice);
        statsLow = findViewById(R.id.statsLow);
        statsMid = findViewById(R.id.statsMid);
        statsBidPrice = findViewById(R.id.statsBidPrice);
        statsOpenPrice = findViewById(R.id.statsOpenPrice);
        statsHigh = findViewById(R.id.statsHigh);
        statsVolume = findViewById(R.id.statsVolume);
        detailAboutText = findViewById(R.id.detailAboutText);

        dataLoadingIndex++;
        if(dataLoadingIndex == 6){
            turnSpinnerOff();
        }
    }

    public void getMetaData(String ticker) {

        String url = "http://rizwan-csci571.us-east-1.elasticbeanstalk.com/meta-data/" + ticker;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ticker = response.getString("ticker");
                    String name = response.getString("name");
                    String startDate = response.getString("startDate");
                    String description = response.getString("description");
                    String exchangeCode = response.getString("exchangeCode");
                    companyName = name;

                    DetailsInterfaces.MetaData metaData = new DetailsInterfaces.MetaData(ticker, name, startDate, description, exchangeCode);
                    setMetaData(metaData);
//                    System.out.println(metaData);
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

    private void setMetaData(DetailsInterfaces.MetaData metaData) {
        detailNameText.setText(metaData.getName());
        detailTickerText.setText(metaData.getTicker());
        detailAboutText.setText(metaData.getDescription());

        int lineCount = detailAboutText.getLineCount();
        if (lineCount > 2) {

            detailAboutText.setMaxLines(2);
            detailAboutText.setEllipsize(TextUtils.TruncateAt.END);
            detailAboutText.setText(metaData.getDescription());
            View button = findViewById(R.id.showMoreButton);
            button.setVisibility(View.VISIBLE);

        }

        dataLoadingIndex++;
        if(dataLoadingIndex == 6){
            turnSpinnerOff();
        }

    }

    public void getPriceData(String ticker) {

        String url = "http://rizwan-csci571.us-east-1.elasticbeanstalk.com/price-data/" + ticker;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // Loop through the array elements
                    JSONObject priceDataItem = response.getJSONObject(0);


                    String timestamp = priceDataItem.getString("timestamp");
                    String bidSize = priceDataItem.getString("bidSize");
                    String lastSaleTimestamp = priceDataItem.getString("lastSaleTimestamp");
                    String low = priceDataItem.getString("low");
                    String bidPrice = priceDataItem.getString("bidPrice");
                    String prevClose = priceDataItem.getString("prevClose");
                    String quoteTimestamp = priceDataItem.getString("quoteTimestamp");
                    String last = priceDataItem.getString("last");
                    String askSize = priceDataItem.getString("askSize");
                    String volume = priceDataItem.getString("volume");
                    String lastSize = priceDataItem.getString("lastSize");
                    String ticker = priceDataItem.getString("ticker");
                    String high = priceDataItem.getString("high");
                    String mid = priceDataItem.getString("mid");
                    String askPrice = priceDataItem.getString("askPrice");
                    String open = priceDataItem.getString("open");
                    String tngoLast = priceDataItem.getString("tngoLast");

                    current = Double.parseDouble(last);

                    DetailsInterfaces.PriceData priceData = new DetailsInterfaces.PriceData(timestamp, bidSize, lastSaleTimestamp, low, bidPrice, prevClose, quoteTimestamp, last, askSize, volume, lastSize, ticker, high, mid, askPrice, open, tngoLast);
//                    System.out.println(priceData.getVolume());
                    setPriceData(priceData);
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

    public void setPriceData(DetailsInterfaces.PriceData priceData) {

        changePortfolioTextView();

        String currentPrice;
        if(priceData.getLast().equals("null")){
            currentPrice = "$0.0";
            detailCurrentText.setText(currentPrice);
            currentPrice = "Current Price: 0.0";
            statsCurrentPrice.setText(currentPrice);
        }
        else{
            currentPrice = "$" + (priceData.getLast());
            detailCurrentText.setText(currentPrice);
            currentPrice = "Current Price: " + (priceData.getLast());
            statsCurrentPrice.setText(currentPrice);
        }

        String low;
        if (priceData.getLow().equals("null")) {
            low = "Low: 0.0";
        } else {
            low = "Low: " + (priceData.getLow());
        }
        statsLow.setText(low);

        String mid;
        if (priceData.getMid().equals("null")) {
            mid = "Mid: 0.0";
        } else {
            mid = "Mid: " + (priceData.getMid());
        }
        statsMid.setText(mid);

        String bidPrice;
        if (priceData.getBidPrice().equals("null")) {
            bidPrice = "Bid Price: 0.0";
        } else {
            bidPrice = "Bid Price: " + (priceData.getBidPrice());
        }
        statsBidPrice.setText(bidPrice);

        String openPrice;
        if (priceData.getOpen().equals("null")) {
            openPrice = "Open Price: 0.0";
        } else {
            openPrice = "Open Price: " + (priceData.getOpen());
        }
        statsOpenPrice.setText(openPrice);

        String high;
        if (priceData.getHigh().equals("null")) {
            high = "High: 0.0";
        } else {
            high = "High: " + (priceData.getHigh());
        }
        statsHigh.setText(high);

        String volume;
        if (priceData.getVolume().equals("null")) {
            volume = "Volume: 0.0";
        } else {
            volume = "Volume: " + dfComma.format(Double.parseDouble(priceData.getVolume()));
        }
        statsVolume.setText(volume);

//        If value of any of these variables is null, parse it as 0.0
        double last = Double.parseDouble(priceData.getLast());
        double prevClose = Double.parseDouble(priceData.getPrevClose());

        double change = last - prevClose;


        if (change < 0) {
            String changeText = "-$" + df2.format(change);
            detailChangeText.setText(changeText);
            detailChangeText.setTextColor(DetailsActivity.this.getResources().getColor(R.color.myRed));
        } else {
            String changeText = "$" + df2.format(change);
            detailChangeText.setText(changeText);
            detailChangeText.setTextColor(DetailsActivity.this.getResources().getColor(R.color.myGreen));
        }

        dataLoadingIndex++;
        if(dataLoadingIndex == 6){
            turnSpinnerOff();
        }
    }

//    public void getHistoricData(String ticker) {
//        String url = "http://10.0.2.2:3000/historic-data/" + ticker;
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//
//                    List<DetailsInterfaces.HistoricData> historicDataList = new ArrayList<>();
//
//                    for (int i = 0; i < response.length() - 1; i++) {  //Minus 1 because last item is 'success'
//                        JSONObject historicDataItem = response.getJSONObject(i);
//                        String date = historicDataItem.getString("date");
//                        String close = historicDataItem.getString("close");
//                        String high = historicDataItem.getString("high");
//                        String low = historicDataItem.getString("low");
//                        String open = historicDataItem.getString("open");
//                        String volume = historicDataItem.getString("volume");
//                        String adjClose = historicDataItem.getString("adjClose");
//                        String adjHigh = historicDataItem.getString("adjHigh");
//                        String adjLow = historicDataItem.getString("adjLow");
//                        String adjOpen = historicDataItem.getString("adjOpen");
//                        String adjVolume = historicDataItem.getString("adjVolume");
//                        String divCash = historicDataItem.getString("divCash");
//                        String splitFactor = historicDataItem.getString("splitFactor");
//
//                        DetailsInterfaces.HistoricData historicData = new DetailsInterfaces.HistoricData(date, close, high, low, open, volume, adjClose, adjHigh, adjLow, adjOpen, adjVolume, divCash, splitFactor);
//                        historicDataList.add(historicData);
//                    }
//                    setHistoricData(historicDataList);
//
////                    System.out.println(historicDataList.get(0).getAdjVolume());
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

    public void setHighcharts() {
        webView = (WebView) findViewById(R.id.highchartsWebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl("file:///android_asset/highchartspractice.html");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals("file:///android_asset/highchartspractice.html")) {
                    webView.loadUrl("javascript:loadHighcharts('" + ticker + "')");
                }
            }
        });
    }

    public void getNewsData(String ticker) {
        String url = "http://rizwan-csci571.us-east-1.elasticbeanstalk.com/news-data/" + ticker;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    List<DetailsInterfaces.NewsData> newsDataList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject newsDataItem = response.getJSONObject(i);
                        String title = newsDataItem.getString("title");
                        String url = newsDataItem.getString("url");
                        String urlToImage = newsDataItem.getString("urlToImage");
                        String publishedAt = newsDataItem.getString("publishedAt");
                        String description = newsDataItem.getString("description");
                        String source = newsDataItem.getString("source");

                        DetailsInterfaces.NewsData newsData = new DetailsInterfaces.NewsData(title, url, urlToImage, publishedAt, description, source);
                        newsDataList.add(newsData);
                    }
                    setNewsData(newsDataList);

//                    System.out.println(newsDataList.get(0).getTitle());
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

    public void setNewsData(List<DetailsInterfaces.NewsData> newsDataList) {

        DetailsInterfaces.NewsData tempNewsSectionName = newsDataList.get(0);
        List<DetailsInterfaces.NewsData> tempNewsSectionList = new ArrayList<>();
        for (int i = 1; i < newsDataList.size(); i++) {
            tempNewsSectionList.add(newsDataList.get(i));
        }

        newsSectionList.add(new NewsSection(tempNewsSectionName, tempNewsSectionList));
        setNewsMainRecyclerView(newsSectionList);

        dataLoadingIndex++;
        if(dataLoadingIndex == 6){
            turnSpinnerOff();
        }
    }

    public void setNewsMainRecyclerView(List<NewsSection> newsSectionList) {
        NewsMainRecyclerAdapter newsMainRecyclerAdapter = new NewsMainRecyclerAdapter(newsSectionList, DetailsActivity.this);
        newsMainRecyclerView.setAdapter(newsMainRecyclerAdapter);
        newsMainRecyclerAdapter.notifyDataSetChanged();

        dataLoadingIndex++;
        if(dataLoadingIndex == 6){
            turnSpinnerOff();
        }

//        Add item decoration if required

    }

//    Other helper methods for event listeners

    public void onClickShowMore(View view) {
        detailAboutText.setMaxLines(1000);
        detailAboutText.setEllipsize(null);
        View moreButton = findViewById(R.id.showMoreButton);
        moreButton.setVisibility(View.GONE);
        View lessButton = findViewById(R.id.showLessButton);
        lessButton.setVisibility(View.VISIBLE);
    }

    public void onClickShowLess(View view) {
        detailAboutText.setMaxLines(2);
        detailAboutText.setEllipsize(TextUtils.TruncateAt.END);
        View moreButton = findViewById(R.id.showMoreButton);
        moreButton.setVisibility(View.VISIBLE);
        View lessButton = findViewById(R.id.showLessButton);
        lessButton.setVisibility(View.GONE);
    }

    public void onClickNewsItem(View view) {
        TextView tv = view.findViewById(R.id.newsItemTitle);
        String newsTitle;
        String url = "";
        if (tv == null) {
            DetailsInterfaces.NewsData sectionHeading = newsSectionList.get(0).getSectionName();
            url = sectionHeading.getUrl();
        } else {
            newsTitle = tv.getText().toString();
            List<DetailsInterfaces.NewsData> newsDataList = newsSectionList.get(0).getSectionItem();
            for (int i = 0; i < newsDataList.size(); i++) {
                if (newsTitle.equals(newsDataList.get(i).getTitle())) {
                    url = newsDataList.get(i).getUrl();
                }
            }
        }

//        System.out.println(url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.details_actionbar, menu);
        setStarSource(menu.findItem(R.id.starIcon));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.starIcon:
                onStarClicked(menuItem);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

//        portfolio = ticker:numShares,ticker:numShares
//        favorites = ticker:numShares:companyName,ticker:numShares:companyName

    public void setStarSource(MenuItem menuItem){
        dataLoadingIndex++;
        if(dataLoadingIndex == 6){
            turnSpinnerOff();
        }

        if(isInFavorites){
            menuItem.setIcon(R.drawable.ic_baseline_star_24);
            return;
        }
        menuItem.setIcon(R.drawable.ic_baseline_star_border_24);
    }

    public void onStarClicked(MenuItem menuItem){
        if(isInFavorites){
            menuItem.setIcon(R.drawable.ic_baseline_star_border_24);
            removeFromFavorites();
            Toast.makeText(DetailsActivity.this, '"' + ticker.toUpperCase() + '"' + " was removed from favorites", Toast.LENGTH_SHORT).show();
            return;
        }
        menuItem.setIcon(R.drawable.ic_baseline_star_24);
        addToFavorites();
        Toast.makeText(DetailsActivity.this, '"' + ticker.toUpperCase() + '"' + " was added to favorites", Toast.LENGTH_SHORT).show();
    }

    public boolean checkInFavorites(){
        String favoritesStringArray = sharedPreferences.getString("favorites", null);
        String [] favoritesTickerArray = favoritesStringArray.split(",");
        for (int i = 0; i < favoritesTickerArray.length; i++) {
            String[] tickerDetails = favoritesTickerArray[i].split(":");
            if(ticker.equals(tickerDetails[0])){
                return true;
            }
        }
        return false;
    }

    public boolean checkInPortfolio(){
        String portfolioStringArray = sharedPreferences.getString("portfolio", null);
        String [] portfolioTickerArray = portfolioStringArray.split(",");
        for (int i = 0; i < portfolioTickerArray.length; i++) {
            String[] tickerDetails = portfolioTickerArray[i].split(":");
            if(ticker.equals(tickerDetails[0])){
                numShares = Double.parseDouble(tickerDetails[1]);
                return true;
            }
        }
        numShares = 0;
        return false;
    }

    public void removeFromFavorites(){
        String favoritesStringArray = sharedPreferences.getString("favorites", null);
        String [] favoritesTickerArray = favoritesStringArray.split(",");
        StringBuilder favoritesTickerItem = new StringBuilder();
        for (int i = 0; i < favoritesTickerArray.length; i++) {
            String[] tickerDetails = favoritesTickerArray[i].split(":");
            if(ticker.equals(tickerDetails[0])){
                continue;
            }
            if(favoritesTickerItem.toString().equals("")){
                favoritesTickerItem.append(tickerDetails[0]).append(":").append(tickerDetails[1]).append(":").append(tickerDetails[2]);
            }
            else {
                favoritesTickerItem.append(",").append(tickerDetails[0]).append(":").append(tickerDetails[1]).append(":").append(tickerDetails[2]);
            }
        }
//        System.out.println("Favorite after remove " + favoritesTickerItem);
        editor.putString("favorites", String.valueOf(favoritesTickerItem));
        editor.commit();
        isInFavorites = !isInFavorites;
    }

    public void removeFromPortfolio(){
        String portfolioStringArray = sharedPreferences.getString("portfolio", null);
        String [] portfolioTickerArray = portfolioStringArray.split(",");
        StringBuilder portfolioTickerItem = new StringBuilder();
        for (int i = 0; i < portfolioTickerArray.length; i++) {
            String[] tickerDetails = portfolioTickerArray[i].split(":");
            if(ticker.equals(tickerDetails[0])){
                continue;
            }
            if(portfolioTickerItem.toString().equals("")){
                portfolioTickerItem.append(tickerDetails[0]).append(":").append(tickerDetails[1]);
            }
            else {
                portfolioTickerItem.append(",").append(tickerDetails[0]).append(":").append(tickerDetails[1]);
            }
        }
//        System.out.println(portfolioTickerItem);
        editor.putString("portfolio", String.valueOf(portfolioTickerItem));
        editor.commit();
        isInPortfolio = !isInPortfolio;
    }

    public void addToFavorites(){
        String favoritesStringArray = sharedPreferences.getString("favorites", null);
        String favoriteItem = ticker + ":" + numShares + ":" + companyName;
        if(favoritesStringArray.length() > 0){
            favoriteItem = "," + ticker + ":" + numShares + ":" + companyName;
        }

        favoritesStringArray = favoritesStringArray + favoriteItem;

//        System.out.println("Favorites after add " + favoritesStringArray);
        editor.putString("favorites", favoritesStringArray);
        editor.commit();
        isInFavorites = !isInFavorites;
    }

    public void addToPortfolio(){
        String portfolioItem = ticker + ":" + numShares;
        String portfolioStringArray = sharedPreferences.getString("portfolio", null);

        if(portfolioStringArray.length() > 0){
            portfolioItem = "," + ticker + ":" + numShares + ":" + companyName;
        }

        portfolioStringArray = portfolioStringArray + portfolioItem;

//        System.out.println("Favorites after add " + portfolioStringArray);
        editor.putString("portfolio", portfolioStringArray);
        editor.commit();
        changeNumShares();
        isInPortfolio = !isInPortfolio;
    }

    public void changeNumShares(){
        String portfolioStringArray = sharedPreferences.getString("portfolio", null);
        String favoritesStringArray = sharedPreferences.getString("favorites", null);

        String [] portfolioTickerArray = portfolioStringArray.split(",");
        String [] favoritesTickerArray = favoritesStringArray.split(",");

        StringBuilder portfolioTickerItem = new StringBuilder();
        StringBuilder favoritesTickerItem = new StringBuilder();

        for (int i = 0; i < portfolioTickerArray.length; i++) {
            String[] tickerDetails = portfolioTickerArray[i].split(":");
            if(ticker.equals(tickerDetails[0])){
                tickerDetails[1] = Double.toString(numShares);
            }
            if(portfolioTickerItem.toString().equals("")){
                portfolioTickerItem.append(tickerDetails[0]).append(":").append(tickerDetails[1]);
            }
            else {
                portfolioTickerItem.append(",").append(tickerDetails[0]).append(":").append(tickerDetails[1]);
            }
        }

        for (int i = 0; i < favoritesTickerArray.length; i++) {
            String[] tickerDetails = favoritesTickerArray[i].split(":");
            if(ticker.equals(tickerDetails[0])){
                tickerDetails[1] = Double.toString(numShares);
            }
            if(favoritesTickerItem.toString().equals("")){
                favoritesTickerItem.append(tickerDetails[0]).append(":").append(tickerDetails[1]).append(":").append(tickerDetails[2]);
            }
            else {
                favoritesTickerItem.append(",").append(tickerDetails[0]).append(":").append(tickerDetails[1]).append(":").append(tickerDetails[2]);
            }
        }
        editor.putString("portfolio", String.valueOf(portfolioTickerItem));
        editor.putString("favorites", String.valueOf(favoritesTickerItem));
        editor.commit();
    }

    public boolean buyStocks(double numStocks){
        double price = numStocks * current;
        if(price > cash){
            return false;
        }
        else{

            stockValue = stockValue + price;
            cash = cash - price;
            netWorth = cash + stockValue;

            numShares = numShares + numStocks;
            updateNetworth();
            updatePortfolio();
            return true;
        }
    }

    public boolean sellStocks(double numStocks){
        double price = numStocks * current;
        if(numStocks > numShares){
            return false;
        }
        else{
            stockValue = stockValue - price;
            cash = cash + price;
            netWorth = cash + stockValue;
            numShares = numShares - numStocks;
            updateNetworth();
            updatePortfolio();
            return true;
        }

    }

    public void updateNetworth(){
        editor.putString("netWorth", Double.toString(netWorth));
        editor.putString("cash", Double.toString(cash));
        editor.putString("stockValue", Double.toString(stockValue));

        editor.commit();
    }

    public void updatePortfolio(){

        if(!isInPortfolio){
            addToPortfolio();
            changePortfolioTextView();
        }
        else if(numShares == 0) {
            removeFromPortfolio();
            changePortfolioTextView();
        }
        else{
            changeNumShares();
            changePortfolioTextView();
        }
    }

    public void changePortfolioTextView(){
        if(isInPortfolio){
            double marketValue = numShares*current;

            String shareOwnedText = "Shares owned: " + df2.format(numShares);
            String marketValueText = "Market value: $" + df2.format(marketValue);
            detailPortfolioShareOwnedText.setText(shareOwnedText);
            detailPortfolioMarketValueText.setText(marketValueText);
        }
        else{
            String shareOwnedText = "You have 0 shares of" + ticker + ".";
            String marketValueText = "Start trading!";
            detailPortfolioShareOwnedText.setText(shareOwnedText);
            detailPortfolioMarketValueText.setText(marketValueText);
        }
    }

    public void onTradeButtonClicked(View view){
        final Dialog dialog = new Dialog(DetailsActivity.this);
        dialog.setContentView(R.layout.trade_dialog);

        // if button is clicked, close the custom dialog

        dialog.show();

        TextView tradeDialogTitle = dialog.findViewById(R.id.tradeDialogTitle);
        EditText tradeDialogInput = dialog.findViewById(R.id.tradeDialogInput);
        TextView tradeDialogResult = dialog.findViewById(R.id.tradeDialogResult);
        TextView tradeDialogRemaining = dialog.findViewById(R.id.tradeDialogRemaining);
        Button tradeDialogBuyButton = (Button) dialog.findViewById(R.id.tradeDialogBuyButton);
        Button tradeDialogSellButton = dialog.findViewById(R.id.tradeDialogSellButton);

        String title = "Trade " + companyName + " shares";
        tradeDialogTitle.setText(title);

        String result = 0 + " x $" + dfComma.format(current) + "/share = $0.0";
        tradeDialogResult.setText(result);

        String remaining = "$" + df2.format(cash) + " available to buy " + ticker.toUpperCase();
        tradeDialogRemaining.setText(remaining);

        tradeDialogInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0){
                    if(Double.parseDouble(s.toString()) == 0){
                        stocksToBuySell = Double.parseDouble(s.toString());
                        String result = "0 x $" + dfComma.format(current) + "/share = $0.00";
                        tradeDialogResult.setText(result);
                    }
                    else{
                        stocksToBuySell = Double.parseDouble(s.toString());
                        stocksBuyPrice = stocksToBuySell * current;
                        String result = stocksToBuySell + " x $" + dfComma.format(current) + "/share = $" + dfComma.format(stocksBuyPrice);
                        tradeDialogResult.setText(result);
                    }
                }
                else{
                    stocksToBuySell = -1;
                    String result = 0 + " x $" + dfComma.format(current) + "/share = $0.00";
                    tradeDialogResult.setText(result);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tradeDialogBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stocksToBuySell <= 0){
                    String toastText = "Cannot buy less than 0 shares";
                    Toast.makeText(DetailsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                }
                else if(buyStocks(stocksToBuySell)){
                    dialog.dismiss();

//                    Dialog successDialog = dialog;

                    final Dialog successDialog = new Dialog(DetailsActivity.this);
                    successDialog.setContentView(R.layout.bought_successfully_dialog);

                    TextView dialogBoughtShares = successDialog.findViewById(R.id.dialogBoughtShares);
                    String bought = "You have successfully bought " + df2.format(stocksToBuySell);
                    dialogBoughtShares.setText(bought);

                    TextView dialogBoughtSharesCompany = successDialog.findViewById(R.id.boughtSharesCompany);
                    String company = "shares of " + ticker.toUpperCase();
                    dialogBoughtSharesCompany.setText(company);


                    Button doneButton = (Button) successDialog.findViewById(R.id.dialogDoneButton);
                    // if button is clicked, close the custom dialog
                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            successDialog.dismiss();
                        }
                    });

                    successDialog.show();
                    stocksToBuySell = 0;
                }
                else{
                    String toastText = "Not enough money to buy";
                    Toast.makeText(DetailsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tradeDialogSellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stocksToBuySell <= 0){
                    String toastText = "Cannot sell less than 0 shares";
                    Toast.makeText(DetailsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                }
                else if(sellStocks(stocksToBuySell)){
                    dialog.dismiss();
                    final Dialog successDialog = new Dialog(DetailsActivity.this);
                    successDialog.setContentView(R.layout.bought_successfully_dialog);

                    TextView dialogBoughtShares = successDialog.findViewById(R.id.dialogBoughtShares);
                    String bought = "You have successfully sold " + df2.format(stocksToBuySell);
                    dialogBoughtShares.setText(bought);

                    TextView dialogBoughtSharesCompany = successDialog.findViewById(R.id.boughtSharesCompany);
                    String company = "shares of " + ticker.toUpperCase();
                    dialogBoughtSharesCompany.setText(company);

                    Button doneButton = (Button) successDialog.findViewById(R.id.dialogDoneButton);
                    // if button is clicked, close the custom dialog
                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            successDialog.dismiss();
                        }
                    });

                    successDialog.show();
                    stocksToBuySell = 0;
                }
                else{
                    String toastText = "Not enough shares to sell";
                    Toast.makeText(DetailsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        boolean x = buyStocks(2);
//        if(x){
//            Toast.makeText(DetailsActivity.this, "Successfully bought stocks", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(DetailsActivity.this, "Not Enough Money", Toast.LENGTH_SHORT).show();
//        }
    }

    public void turnSpinnerOn(){

        spinnerLayout = findViewById(R.id.spinnerLayout);
        fakeToolbar = findViewById(R.id.fakeToolbar);
        spinner = findViewById(R.id.spinner);

        originalLayout = findViewById(R.id.originalLayout);
        originalLayout.setVisibility(View.INVISIBLE);

        spinnerLayout.setVisibility(View.VISIBLE);

        setSupportActionBar(fakeToolbar);
        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
//        fakeToolbar.setVisibility(View.VISIBLE);
//        spinner.setVisibility(View.VISIBLE);

    }

    public void turnSpinnerOff(){

        originalLayout.setVisibility(View.VISIBLE);
        spinnerLayout.setVisibility(View.GONE);

    }

    public void onFooterClick(View view) {
        String url = "https://www.tiingo.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}