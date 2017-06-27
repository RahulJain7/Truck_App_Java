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
import android.view.View.OnClickListener;
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

public class MapsActivity extends Activity {

    private List<Truck> trucks = new ArrayList<Truck>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
    }

    public void addTrucks(View view) {

        int i = Trucks.trucks.size();
        Truck truck = new Truck("Truck " + i);

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
        }
        else if(requestCode == 2){

        }

    }


    public void drawMap(int truck_no){
        Intent map = new Intent(this, SecondActivity.class);
        map.putExtra("truck_no",truck_no);
        startActivityForResult(map,2);
    }

}
