package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.wallet.LineItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Step;
import Modules.Truck;
import Modules.Trucks;

public class new_truck extends Activity{
    private int truck_number;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_truck);
        Intent main = getIntent();
        truck_number = main.getExtras().getInt("truck_no");

    }

    public void Clicked(View view) {
        EditText source = (EditText)findViewById(R.id.source_input);
        EditText dest = (EditText)findViewById(R.id.dest_input);
        EditText name = (EditText)findViewById(R.id.name_input);
        EditText driver_name = (EditText)findViewById(R.id.driver_name_input);
        EditText driver_number = (EditText)findViewById(R.id.phone_input);

        if (source.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
        }
        else if (dest.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
        }
        else {

            Trucks.trucks.get(truck_number).raw_source = source.getText().toString();

            Trucks.trucks.get(truck_number).raw_destination = dest.getText().toString();

            Trucks.trucks.get(truck_number).Name = name.getText().toString();

            Trucks.trucks.get(truck_number).driver_name = driver_name.getText().toString();

            Trucks.trucks.get(truck_number).driver_number = driver_number.getText().toString();

            Intent maps = new Intent(this, SecondActivity.class);

            maps.putExtra("truck_no", truck_number);
            startActivityForResult(maps,1);


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("truck", Boolean.TRUE);
            resultIntent.putExtra("truck_no",truck_number);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("truck",Boolean.FALSE);
        resultIntent.putExtra("truck_no",truck_number);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}