package me.standy.findmycar.findmycar;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by astepanov on 15/10/2017.
 */

public class CarRadarAPI {

    public class QueryResult {
        public double latitude;
        public double longitude;
        public double confidence;
        public byte[] photo;

        public QueryResult(double latitude, double longitude, double confidence, String photo_base64) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.confidence = confidence;
            this.photo = Base64.decode(photo_base64, Base64.DEFAULT);
        }
    };

    public interface QueryResultListener {
        void onResult(QueryResult result);
    }

    private String api_endpoint;
    private RequestQueue queue;
    private static CarRadarAPI instance;

    public static CarRadarAPI getInstance() {
        return instance;
    }

    public CarRadarAPI(Context context, String api_endpoint) {
        this.queue = Volley.newRequestQueue(context);
        this.api_endpoint = api_endpoint;
        instance = this;
    }

    void subscribe(String device_id, String license_plate) {
        String uri = this.api_endpoint + "/subscribe";
        JSONObject data = new JSONObject();
        try {
            data.put("token", device_id);
            data.put("number", license_plate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uri, data, null, null);
        queue.add(request);
    }



    void query(String license_plate, final QueryResultListener listener) {
        String uri = this.api_endpoint + "/query?number=" + license_plate;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject result = response.getJSONObject("result");
                                listener.onResult(new QueryResult(
                                        result.getDouble("latitude"),
                                        result.getDouble("longitude"),
                                        result.getDouble("confidence"),
                                        result.getString("photo")
                                ));
                            } else {
                                listener.onResult(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResult(null);
                    }
                }
        );
        queue.add(request);
    }
}
