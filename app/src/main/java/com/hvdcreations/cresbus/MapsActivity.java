package com.hvdcreations.cresbus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Spinner spinner;

    String spinnertext;
    TextView textView;

    FirebaseDatabase mDatabase;
    DatabaseReference mref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance();
        mref = mDatabase.getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        spinner = findViewById(R.id.spinnerB);
        textView = findViewById(R.id.textView);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnertext = spinner.getSelectedItem().toString();
                onMapReady(mMap);
                mMap.clear();
                new getlocation(spinnertext).execute();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        List<String> categories = new ArrayList<String>();
        categories.add("Bus 1");
        categories.add("Bus 2");
        categories.add("Bus 3");
        categories.add("Bus 4");
        categories.add("Bus 5");
        categories.add("Bus 6");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().isMyLocationButtonEnabled();


        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);

    }


    public class getlocation extends AsyncTask<Void,String,String> {

        ProgressDialog dialog;
        String lt,lg,busname;

        public getlocation(String spinnertext) {
            this.busname = spinnertext;        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(MapsActivity.this,"Tracking...","Pls. wait while we Track your Bus");

        }

        @Override
        protected String doInBackground(Void... voids) {

                mDatabase = FirebaseDatabase.getInstance();

                mref = mDatabase.getReference().child(busname).child("location").child("lati");
                mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot post : dataSnapshot.getChildren()){

                            lt = post.getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mref = mDatabase.getReference().child(busname).child("location").child("longi");
                mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot post : dataSnapshot.getChildren()){

                            lg = post.getValue(String.class);
                        }

                        if (lt == null && lg == null){
                            Toast.makeText(MapsActivity.this, "Bus not Available", Toast.LENGTH_SHORT).show();
                        }

                        if(lt!=null && lg!=null){
                            try{
                                LatLng loc = new LatLng(Double.parseDouble(lt),Double.parseDouble(lg));
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(loc).title(spinnertext).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,17));
                            }catch(Exception e){
                                Toast.makeText(MapsActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);


               Handler handler = new Handler();
               handler.postDelayed(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
               }, 3000);



        }
    }



}
