/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {
    private EarthquakeAdapter mAdapter;

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    public static String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
//            "?format=geojson&eventtype=earthquake&orderby=time&minmag=1.0&limit=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
    final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        getLoaderManager().restartLoader(0, null, EarthquakeActivity.this);
                    }
                },3000);
            }
        });
        if (!hasInternetConnection()){
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_circle);
            progressBar.setVisibility(View.GONE);
            TextView emptyView = (TextView)findViewById(R.id.empty_view);
            emptyView.setText(R.string.no_internet);
            return;
        }


        ListView listView = (ListView) findViewById(R.id.list);

        TextView emptyView = (TextView)findViewById(R.id.empty_view);

        emptyView.setText(R.string.loading_earthquakes);

        listView.setEmptyView(emptyView);
//        new EarthquakeAsyncTask().execute(USGS_REQUEST_URL);

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        Log.v(LOG_TAG,"INITIALISING THE LOADER initloader()");
        getLoaderManager().initLoader(0, null, EarthquakeActivity.this);

    }

    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG,"onCREATELOADER IS CALLED");

        //Get the sharedPreferences object
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Get the value of the Minimum Magnitude set in the settings
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        //Get the value of the Sort mode set in the settings
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        //Get the value of Max Results.
        String maxResults = sharedPreferences.getString(
                getString(R.string.settings_max_results_key),
                getString(R.string.settings_max_results_default));

        //Creating the URI for retrieving data
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //Adding queries to the base URI
        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("limit",maxResults);
        uriBuilder.appendQueryParameter("minmag",minMagnitude);
        uriBuilder.appendQueryParameter("orderby",orderBy);
        return new EarthquakeLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> data) {
        Log.v(LOG_TAG,"onLoadFinished IS CALLED");

        //Removing the Progress circle after data is loaded.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_circle);
        progressBar.setVisibility(View.GONE);

        //If no earthquake data is found, display "No earthquake data"
        TextView emptyView = (TextView)findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_earthquakes);

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            Log.v(LOG_TAG,"onLoadFinished UPDATING UI");
            updateUI(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        Log.v(LOG_TAG,"onLoaderReset IS CALLED");
        // Loader reset, so we can clear out our existing data.
//        mAdapter.clear();
    }
    private boolean hasInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     *
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
     * progress updates, so the second generic is just Void.
     *
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    /*private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        *//**
     * This method runs on a background thread and performs the network request.
     * We should not update the UI from a background thread, so we return a list of
     * {@link Earthquake}s as the result.
     *//*
        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            ArrayList<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(urls[0]);
            return earthquakes;

        }*/

    /**
     * This method runs on the main UI thread after the background work has been
     * completed. This method receives as input, the return value from the doInBackground()
     * method. First we clear out the adapter, to get rid of earthquake data from a previous
     * query to USGS. Then we update the adapter with the new list of earthquakes,
     * which will trigger the ListView to re-populate its list items.
     *//*

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
            if (earthquakes.isEmpty()) {
                return;
            }
            updateUI(earthquakes);
        }
    }*/
    private void updateUI(final ArrayList<Earthquake> earthquakes) {
        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new EarthquakeAdapter(this, earthquakes);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake = earthquakes.get(position);
                String url = currentEarthquake.getmUrl();
                Uri webPage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

    }
    //Overriding methods to to inflate the menu, and respond when users click on our menu item


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_settings){
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
