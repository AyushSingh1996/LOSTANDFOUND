package com.lostandfound.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetLocation extends FragmentActivity implements OnMapReadyCallback ,OnMapClickListener{
    Button GetAddressfromMarker;
    GoogleMap googleMap;
    Button SetAddress;
    LatLng markerLatlang;
    String CoordianteGPS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setdestination);
        SetAddress = (Button) findViewById(R.id.btnSetAddress);
        GetAddressfromMarker = (Button) findViewById(R.id.btnGetAddressfromMarker);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Setting a click event handler for the map



    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap=map;
        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);
                markerLatlang=latLng;
                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);
            }


        });

        GetAddressfromMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (markerLatlang != null) {
                        Geocoder geocoder;


                        try {
                            List<android.location.Address> addresses;
                            geocoder = new Geocoder(SetLocation.this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(markerLatlang.latitude,markerLatlang.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            CoordianteGPS= address+" "+city+" "+ state+" "+country+" ";
                            Toast.makeText(SetLocation.this,CoordianteGPS,Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }



        });

        SetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getIntent().hasExtra("FromSignUp")) {

              /*  if (markergpsflag==1) {
                    intent.putExtra("Address", CoordianteMarker);
                }*/
                    Intent returnIntent = getIntent();
                    returnIntent.putExtra("Address", CoordianteGPS);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();

                }
                if (getIntent().hasExtra("FromFormPage")) {

              /*  if (markergpsflag==1) {
                    intent.putExtra("Address", CoordianteMarker);
                }*/
                    Intent returnIntent = getIntent();
                    returnIntent.putExtra("Address", CoordianteGPS);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();


                }


            }
        });

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}