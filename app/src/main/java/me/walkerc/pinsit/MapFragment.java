package me.walkerc.pinsit;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";

    @BindView(R.id.refreshButton) public AppCompatImageButton refreshButton;
    @BindView(R.id.mapView) public MapView mapView;

    private GoogleMap googleMap;
    private GeoQuery query;
    private HashMap<String, GeoLocation> locations = new HashMap<>();
    private ArrayList<Marker> activeMarkers = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, root);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        prepareMap();

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick(R.id.refreshButton)
    public void onRefreshButtonClick() {
        refreshPins(new OnKeyAddedListener() {
            @Override
            public void keyAdded(String key) {
                populateMapWithLocations();
            }
        });
    }

    private void prepareMap() {
        Context context = getActivity().getApplicationContext();
        if (context == null) {
            Log.w(TAG, "Context for mapView containing activity is null");
            return;
        }

        MapsInitializer.initialize(context);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapFragment.this.googleMap = googleMap;
            }
        });
    }

    private void refreshPins(final OnKeyAddedListener listener) {
        if (googleMap == null) {
            Log.w(TAG, "Google map instance is null");
            return;
        } if (query != null) {
            query.removeAllListeners(); //Reset position on refresh
        }

        //Calculate distance between center of screen and side
        LatLng screenSide = googleMap.getProjection().getVisibleRegion().farRight;
        LatLng center = googleMap.getCameraPosition().target;

        Location locA = new Location("A");
        locA.setLatitude(center.latitude);
        locA.setLongitude(center.longitude);
        Location locB = new Location("B");
        locB.setLatitude(screenSide.latitude);
        locB.setLongitude(screenSide.longitude);

        double distance = locA.distanceTo(locB) * .001; //convert m to km

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire gf = new GeoFire(ref);
        query = gf.queryAtLocation(new GeoLocation(center.latitude, center.longitude), distance);
        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                locations.put(key, location);
                if (listener != null)
                    listener.keyAdded(key);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.i(TAG, "GeoQuery data has been loaded/fired events");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void populateMapWithLocations() {
        for (Marker marker : activeMarkers) {
            marker.remove(); //Remove all active markers before replacing
        }

        for (GeoLocation l : locations.values()) {
            LatLng loc = new LatLng(l.latitude, l.longitude);

            Marker m = googleMap.addMarker(new MarkerOptions().position(loc));
            activeMarkers.add(m);
        }
    }

    private interface OnKeyAddedListener {
        void keyAdded(String key);
    }
}
