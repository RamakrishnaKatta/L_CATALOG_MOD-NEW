package com.immersionslabs.lcatalogModule.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.immersionslabs.lcatalogModule.ARNativeActivity;
import com.immersionslabs.lcatalogModule.Adapters.ImageSliderAdapter;
import com.immersionslabs.lcatalogModule.Article3dViewActivity;
import com.immersionslabs.lcatalogModule.R;
import com.immersionslabs.lcatalogModule.Utils.DownloadManager_3DS;
import com.immersionslabs.lcatalogModule.Utils.EnvConstants;
import com.immersionslabs.lcatalogModule.Utils.PrefManager;
import com.immersionslabs.lcatalogModule.Utils.UnzipUtil;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import static com.immersionslabs.lcatalogModule.Utils.EnvConstants.UserId;
import static com.immersionslabs.lcatalogModule.Utils.EnvConstants.user_Favourite_list;

public class Fragment_ProductImages extends Fragment implements OnAnimationEndListener, OnLikeListener {

    private static final String TAG = "Fragment_ProductImages";

    private static String FILE_URL_3DS = EnvConstants.APP_BASE_URL + "/upload/3dviewfiles/";

    private static String LIKE_URL = "http://35.154.150.204:4000/users/favouriteArticles";

    private static String EXTENDED_URL_3DS;

    private PrefManager prefManager;

    LinearLayout note;
    ImageButton article_share, article_download, article_3d_view, article_augment;
    TextView zip_downloaded;

    String article_images, article_id;
    // article_images is split in to five parts and assigned to each string
    String image1, image2, image3, image4, image5;

    String article_name, article_3ds;

    String resp, code, message;

    private ViewPager ArticleViewPager;
    private LinearLayout Slider_dots;
    ImageSliderAdapter imagesliderAdapter;
    ArrayList<String> slider_images = new ArrayList<>();
    TextView[] dots;
    int page_position = 0;

    LikeButton likeButton;

    String Article_3DS_ZipFileLocation, Article_3DS_ExtractLocation, Article_3DS_FileLocation;
    private boolean zip_3ds_downloaded = true;
    File article_3ds_zip_file, article_3ds_file;

