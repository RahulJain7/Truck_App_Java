package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.PlaceFinderListener;
import Modules.Route;
import Modules.Step;
import Modules.Step;
import Modules.Trucks;
import Modules.Truck;
import Modules.PlaceFinder;
import Modules.Place;
import Modules.Distance;
import Modules.Duration;
public class SecondActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, PlaceFinderListener {

    private int truck_number;
    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<Marker> gasStationMarkers = new ArrayList<>();
    private Boolean gasStationSet;
    private ProgressDialog progressDialog;
    private LatLng truck_position = new LatLng(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Intent new_truck = getIntent();
        truck_number = new_truck.getExtras().getInt("truck_no");
        gasStationSet = Boolean.FALSE;

        sendRequest();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void sendRequest() {
        String origin = Trucks.trucks.get(truck_number).raw_source;
        String destination = Trucks.trucks.get(truck_number).raw_destination;
        Log.i("Origin" , origin);
        Log.i("Destination", destination);

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }



    @Override
    public void onDirectionFinderSuccess(List<Step> routes, Route route) {
        progressDialog.dismiss();

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.truck))
                .title(route.start_address)
                .position(route.start_location));
        truck_position = route.start_location;
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                .title(route.end_address)
                .position(route.end_location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route.bounds,0));
        ((TextView) findViewById(R.id.tvDuration)).setText(route.du.text);
        ((TextView) findViewById(R.id.tvDistance)).setText(route.di.text);
        for (Step step : routes) {
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            if (step.Toll){
                polylineOptions.color(Color.RED);
            }

            for (int i = 0; i < step.points.size(); i++)
                polylineOptions.add(step.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    public void find_gas(View view) {
        if(gasStationSet){
            for (Marker marker : gasStationMarkers) {
                marker.remove();
            }
        }
        else{
            try {
                new PlaceFinder(this, truck_position, "gas+station", 2000).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onPlaceFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding places..!", true);

    }

    @Override
    public void onPlaceFinderSuccess(List<Place> places){
        progressDialog.dismiss();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(truck_position,14));

        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.gas_station);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        for (Place place : places) {
            gasStationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(place.name)
                    .position(place.location)));
        }
        gasStationSet = Boolean.TRUE;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    public void center_truck(View view) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(truck_position));

    }

    public void driver_info(View view) {
        Intent driver = new Intent(this, driver_info.class);
        driver.putExtra("truck_no",truck_number);
        startActivity(driver);
    }
}

