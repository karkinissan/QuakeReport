package com.example.android.quakereport;

/**
 * Created by Nissan on 5/30/2017.
 */

public class Earthquake {

    private Double mMagnitude;
    private String mLocation;
    private Long mTimeInMilliseconds;
    private String mUrl;



    public Earthquake(Double magnitude, String location, long timeInMilliseconds, String url) {
        this.mMagnitude = magnitude;
        this.mLocation = location;
        this.mTimeInMilliseconds = timeInMilliseconds;
        this.mUrl = url;
    }

    public Double getmMagnitude() {
        return mMagnitude;
    }

    public String getmLocation() {
        return mLocation;
    }

    public Long getmTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }
    public String getmUrl() {
        return mUrl;
    }

}