    public Fragment_ProductImages() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_product_images, container, false);

        likeButton = view.findViewById(R.id.article_fav_icon);
        likeButton.setOnLikeListener(this);
        likeButton.setOnAnimationEndListener(this);
        article_share = view.findViewById(R.id.article_share_icon);
        article_download = view.findViewById(R.id.article_download_icon);
        article_3d_view = view.findViewById(R.id.article_3dview_icon);
        article_augment = view.findViewById(R.id.article_augment_icon);
        zip_downloaded = view.findViewById(R.id.download_text);

        article_images = getArguments().getString("article_images");
        article_name = getArguments().getString("article_name");
        article_3ds = getArguments().getString("article_3ds");
        article_id = getArguments().getString("article_id");

        Log.d(TAG, "onCreateView:3ds" + article_3ds);
        Log.d(TAG, "onCreateView:name" + article_name);

        if (user_Favourite_list.contains(article_id)) {
            Log.e(TAG, "Favourite Article List: " + user_Favourite_list + " Article id: " + article_id + "  --Article Exists in the ArrayList");
            likeButton.setLiked(true);
        } else {
            Log.e(TAG, "Favourite Article List: " + user_Favourite_list + " Article id: " + article_id + "  --Article Doesn't Exist in the ArrayList");
            likeButton.setLiked(false);
        }

        try {

            JSONArray image_json = new JSONArray(article_images);
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

        Log.e(TAG, "Article Image 1----" + image1);
        Log.e(TAG, "Article Image 2----" + image2);
        Log.e(TAG, "Article Image 3----" + image3);
        Log.e(TAG, "Article Image 4----" + image4);
        Log.e(TAG, "Article Image 5----" + image5);

        final String[] Images = {image1, image2, image3, image4, image5};

        Collections.addAll(slider_images, Images);

        ArticleViewPager = view.findViewById(R.id.article_view_pager);
        imagesliderAdapter = new ImageSliderAdapter(getContext(), slider_images);
        ArticleViewPager.setAdapter(imagesliderAdapter);

        Slider_dots = view.findViewById(R.id.article_slider_dots);

        ArticleViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            private void addBottomDots(int currentPage) {

                dots = new TextView[slider_images.size()];

                Slider_dots.removeAllViews();

                for (int i = 0; i < dots.length; i++) {
                    dots[i] = new TextView(view.getContext());
                    dots[i].setText(Html.fromHtml("&#8226;"));
                    dots[i].setTextSize(35);
                    dots[i].setTextColor(Color.WHITE);
                    Slider_dots.addView(dots[i]);
                }

                if (dots.length > 0)
                    dots[currentPage].setTextColor(Color.parseColor("#004D40"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Article_3DS_ZipFileLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/" + article_3ds;
        Log.e(TAG, "ZipFileLocation--" + Article_3DS_ZipFileLocation);
        Article_3DS_ExtractLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/";
        Log.e(TAG, "ExtractLocation--" + Article_3DS_ExtractLocation);
        Article_3DS_FileLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/article_view.3ds";
        Log.e(TAG, "Object3DFileLocation--" + Article_3DS_FileLocation);

        note = view.findViewById(R.id.download_note);

        article_3ds_zip_file = new File(Article_3DS_ZipFileLocation);
        article_3ds_file = new File(Article_3DS_FileLocation);

        zip_3ds_downloaded = false;

        article_3d_view.setEnabled(false);
        if (article_3ds_file.exists()) {
            article_3d_view.setEnabled(true);
            article_download.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
            zip_3ds_downloaded = true;
            zip_downloaded.setText("File Downloaded");
            zip_downloaded.setTextColor(Color.BLUE);
        }

        article_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Downloading Article, Just for once....");
                progressDialog.setTitle("Article Downloading");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                try {
                                    addModelFolder();
                                    EXTENDED_URL_3DS = FILE_URL_3DS + article_3ds;
                                    Log.e(TAG, "URL ---------- " + EXTENDED_URL_3DS);
                                    new DownloadManager_3DS(EXTENDED_URL_3DS, article_name, article_3ds);

                                    if (article_3ds_zip_file.exists()) {
                                        new UnzipUtil(Article_3DS_ZipFileLocation, Article_3DS_ExtractLocation);
                                    } else {
                                        Toast.makeText(getContext(), "Cannot locate Zipper, Try to download again", Toast.LENGTH_SHORT).show();
                                    }

                                    zip_3ds_downloaded = true;

                                    Log.e(TAG, "Zip Downloaded ---------- " + zip_3ds_downloaded);
                                    progressDialog.dismiss();
                                    article_download.setVisibility(View.GONE);
                                    article_3d_view.setEnabled(true);
                                    note.setVisibility(View.GONE);
                                    zip_downloaded.setText("File Downloaded");
                                    zip_downloaded.setTextColor(Color.BLUE);

                                } catch (IOException e) {
                                    article_download.setVisibility(View.VISIBLE);
                                    article_3d_view.setEnabled(false);
                                    zip_3ds_downloaded = false;
                                    Log.e(TAG, "Zip Not Downloaded ---------- " + zip_3ds_downloaded);
                                    e.printStackTrace();
                                    note.setVisibility(View.VISIBLE);
                                    zip_downloaded.setText("Download !");

                                }
                            }
                        }, 6000);
            }
        });

        article_3d_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zip_3ds_downloaded == true) {

                    Bundle b3 = new Bundle();
                    b3.putString("article_name", article_name);
                    Intent _3d_intent = new Intent(getContext(), Article3dViewActivity.class).putExtras(b3);
                    startActivity(_3d_intent);
                }
            }
        });

        article_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "L_CATALOG_MOD");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "If you want to know more details Click here to visit http://immersionslabs.com/ ");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        article_augment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ARNativeActivity.class);
                startActivity(intent);
            }
        });


        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (page_position == slider_images.size()) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                ArticleViewPager.setCurrentItem(page_position, true);
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 2000, 5000);

        prefManager = new PrefManager(getActivity());
        Log.e(TAG, " " + prefManager.ProductPageActivityScreenLaunch());
        if (prefManager.ProductPageActivityScreenLaunch()) {
            ShowcaseView(view);
        }
        return view;
    }

    private void ShowcaseView(View view) {
        prefManager.setProductPageActivityScreenLaunch();
        Log.e(TAG, " " + prefManager.ProductPageActivityScreenLaunch());
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        final TapTargetSequence sequence = new TapTargetSequence(getActivity()).targets(
                TapTarget.forView(view.findViewById(R.id.article_download_icon), "DOWNLOAD", "Click Here before you click the 3d & Augment ")
                        .targetRadius(30)
                        .textColor(R.color.white)
                        .outerCircleColor(R.color.primary)
                        .id(1),
                TapTarget.forView(view.findViewById(R.id.article_augment_icon), "AUGMENT", "Click Here to Augment the Object")
                        .cancelable(false)
                        .textColor(R.color.white)

                        .targetRadius(30)
                        .outerCircleColor(R.color.primary)
                        .id(2),
                TapTarget.forView(view.findViewById(R.id.article_3dview_icon), "3D", "Click Here see the object in 3d View")
                        .cancelable(false)
                        .targetRadius(30)
                        .textColor(R.color.white)
                        .outerCircleColor(R.color.primary)
                        .id(3)
        ).listener(new TapTargetSequence.Listener() {
            @Override
            public void onSequenceFinish() {
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
            }
        });
        sequence.start();
    }

    /*creation of directory in external storage */
    private void addModelFolder() throws IOException {
        String state = Environment.getExternalStorageState();

        File folder = null;
        if (state.contains(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "Article Name--" + article_name);
            folder = new File(Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name);
        }
        assert folder != null;
        if (!folder.exists()) {
            boolean wasSuccessful = folder.mkdirs();
            Log.e(TAG, "Model Directory is Created --- '" + wasSuccessful + "' Thank You !!");
        }
    }

    @Override
    public void onAnimationEnd(LikeButton likeButton) {

    }

    @Override
    public void liked(LikeButton likeButton) {
        user_Favourite_list.add(article_id);
        likeApiCall(1);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        user_Favourite_list.remove(article_id);
        likeApiCall(0);
        Toast.makeText(getContext(), "Disliked!", Toast.LENGTH_SHORT).show();
    }

    public void likeApiCall(final int value) {
        JSONObject favorites = new JSONObject();
        try {
            favorites.put("liked", value);
            favorites.put("userid", UserId);
            favorites.put("article_id", article_id);
            Log.e(TAG, "--------------------------------------------------" + UserId);
            Log.e(TAG, "--------------------------------------------------" + article_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LIKE_URL, favorites, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: Like" + response);
                if (value == 1) {
                    Toast.makeText(getContext(), "Liked!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "DisLiked!", Toast.LENGTH_SHORT).show();
                }
                try {
                    resp = response.getString("success");
                    code = response.getString("status_code");
                    message = response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject request = new JSONObject(res);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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


