package com.immersionslabs.lcatalogModule.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.immersionslabs.lcatalogModule.ProductPageActivity;
import com.immersionslabs.lcatalogModule.ProjectPartDetailsActivity;
import com.immersionslabs.lcatalogModule.R;
import com.immersionslabs.lcatalogModule.Utils.EnvConstants;

import java.util.ArrayList;

public class ProjectpartDetailsAdapter extends RecyclerView.Adapter<ProjectpartDetailsAdapter.ViewHolder> {
    private static final String TAG = "ProjectpartDetailsAdapter";
    private Activity activity;
    private ArrayList<String> part_articles_id;
    private ArrayList<String> part_article_name;
    private ArrayList<String> part_article_images;
    private Context mcontext;

    public ProjectpartDetailsAdapter(ProjectPartDetailsActivity activity,
                                     ArrayList<String> part_articles_id,
                                     ArrayList<String> part_article_name,
                                     ArrayList<String> part_article_images, Context context) {
        this.part_articles_id = part_articles_id;
        this.part_article_name = part_article_name;
        this.part_article_images = part_article_images;
        this.activity = activity;
        this.mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_project_part_articles, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectpartDetailsAdapter.ViewHolder holder, final int position) {
        final Context[] context = new Context[1];
        String im1 = null;
        String get_image = part_article_images.get(position);
        Log.e(TAG, "project_images " +  get_image);

        holder.article_name.setText(part_article_name.get(position));
        Glide.with(activity)
                .load(EnvConstants.APP_BASE_URL + "/upload/images/" + get_image)
                .placeholder(R.drawable.dummy_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.article_image);

        holder.part_article_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvConstants.part_articles_id_var = part_articles_id.get(position);
                EnvConstants.part_article_name_var = EnvConstants.part_article_name.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_articles_description_var = EnvConstants.part_articles_description.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_articles_price_var = EnvConstants.part_articles_price.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_article_dimensions_var = EnvConstants.part_article_dimensions.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_article_images_var = EnvConstants.part_article_images.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_articles_vendor_id_var = EnvConstants.part_articles_vendor_id.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_articles_3ds_var = EnvConstants.part_articles_3ds.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_articles_pattern_var = EnvConstants.part_articles_pattern.get(EnvConstants.part_articles_id_var);
                EnvConstants.part_article__discounts_var = EnvConstants.part_article__discounts.get(EnvConstants.part_articles_id_var);
                EnvConstants.flag_article_details = true;
                EnvConstants.position = position;
                Log.e(TAG, "piceyyyyy " + EnvConstants.part_articles_price_var);
                Log.e(TAG, "id " + EnvConstants.part_articles_id_var);
                context[0] = v.getContext();
                Intent intent = new Intent(context[0], ProductPageActivity.class);
                context[0].startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return part_article_name.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView article_name;
        private AppCompatImageView article_image;
        private RelativeLayout part_article_container;

        public ViewHolder(View itemView) {

            super(itemView);
            article_name = itemView.findViewById(R.id.part_article_name);
            article_image = itemView.findViewById(R.id.part_article_image);
            part_article_container = itemView.findViewById(R.id.part_article_container);

        }
    }
}
