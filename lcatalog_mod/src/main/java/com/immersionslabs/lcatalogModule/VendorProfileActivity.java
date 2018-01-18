package com.immersionslabs.lcatalogModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.immersionslabs.lcatalogModule.Utils.DownloadImages_Vendor;
import com.immersionslabs.lcatalogModule.Utils.EnvConstants;
import com.immersionslabs.lcatalogModule.Utils.NetworkConnectivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class VendorProfileActivity extends AppCompatActivity {

    private static final String TAG = "VendorProfileActivity";
    private static final String REGISTER_URL = EnvConstants.APP_BASE_URL + "/vendors/specific/";
    private static String VENDOR_URL = null;

    private static final int REQUEST_UPDATE = 0;

    String vendor_id, vendor_name, vendor_address, vendor_image, vendor_no_of_articles;
    TextView profile_vendor_name, profile_vendor_location;
    ImageView profile_vendor_logo;
    Button profile_vendor_articles_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        Toolbar toolbar = findViewById(R.id.vendor_profile_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        profile_vendor_name = findViewById(R.id.profile_vendor_name);
        profile_vendor_location = findViewById(R.id.profile_vendor_address);
        profile_vendor_logo = findViewById(R.id.profile_vendor_image);
        profile_vendor_articles_list = findViewById(R.id.profile_vendor_articles_list);

        profile_vendor_articles_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle vendor_data = new Bundle();
                vendor_data.putString("vendor_id", vendor_id);

//                Intent intent = new Intent(VendorProfileActivity.this, VendorCatalogActivity.class).putExtras(vendor_data);
//                startActivity(intent);
            }
        });

        final Bundle vendor_data = getIntent().getExtras();
        vendor_id = vendor_data.getString("vendor_id");

        VENDOR_URL = REGISTER_URL + vendor_id;
        Log.e(TAG, "VENDOR_URL--" + VENDOR_URL);

        try {
            getVendorData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (NetworkConnectivity.checkInternetConnection(VendorProfileActivity.this)) {

        } else {
            InternetMessage();
        }
    }

    private void getVendorData() throws JSONException {

        final JSONObject baseclass = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, VENDOR_URL, baseclass, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "response--" + response);

                try {
                    String response_type = response.getString("success");
                    Log.e(TAG, "Vendor Response Type--" + response_type);
                    String response_message = response.getString("message");
                    Log.e(TAG, "Vendor Response Message--" + response_message);

                    JSONArray array = response.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        vendor_id = object.getString("id");
                        vendor_name = object.getString("name");
                        vendor_address = object.getString("location");
                        vendor_image = object.getString("logo");
                        vendor_no_of_articles = object.getString("total_objects");
                    }

                    Log.e(TAG, "Article Vendor ID--" + vendor_id);
                    Log.e(TAG, "Article Vendor Name--" + vendor_name);
                    Log.e(TAG, "Article Vendor Address--" + vendor_address);
                    Log.e(TAG, "Article Vendor Image--" + vendor_image);

                    profile_vendor_name.setText(vendor_name);
                    profile_vendor_location.setText(vendor_address);
                    profile_vendor_articles_list.setText("VENDOR ARTICLES (" + vendor_no_of_articles + ")");

                    new DownloadImages_Vendor(profile_vendor_logo).execute(vendor_image);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VendorProfileActivity.this, "Internal Error", Toast.LENGTH_SHORT).show();
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject request = new JSONObject(res);
                        Log.e(TAG, "request--" + request);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(VendorProfileActivity.this);
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UPDATE) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    private void InternetMessage() {
        final View view = this.getWindow().getDecorView().findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, "Check Your Internet connection", Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.red));
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if (NetworkConnectivity.checkInternetConnection(VendorProfileActivity.this)) {
                } else {
                    InternetMessage();
                }
            }
        });
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}