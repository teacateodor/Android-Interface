package com.theo.clientinterface;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
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


public class MainActivity extends AppCompatActivity {


    Button button;
    RequestQueue requestQueue;
    LinearLayout linearLayout;
    Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        requestQueue = Volley.newRequestQueue(this);
        linearLayout = findViewById(R.id.linearLayout);
        getJson();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                getJson();
            }
        });

    }

    public void getJson() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                "http://warehouse.azurewebsites.net/api/TeodorProducts",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            JSONArray jsonArray = response;

                            for (int i = 0; i < jsonArray.length(); i++) {

                                final JSONObject Product = jsonArray.getJSONObject(i);

                                String Id = Product.getString("Id");
                                String Label = Product.getString("Label");

                                LinearLayout view = new LinearLayout(c);
                                WebView w = new WebView(c);
                                TextView t = new TextView(c);

                                //padding
                                w.setInitialScale(1);
                                w.getSettings().setUseWideViewPort(true);
                                w.getSettings().setLoadWithOverviewMode(true);
                                w.loadUrl(Product.getString("ImageUri"));

                                t.append(Product.getString("Label"));
                                //


                                view.addView(t);
                                view.addView(w);
                                linearLayout.addView(view);
//                                linearLayout.addView(t);
//                                linearLayout.addView(w);

                                final String id = Product.getString("Id");

                                //Delete
                                w.setOnLongClickListener(new View.OnLongClickListener() {

                                    @Override
                                    public boolean onLongClick(final View view) {

                                        linearLayout.removeView(view);
                                        linearLayout.refreshDrawableState();
                                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.DELETE,
                                                "http://warehouse.azurewebsites.net/api/TeodorProducts" + id,
                                                "",
                                                new Response.Listener<JSONArray>() {
                                                    @Override
                                                    public void onResponse(JSONArray response) {
                                                        linearLayout.removeAllViews();
                                                        getJson();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                });

                                        return true;
                                    }


                                });


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error.getStackTrace().toString(), "volley");
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
