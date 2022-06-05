package com.mapmyindia.sdk.demo;

import android.app.Application;

import com.mapmyindia.sdk.maps.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;

import timber.log.Timber;

/**
 * Created by CEINFO on 29-06-2018.
 */

public class MapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        MapmyIndiaAccountManager.getInstance().setRestAPIKey(getRestAPIKey());
        MapmyIndiaAccountManager.getInstance().setMapSDKKey(getMapSDKKey());
        MapmyIndiaAccountManager.getInstance().setAtlasClientId(getAtlasClientId());
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret(getAtlasClientSecret());
        MapmyIndia.getInstance(this);
    }

    public String getAtlasClientId() {
        return "33OkryzDZsIBrQJnqft9IOjW_GDwY3FrVfiUzFqLoXqlN7gXDEBVSwTP3sQ9sEz4pPHfYiwEFFV6UsJzhxlYAUfl8Pta0uwd";
    }

    public String getAtlasClientSecret() {
        return "lrFxI-iSEg-sZephtBLcQJcs923N4BX92EdKGvf5IzPGXgRF_hfExxuscZeZfnqtBAqT7xPmS_sLP4Wl1nzaPCuiN2gb5Xh7Oe7LfdkJhwQ=";
    }

    public String getMapSDKKey() {
        return "68fe2d468768599c9aace3013c9d7beb";
    }

    public String getRestAPIKey() {
        return "68fe2d468768599c9aace3013c9d7beb";
    }

}
