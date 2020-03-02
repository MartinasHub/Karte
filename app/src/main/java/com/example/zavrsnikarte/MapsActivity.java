package com.example.zavrsnikarte;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity {

    private static LatLng LAT_LNG_OSIJEK = new LatLng(45.55111, 18.69389);
    private static final float INIT_ZOOM_LEVEL = 17.0f;

    private GoogleMap mMap;

    private static final String ADDRESS = "Trg Ljudevita Gaja 7, Osijek";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return ;
        int id = item.getItemId();
        if (id == R.id.izadi) {
            new AlertDialog.Builder(this)
                    .setTitle("Izlaz iz aplikacije")
                    .setMessage("Da li ste sigurni da želite izaći iz aplikacije?")
                    .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("NE", null)
                    .show();
        }
        if(id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded(){
        if (mMap == null){
            mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null){
                setUpMap();
            }
        }
    }
    private void setUpMap(){
        mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
        configureMap();
        LAT_LNG_OSIJEK = getLatLngFromAdress(ADDRESS);
        addMarker(LAT_LNG_OSIJEK);
        setAdapter();
        animateCamera(LAT_LNG_OSIJEK);
        setListener();
    }

    private void configureMap(){
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
    }

    private void addMarker(LatLng latLng){
        mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_black_24dp)).title("Osijek"));
    }

    private void setAdapter() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);
                TextView markerText = (TextView) v.findViewById(R.id.marker_text);
                markerIcon.setImageResource(R.drawable.baseline_my_location_black_24dp);
                markerText.setText("Osijek");
                return v;
            }
        });
    }

    private void animateCamera(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, INIT_ZOOM_LEVEL));
    }

    private LatLng getLatLngFromAdress(String address){
        LatLng latLng = LAT_LNG_OSIJEK;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double langitude = addresses.get(0).getLongitude();
                latLng = new LatLng(latitude, langitude);
            }
        }
            catch (IOException e){

            }
            return latLng;

        }

    private void setListener(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);
                String address = getAddressFromLatLng(latLng);
                Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getAddressFromLatLng(LatLng latLng){
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
           address = addresses.get(0).getAddressLine(0);
        }
        catch (IOException e){

        }
        return address;
    }


}
