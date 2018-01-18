package com.immersionslabs.lcatalogModule.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImages_Vendor extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "DownloadImages_Vendor";
    @SuppressLint("StaticFieldLeak")
    private ImageView bmImage;

    public DownloadImages_Vendor(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = EnvConstants.APP_BASE_URL + "/upload/vendorLogos/" + urls[0];
        Log.e(TAG, "Vendor_image1URL : " + urldisplay);
        Bitmap mIcon = null;
        try {

            InputStream in = new URL(urldisplay).openStream();

            mIcon = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error", "" + e.getMessage());

            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            bmImage.setImageBitmap(result);
        }
    }
}

