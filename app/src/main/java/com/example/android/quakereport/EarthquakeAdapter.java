package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nissan on 5/30/2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquakes) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false);
        }
        Earthquake earthquake = getItem(position);

        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude_view);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(earthquake.getmMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);


        //Formatting the Magnitude value to something like 5.5

        //Convert the string magnitude to Double
        Double magnitude = earthquake.getmMagnitude();
        //Define the new format for the magnitude 0.0
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        //Format the magnitude value and store the result in a String
        String formattedMagnitude = formatMagnitude(magnitude);
        //Display the Text
        magnitudeTextView.setText(formattedMagnitude);


        TextView directionTextView = (TextView) listItemView.findViewById(R.id.direction_view);
        TextView locationTextView = (TextView) listItemView.findViewById(R.id.location_view);
        String originalLocation = earthquake.getmLocation();
        //Check if the original location text has the " of " substring first
        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            //If exists, split accordingly
            String[] splittedString = originalLocation.split(LOCATION_SEPARATOR);
            directionTextView.setText(splittedString[0]+LOCATION_SEPARATOR);
            locationTextView.setText(splittedString[1]);
        } else {
            //If not exists, add the text "Near of" instead to the directionTextView
            directionTextView.setText(R.string.near_the);
            locationTextView.setText(originalLocation);
        }


        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_view);
        //Formatting the UNIX timestamp into date and time.
        Date date = new Date(earthquake.getmTimeInMilliseconds());
        String dateToDisplay = formatDate(date);
        dateTextView.setText(dateToDisplay);

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time_view);
        String timeToDisplay = formatTime(date);
        timeTextView.setText(timeToDisplay);


        return listItemView;
    }

    private int getMagnitudeColor(Double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
            switch (magnitudeFloor){
                case 0:
                case 1:
                    magnitudeColorResourceId = R.color.magnitude1;
                    break;
                case 2:
                    magnitudeColorResourceId = R.color.magnitude2;
                    break;
                case 3:
                    magnitudeColorResourceId = R.color.magnitude3;
                    break;
                case 4:
                    magnitudeColorResourceId = R.color.magnitude4;
                    break;
                case 5:
                    magnitudeColorResourceId = R.color.magnitude5;
                    break;
                case 6:
                    magnitudeColorResourceId = R.color.magnitude6;
                    break;
                case 7:
                    magnitudeColorResourceId = R.color.magnitude7;
                    break;
                case 8:
                    magnitudeColorResourceId = R.color.magnitude8;
                    break;
                case 9:
                    magnitudeColorResourceId = R.color.magnitude9;
                    break;
                default:
                    magnitudeColorResourceId = R.color.magnitude10plus;
                    break;

            }
            return ContextCompat.getColor(getContext(),magnitudeColorResourceId);
    }


    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        //Define the format for the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        //Format the date and return the String result
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        //Define the format for the time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        //Format the time and return the String result
        return timeFormat.format(dateObject);
    }

    private String formatMagnitude(double magnitude) {
        // //Define the new format for the magnitude 0.0
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        //Format the magnitude value and store the result in a String
        return decimalFormat.format(magnitude);
    }
}