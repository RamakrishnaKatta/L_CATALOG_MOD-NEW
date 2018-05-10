package com.immersionslabs.lcatalogModule.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor, editor2;

    // Shared preferences file name
    private static final String PREF_NAME_1 = " L_Catalog_welcome_Screen ";
    private static final String PREF_NAME_2 = " L_Catalog_ProductPageActivityScreen ";


    private static final String WELCOMEACTIVITY_SCREEN_LAUNCH = "WelcomeActivityScreenLaunch";
    private static final String PRODUCTPAGEACTIVITY_LAUNCH_SCREEN = "ProductPageActivityLaunchScreen";


    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME_1, PRIVATE_MODE);
        editor = pref.edit();

        pref2 = context.getSharedPreferences(PREF_NAME_2, PRIVATE_MODE);
        editor2 = pref2.edit();

    }

    /*welcomeScreen Pref*/
    public void SetWelcomeActivityScreenLaunch(boolean WelcomeScreen) {
        editor.putBoolean(WELCOMEACTIVITY_SCREEN_LAUNCH, WelcomeScreen);
        editor.commit();
    }

    public boolean WelcomeActivityScreenLaunch() {
        return pref.getBoolean(WELCOMEACTIVITY_SCREEN_LAUNCH, true);
    }


    /*ProductPageActivity Screen Pref*/
    public void setProductPageActivityScreenLaunch() {
        editor2.putBoolean(PRODUCTPAGEACTIVITY_LAUNCH_SCREEN, false);
        editor2.commit();
    }

    public boolean ProductPageActivityScreenLaunch() {
        return pref2.getBoolean(PRODUCTPAGEACTIVITY_LAUNCH_SCREEN, true);
    }


}
