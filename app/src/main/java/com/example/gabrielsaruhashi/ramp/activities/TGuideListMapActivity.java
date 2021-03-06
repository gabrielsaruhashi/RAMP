package com.example.gabrielsaruhashi.ramp.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gabrielsaruhashi.ramp.R;
import com.example.gabrielsaruhashi.ramp.clients.Two11Client;
import com.example.gabrielsaruhashi.ramp.fragments.PlacesFragment;
import com.example.gabrielsaruhashi.ramp.models.Place;
import com.example.gabrielsaruhashi.ramp.models.SubCategory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Masayuki on 9/7/18.
 */

public class TGuideListMapActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    //https://developer.android.com/training/location/retrieve-current#java
    //To get user location
    private FusedLocationProviderClient mFusedLocationClient;

    /*
    Boolean for switching between list view (default) and map view.
    Depending on boolean value, upon click of button either list view or map view will be loaded into the fragment container.
     */
    boolean isMapView = false;
    private PlacesFragment placesFragment;
    SubCategory subcategory;
    TextView title;
    TextView tvSeeGuide;
    TextView tvDescript;

    private final static String KEY_LOCATION = "location";
    ArrayList<Place> places = new ArrayList<>();

    Location ulocation;
    LatLng uLatLong;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //https://developer.android.com/training/location/retrieve-current#java
        //To get user location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if(PermissionChecker.checkSelfPermission(getApplicationContext(),"android.permission.ACCESS_FINE_LOCATION")
                == PermissionChecker.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location userlocation) {
                            // Got last known location. In some rare situations this can be null.
                            if (userlocation != null) {
                                // Logic to handle location object
                                Log.d("search", "USER LOCATION" + userlocation.toString());
                                ulocation = userlocation;
                                uLatLong = new LatLng(userlocation.getLatitude(), userlocation.getLongitude());
                            }
                            else{
                                //QUESTION: WHERE DO USERS GRANT PERMISSION FOR LOCATION?
                                //https://stackoverflow.com/questions/3500197/how-to-display-toast-in-android
//                                Toast.makeText(TGuideListMapActivity.this, "User ",
//                                        Toast.LENGTH_LONG).show();
                                uLatLong = new LatLng(41.3083, -72.9279);
                            }
                        }
                    });
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tguide_list_map);
        if (TextUtils.isEmpty(getResources().getString(R.string.google_map_API_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        title = findViewById(R.id.tvMapTitle);
        tvSeeGuide = findViewById(R.id.tvSeeGuide);
        tvDescript = findViewById(R.id.tvDescript);

        subcategory = Parcels.unwrap(getIntent().getParcelableExtra(SubCategory.class.getSimpleName()));
        title.setText(subcategory.getTitle().toString());
        // tvDescript.setText(subcategory.getCatchPhrase().toString());
        if (subcategory.hasGuide() == 1) {
            // has guide, show text to view guide
            tvSeeGuide.setVisibility(View.VISIBLE);
            tvSeeGuide.setText("See our " + subcategory.getTitle().toString() + " guide!");
            tvSeeGuide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(TGuideListMapActivity.this, RGuideIndexActivity.class);
                    i.putExtra(SubCategory.class.getSimpleName(), Parcels.wrap(subcategory));
                    startActivity(i);
                }
            });
        }
        String parameter = "";
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("pairs");
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("Details-->", jo_inside.getString("primary_care"));
                String formula_value = jo_inside.getString("primary_care");
                parameter = jo_inside.getString("primary_care");
                // String url_value = jo_inside.getString("url");

                //Add your values in your `ArrayList` as below:
                m_li = new HashMap<String, String>();
                m_li.put("formule", formula_value);
                // m_li.put("url", url_value);

                formList.add(m_li);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // load places fragment
        Two11Client newClient = new Two11Client();
        newClient.search("enfield", new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray placesJson = response.getJSONArray("results");
                    places.clear();
                    for (int i = 0; i < placesJson.length(); i++) {
                        Place newPlace = Place.fromJson(placesJson.getJSONObject(i));
                        newPlace.setCategoryTitle(subcategory.getTitle().toString());
                        places.add(newPlace);
                    }
                    placesFragment = PlacesFragment.newInstance(places);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_placeholder, PlacesFragment.newInstance(places));
                    ft.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String loadJSONFromAsset() {
        Log.d("TGuideListMapActivity", "loadJSON");
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            //MapDemoActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            //MapDemoActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSwitchView (View view) {
        if (isMapView) {
            // trigger list view
            view.setSelected(true);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            mapFragment.getView().setVisibility(View.INVISIBLE);
            ft.replace(R.id.fragment_placeholder, PlacesFragment.newInstance(places));
            ft.commit();
            isMapView = false;
        } else {
            // trigger map view
            view.setSelected(false);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(placesFragment);
            ft.commit();
            // load map fragment
            FragmentManager fm = getSupportFragmentManager();
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.fragment_placeholder, mapFragment).commit();
            Two11Client newClient = new Two11Client();
            newClient.search("enfield", new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray placesJson = response.getJSONArray("results");
                        places.clear();
                        places.addAll(Place.fromJson(placesJson));
                        // TODO (masayukinagase) instead of doing this try / catch, move it to an AsyncTask
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap map) {
                                    map.addMarker(new MarkerOptions()
                                        .position(uLatLong)
                                        .icon(BitmapDescriptorFactory.defaultMarker(180)));
                                    LatLng location = null;
                                    for(int i = 0; i < places.size(); i++){
                                        location = new LatLng(places.get(i).getLat(), places.get(i).getLon());
                                        Marker m = map.addMarker(new MarkerOptions().position(location)
                                                .title(places.get(i).getName())
                                        );
                                        m.setTag(places.get(i));
                                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {
                                                Place currentPlace = (Place) marker.getTag();
                                                Intent i = new Intent(TGuideListMapActivity.this, PlaceDetailsActivity.class);
                                                i.putExtra("places", Parcels.wrap(currentPlace));
                                                startActivity(i);
                                                return true;

                                            }
                                        });

                                        Log.d("search", "latitude:" + places.get(i).getLat());
                                    }


                                    //https://stackoverflow.com/questions/37428464/how-can-i-check-permission-under-api-level-23
                                    //https://stackoverflow.com/questions/32491960/android-check-permission-for-locationmanager
                                    //https://stackoverflow.com/questions/33407250/checkselfpermission-method-is-not-working-in-targetsdkversion-22
                                    //https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLngBounds
                                    //https://developers.google.com/maps/documentation/android-sdk/views
                                    map.moveCamera(CameraUpdateFactory.newLatLng(uLatLong));
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(uLatLong, 12.0f));
                                    loadMap(map);
                                }
                            });
                        } else {
                            Log.d("search", "failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            isMapView = true;
        }
    }

}
