package com.rizwan.secondtry;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

/**
 * Created by MG on 04-03-2018.
 */

public class AutocompleteApiCall {
    private static AutocompleteApiCall mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public AutocompleteApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized AutocompleteApiCall getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AutocompleteApiCall(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String query, Response.Listener<JSONArray>
            listener, Response.ErrorListener errorListener) {
        String url = "http://rizwan-csci571.us-east-1.elasticbeanstalk.com/autocomplete/" + query;
        System.out.println(listener);
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        AutocompleteApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }



}
