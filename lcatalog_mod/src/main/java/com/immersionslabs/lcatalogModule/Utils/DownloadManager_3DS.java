package com.immersionslabs.lcatalogModule.Utils;

import android.os.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager_3DS {
    private String DOWNLOAD_URL;
    private String Article_Name, Article_3d;

    public DownloadManager_3DS(String url, String article_name, String article_3ds) {

        DOWNLOAD_URL = url;
        Article_Name = article_name;
        Article_3d = article_3ds;

        try {
            Download();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Download() throws IOException {
        URL u = new URL(DOWNLOAD_URL);
        URLConnection conn = u.openConnection();
        int contentLength = conn.getContentLength();

        DataInputStream stream = new DataInputStream(u.openStream());

        byte[] buffer = new byte[contentLength];
        stream.readFully(buffer);
        stream.close();

        DataOutputStream file_out = new DataOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + Article_Name + "/" + Article_3d));
        file_out.write(buffer);
        file_out.flush();
        file_out.close();
    }
}

