package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;


/**
 * Created by Nissan on 6/5/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {
    private String url;
    public EarthquakeLoader(Context context, String url) {
        super(context);
        Log.v(LOG_TAG,"EARTHQUAKELOADER CONSTRUCTOR");
        this.url = url;
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        Log.v(LOG_TAG,"EARTHQUAKELOADER.LOADINBACKGROUND METHOD CALLED");
        if (url==null){
            Log.v(LOG_TAG,"Url is null");
            return null;
        }
        ArrayList<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(url);
        return earthquakes;
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG,"EARTHQUAKELOADER.ONSTARTLOADING() METHOD CALLED");
        forceLoad();
    }
}
