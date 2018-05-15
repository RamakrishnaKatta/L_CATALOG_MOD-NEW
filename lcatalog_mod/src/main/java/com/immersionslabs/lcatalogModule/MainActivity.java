package com.immersionslabs.lcatalogModule;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.immersionslabs.lcatalogModule.Utils.PrefManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static final int MY_PERMISSIONS_REQUEST = 10;
    Button click, Augment, about_us, faq,Projects;
    boolean success = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        RequestPermissions();
        CreateFolderStructure();

        click = findViewById(R.id.catalog);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                startActivity(intent);
            }
        });

        Augment = findViewById(R.id.augment);
        Augment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ARNativeActivity.class);
                startActivity(intent);
            }
        });

        Projects = findViewById(R.id.projects);
        Projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,ProjectActivity.class);
                startActivity(intent);

            }
        });

        about_us = findViewById(R.id.about_us);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        faq = findViewById(R.id.faq);
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, faqActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and its LayerDrawable (layer-list)

        // MenuItem item = menu.findItem(R.id.action_notifications);
        // NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);

        // force the ActionBar to relayout its MenuItems. onCreateOptionsMenu(Menu) will be called again.

        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will automatically handle clicks on the
        // Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notifications) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
            builder.setTitle("Watch the welcome Slider, If you missed it");
            builder.setMessage("To see the welcome slider again, either you can go to Settings -> apps -> welcome slider -> clear data or Press OK ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // We normally won't show the welcome slider again in real app but this is for testing
                    PrefManager prefManager = new PrefManager(getApplicationContext());

                    // make first time launch TRUE
                    prefManager.SetWelcomeActivityScreenLaunch(true);

                    startActivity(new Intent(MainActivity.this, OnBoardingActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

            return true;

        } else if (id == R.id.action_replay_info) {

            boolean delete_models = false;
            boolean delete_patterns = false;
            boolean delete_data = false;

            File dir_models = new File(Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/cache/Data/models");
            File dir_patterns = new File(Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/cache/Data/patterns");
            File dir_data = new File(Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/cache/Data");

            if (dir_models.isDirectory()) {
                String[] children_models = dir_models.list();

                Log.e(TAG, "" + Arrays.toString(children_models));

                for (int i = 0; i < children_models.length; i++) {
                    delete_models = new File(dir_models, children_models[i]).delete();
                }
                Log.e(TAG, "Files inside Models Folder deleted : " + delete_models);
            }

            if (dir_patterns.isDirectory()) {
                String[] children_patterns = dir_patterns.list();

                Log.e(TAG, "" + Arrays.toString(children_patterns));

                for (int i = 0; i < children_patterns.length; i++) {
                    delete_patterns = new File(dir_patterns, children_patterns[i]).delete();
                }
                Log.e(TAG, "Files inside Patterns Folder deleted : " + delete_patterns);
            }

            if (dir_data.isDirectory()) {
                String[] children_data = dir_data.list();

                Log.e(TAG, "" + Arrays.toString(children_data));

                for (int i = 0; i < children_data.length; i++) {
                    delete_data = new File(dir_data, children_data[i]).delete();
                }
                Log.e(TAG, "Files inside Data Folder deleted : " + delete_data);
            }

            if (delete_models || delete_patterns || delete_data) {
                Toast.makeText(getBaseContext(), "Debugging: Cache Files Removed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Debugging: Cache doesn't exist", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void CreateFolderStructure() {
        String root_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD";
        String models_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD/Models";
        String screenshots_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD/Screenshots";
        String cache_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD/cache";

        File Root_Folder, Models_Folder, Screenshots_Folder, Cache_Folder;

        if (Environment.getExternalStorageState().contains(Environment.MEDIA_MOUNTED)) {
            Root_Folder = new File(root_Path);
            Models_Folder = new File(models_Path);
            Screenshots_Folder = new File(screenshots_Path);
            Cache_Folder = new File(cache_Path);
        } else {
            Root_Folder = new File(root_Path);
            Models_Folder = new File(models_Path);
            Screenshots_Folder = new File(screenshots_Path);
            Cache_Folder = new File(cache_Path);
        }

        if (Root_Folder.exists()) {
        } else {

            if (!Root_Folder.exists()) {
                success = Root_Folder.mkdirs();
            }
            if (!Models_Folder.exists()) {
                success = Models_Folder.mkdirs();
            }
            if (!Screenshots_Folder.exists()) {
                success = Screenshots_Folder.mkdirs();
            }
            if (!Cache_Folder.exists()) {
                success = Cache_Folder.mkdirs();
            }
            if (success) {

            }
        }
    }

    private void RequestPermissions() {

        int PermissionCamera = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
        int PermissionReadStorage = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int PermissionWriteStorage = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (PermissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (PermissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (PermissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.e(TAG, "Permission callback called-------");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with all three permissions
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check all three permissions
                    if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "Camera and Storage(Read and Write) permission granted");
                        // Process the normal flow
                        // Else any one or both the permissions are not granted
                    } else {
                        Log.e(TAG, "Some permissions are not granted ask again ");

                        // Permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // ShouldShowRequestPermissionRationale will return true
                        // Show the dialog or SnackBar saying its necessary and try again otherwise proceed with setup.

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            showDialogOK(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    RequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    android.os.Process.killProcess(android.os.Process.myPid());
                                                    System.exit(0);
                                                    break;
                                            }
                                        }
                                    });
                        }
                        // permission is denied (and never ask again is checked)
                        // shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                            // Proceed with logic by disabling the related features or quit the app.
                        }
                    } // other 'case' lines to check for other permissions this app might request
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("Storage and Camera Services are Mandatory for this Application")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
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
