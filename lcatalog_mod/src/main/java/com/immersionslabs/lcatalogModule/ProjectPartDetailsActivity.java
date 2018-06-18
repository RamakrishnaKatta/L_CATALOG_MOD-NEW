package com.immersionslabs.lcatalogModule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.immersionslabs.lcatalogModule.Adapters.ProjectPartImageSliderAdapter;
import com.immersionslabs.lcatalogModule.Adapters.ProjectpartDetailsAdapter;
import com.immersionslabs.lcatalogModule.Utils.DownloadManager_3DS;
import com.immersionslabs.lcatalogModule.Utils.EnvConstants;
import com.immersionslabs.lcatalogModule.Utils.UnzipUtil;
import com.immersionslabs.lcatalogModule.network.ApiCommunication;
import com.immersionslabs.lcatalogModule.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ProjectPartDetailsActivity extends AppCompatActivity implements ApiCommunication {

    private static final String REGISTER_URL = EnvConstants.APP_BASE_URL + "/getProjectDetails/";
    private static String PROJECT_PART_ARTICLE_URL = null;
    private static final String ARTICLE_SPECIFIC_URL = EnvConstants.APP_BASE_URL + "/vendorArticles/";

    private static String UNIQUE_ARTICLE_URL;

    private static final String TAG = "ProjectPartDetailsActivity";

    TextView part_name, part_Desc;
    AppCompatImageView part_image;

    ImageButton part_augment, part_3dview;


    String image1, image2, image3, image4, image5;
    String project_id;
    RecyclerView recyclerView;
    ProjectpartDetailsAdapter adapter;
    GridLayoutManager layoutManager;
    Context mcontext;

    String p_name, part_3ds;
    private ArrayList<String> part_articles_id;
    private ArrayList<String> part_article_images;
    private ArrayList<String> part_article_name;
    private ArrayList<String> project_ids;

    ArrayList<String> slider_images = new ArrayList<>();
    private LinearLayout slider_dots;
    String project_part_images;
    private ViewPager viewPager;
    ProjectPartImageSliderAdapter imageSliderAdapter;
    TextView[] dots;
    int page_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_part_details);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = findViewById(R.id.toolbar_project_part);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.part_list_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        part_name = findViewById(R.id.part_title_text);
        part_Desc = findViewById(R.id.part_description_text);
        part_image = findViewById(R.id.part_image_view);
        part_3dview = findViewById(R.id.part_3dview_icon);
        part_augment = findViewById(R.id.part_augment_icon);


        part_articles_id = new ArrayList<>();
        part_article_name = new ArrayList<>();
        part_article_images = new ArrayList<>();
        project_ids = new ArrayList<>();
        mcontext = getApplicationContext();

        final Bundle b = getIntent().getExtras();

        project_id = (String) b.getCharSequence("_id");
        Log.e(TAG, "project_id ---- " + project_id);

        part_3ds = (String) b.getCharSequence("partview_3d");
        Log.e(TAG, "part_3ds-----" + part_3ds);
        p_name = (String) b.getCharSequence("partName");
        Log.e(TAG, "part_name" + p_name);

        part_name.setText(b.getCharSequence("partName"));
        Log.e(TAG, "onCreate:part_name " + part_name);
        part_Desc.setText(b.getCharSequence("partDesc"));
        Log.e(TAG, " part_Desc" + part_Desc);

        project_part_images = (String) b.getCharSequence("partimages");
        Log.e(TAG, " part images" + project_part_images);

        try {
            JSONArray image_json = new JSONArray(project_part_images);
            for (int i = 0; i < image_json.length(); i++) {
                image1 = image_json.getString(0);
                image2 = image_json.getString(1);
                image3 = image_json.getString(2);
                image4 = image_json.getString(3);
                image5 = image_json.getString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "ProjectpartImage 1----" + image1);
        Log.e(TAG, "ProjectpartImage 2----" + image2);
        Log.e(TAG, "ProjectpartImage 3----" + image3);
        Log.e(TAG, "ProjectpartImage 4----" + image4);
        Log.e(TAG, "ProjectpartImage 5----" + image5);

        final String[] Images = {image1, image2, image3, image4, image5};
        Collections.addAll(slider_images, Images);

        viewPager = findViewById(R.id.part_view_pager);
        imageSliderAdapter = new ProjectPartImageSliderAdapter(ProjectPartDetailsActivity.this, slider_images, project_id);
        viewPager.setAdapter(imageSliderAdapter);

        slider_dots = findViewById(R.id.project_part_slide_dots);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        part_augment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectPartDetailsActivity.this, ARNativeActivity.class);
                startActivity(intent);
            }
        });

        part_3dview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b5 = new Bundle();
                b5.putString("part3dsName", part_3ds);
                b5.putString("name_project", project_id);
                Intent _3d_intent = new Intent(ProjectPartDetailsActivity.this, Article3dViewActivity.class).putExtras(b5);
                startActivity(_3d_intent);
            }
        });

        PROJECT_PART_ARTICLE_URL = REGISTER_URL + project_id;
        Log.e(TAG, "PROJECT_PART_URL------" + PROJECT_PART_ARTICLE_URL);

        try {
            getpartData();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[slider_images.size()];

        slider_dots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.WHITE);
            slider_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#004D40"));
    }

    final Runnable update = new Runnable() {
        @Override
        public void run() {
            if (page_position == slider_images.size()) {
                page_position = 0;
            } else {
                page_position = page_position + 1;
            }
            viewPager.setCurrentItem(page_position, true);
        }
    };

    private void getpartData() throws JSONException {
        ApiService.getInstance(this).getData(this, false, "PROJECT_PART_DATA", PROJECT_PART_ARTICLE_URL, "PART_ARTICLE");
    }

    public void getarticledata() {
        Iterator iterator = EnvConstants.part_articles_id.iterator();
        while (iterator.hasNext()) {
            UNIQUE_ARTICLE_URL = ARTICLE_SPECIFIC_URL + iterator.next();
            ApiService.getInstance(this).getData(this, false, "ARTICLE_PROJECT_DATA", UNIQUE_ARTICLE_URL, "ARTICLE_DATA");
        }

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        if (flag.equals("PART_ARTICLE")) {

            try {
                JSONObject resp = response.getJSONObject("data");
                project_ids.add(resp.getString("_id"));
                Log.e(TAG, "responseproject " + response);

                JSONArray parts = resp.getJSONArray("parts");
                Log.e(TAG, "partsjson: " + parts);
                getData(parts);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (flag.equals("ARTICLE_DATA")) {
            try {
                JSONObject resp = response.getJSONObject("data");
                Log.e(TAG, "project_articles " + response);
                String id = resp.getString("_id");
                EnvConstants.part_article_name.put(id, resp.getString("name"));
                EnvConstants.part_articles_vendor_id.put(id, resp.getString("vendor_id"));
                EnvConstants.part_articles_description.put(id, resp.getString("description"));
                EnvConstants.part_articles_price.put(id, resp.getString("price"));
                EnvConstants.part_article__discounts.put(id, resp.getString("discount"));
                EnvConstants.part_article_dimensions.put(id, resp.getString("dimensions"));
                EnvConstants.part_articles_3ds.put(id, resp.getString("view_3d"));
                EnvConstants.part_articles_pattern.put(id, resp.getString("pattern"));
                EnvConstants.part_article_images.put(id, resp.getString("img"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getData(JSONArray parts) {
        for (int i = 0; i < parts.length(); i++) {
            JSONObject object;
            try {
                object = parts.getJSONObject(i);
                JSONArray array = object.getJSONArray("articlesData");
                Log.e(TAG, "getData: articlesdata" + array);
                for (int j = 0; j < object.length(); j++) {

                    JSONObject object1 = array.getJSONObject(j);
                    part_articles_id.add(object1.getString("_id"));
                    part_article_name.add(object1.getString("name"));
                    part_article_images.add(object1.getString("img"));
                    EnvConstants.part_articles_id = part_articles_id;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, " Article Id" + part_articles_id);
        Log.e(TAG, " Article Name" + part_article_name);
        Log.e(TAG, " Article Images" + part_article_images);

        getarticledata();
        layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProjectpartDetailsAdapter(this, part_articles_id, part_article_name, part_article_images, mcontext);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        Toast.makeText(this, "Internal Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();

        Intent intent = new Intent(this, ProjectDetailActivity.class);
        intent.putExtra("activity", "SplashScreen");
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
