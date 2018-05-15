package com.immersionslabs.lcatalogModule.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.immersionslabs.lcatalogModule.ProjectPartDetailsActivity;
import com.immersionslabs.lcatalogModule.R;
import com.immersionslabs.lcatalogModule.Utils.EnvConstants;

import java.util.ArrayList;

public class ProjectPartImageSliderAdapter extends PagerAdapter {
    private ArrayList<String> Images;
    private Activity activity;
    AppCompatImageView images;
    private String project_id;
    private static final String TAG = "ProjectPartImageSliderAdapter";

    public ProjectPartImageSliderAdapter(ProjectPartDetailsActivity activity,
                                         ArrayList<String> slider_images,
                                         String project_id) {
        this.activity = activity;
        this.Images = slider_images;
        this.project_id = project_id;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_project_part_details, container, false);
        Log.e(TAG, "projectpartimages  " + project_id);

        images = v.findViewById(R.id.part_image_view);
        String urls = Images.get(position);

        Glide.with(activity)
                .load(EnvConstants.APP_BASE_URL + "/upload/projectpartimages/partimages_" + project_id + "_" + urls)
                .placeholder(R.drawable.dummy_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(images);

        container.addView(v);
        return v;
    }

    @Override
    public int getCount() {
        return Images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}
