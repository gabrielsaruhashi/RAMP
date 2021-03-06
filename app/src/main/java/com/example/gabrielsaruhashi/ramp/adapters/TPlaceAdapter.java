package com.example.gabrielsaruhashi.ramp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gabrielsaruhashi.ramp.R;
import com.example.gabrielsaruhashi.ramp.activities.PlaceDetailsActivity;
import com.example.gabrielsaruhashi.ramp.models.Place;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masayuki on 10/5/18.
 */

public class TPlaceAdapter extends RecyclerView.Adapter<TPlaceAdapter.ViewHolder>{

    Context context;
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView name;
        public TextView hours;
        public TextView description;
        public TextView address;
        public TextView phoneNumber;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemView.setOnClickListener(this);

            name = (TextView) itemView.findViewById(R.id.name);
            hours = (TextView)itemView.findViewById(R.id.hours);
            description = (TextView) itemView.findViewById(R.id.description);
            address = (TextView) itemView.findViewById(R.id.address);
            phoneNumber = (TextView) itemView.findViewById(R.id.phoneNumber);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // ensure valid position
            if (position != RecyclerView.NO_POSITION) {
                // get place at position
                Place place = mPlaces.get(position);
                Intent intent = new Intent(context, PlaceDetailsActivity.class);
                intent.putExtra("places", Parcels.wrap(place));
                intent.putExtra("categoryTitle", place.getCategoryTitle());
                // show the activity
                context.startActivity(intent);
            }
        }
    }

    private List<Place> mPlaces;
    public TPlaceAdapter(ArrayList<Place> contacts) {
        mPlaces= contacts;
    }

    @Override
    public TPlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_t_place, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TPlaceAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Place place = mPlaces.get(position);

        // Set item views based on your views and data model
//        TextView id = viewHolder.id;
//        id.setText("Id: " + Integer.toString(place.getId()));
//        TextView lat = viewHolder.lat;
//        lat.setText(" Latitude: " + Double.toString(place.getLat()));
//        TextView lon = viewHolder.lon;
//        lon.setText(" Longitude: " + Double.toString(place.getLon()));
        if(place.getName() != null){
            TextView name = viewHolder.name;
            name.setText(place.getName());
        }
        if(!place.getHours().equals("null")){
            TextView hours = viewHolder.hours;
            hours.setText("Hours: " + place.getHours());
        } else {
            TextView hours = viewHolder.hours;
            hours.setText("Hours: N/A");
        }
        if(place.getDescription() != null){
            TextView description = viewHolder.description;
            description.setText(place.getDescription());
        }
        if(place.getAddress() != null){
            TextView address = viewHolder.address;
            address.setText(place.getAddress());
        }
        if(place.getPhoneNumer() != null){
            TextView phoneNumber = viewHolder.phoneNumber;
            phoneNumber.setText(place.getPhoneNumer());
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPlaces.size();
    }
}
