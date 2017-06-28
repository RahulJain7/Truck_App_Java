package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

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
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Step;
import Modules.Truck;
import Modules.Trucks;

public class MapsActivity extends Activity {

    private List<Truck> trucks = new ArrayList<Truck>();
    private Truck truck;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        Trucks.trucks.clear();
        BufferedReader input = null;
        File file = null;
        try {
            file = new File(getFilesDir(), "MyFile"); // Pass getFilesDir() and "MyFile" to read file

            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                buffer.append(line);
                Log.i("hel",line);
                String[] parts = line.split("_");
                truck = new Truck("");
                truck.Name = parts[0];
                truck.raw_source = parts[1];
                truck.raw_destination = parts[2];
                truck.driver_name = parts[3];
                truck.driver_number = parts[4];

                Trucks.trucks.add(truck);
                add_button(Trucks.trucks.size()-1);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Start","yes");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Stop","yes");
        String fileName = "MyFile";
        String content = "";
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            for (Truck t : Trucks.trucks) {
                content = t.Name+"_"+t.raw_source+"_"+t.raw_destination+"_"+t.driver_name+"_"+t.driver_number+"\n";
                outputStream.write(content.getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Destroy","yes");

    }


    public void addTrucks(View view) {

        int i = Trucks.trucks.size();
        truck = new Truck("Truck " + i);

        Trucks.trucks.add(truck);
        Intent add_truck = new Intent(this,new_truck.class);
        add_truck.putExtra("truck_no",i);
        startActivityForResult(add_truck,1);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // TODO Extract the data returned from the child Activity.
            String returnValue = data.getStringExtra("some_key");
            int truck_number = data.getExtras().getInt("truck_no");
            if(data.getExtras().getBoolean("truck")){
                add_button(truck_number);
            }
            else{
                Trucks.trucks.remove(Trucks.trucks.size()-1);
            }
        }
        else if(requestCode == 2){

        }

    }

    public void add_button(int truck_number){
        Button b_truck = new Button(this);
        b_truck.setText(Trucks.trucks.get(truck_number).Name);
        LinearLayout ll = (LinearLayout)findViewById(R.id.truck_menu);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        b_truck.setId(truck_number);
        b_truck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawMap(v.getId());
            }
        });
        ll.addView(b_truck);
    }


    public void drawMap(int truck_no){
        Intent map = new Intent(this, SecondActivity.class);
        map.putExtra("truck_no",truck_no);
        startActivityForResult(map,2);
    }

    public void clear_trucks(View view) {
        Trucks.trucks.clear();
        LinearLayout ll = (LinearLayout)findViewById(R.id.truck_menu);
        ll.removeAllViews();
    }
}
