package com.neji.kaboo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Neji on 1/21/2015.
 */
public class MapActivity extends FragmentActivity {
    private static GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapper);
        final GlobalClass globalVariable= (GlobalClass) getApplicationContext();

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        setUpMapIfNeeded();
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng LOC = new LatLng(Double.parseDouble(globalVariable.getLatitude()), Double.parseDouble(globalVariable.getLongitude()));
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(LOC)
                            .title(globalVariable.getDriverName()+"'s current location")
                            .snippet(globalVariable.getDistanceAway())
                            .draggable(false)

            );
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(LOC)
                    .zoom(15.5F)
                    .bearing(200F) // orientation
                    .tilt(50F) // viewing angle

                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}

