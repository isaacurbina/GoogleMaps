package com.mac.isaac.googlemapssaul;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, DirectionCallback {

    private GoogleMap mMap;
    CheckBox cbLocation, cbTraffic;
    final int MY_PERMISSIONS_REQUEST_CODE = 123;
    Marker nMarker1, nMarker2, nMarker3, nMarker4;
    LatLng[] MARKERS = new LatLng[4];
    LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude, longitude;
    static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    Spinner spTypes;
    ArrayAdapter spAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MARKERS[0] = new LatLng(29.95, -90.06);
        MARKERS[1] = new LatLng(25.77, -80.20);
        MARKERS[2] = new LatLng(32.77, -96.79);
        MARKERS[3] = new LatLng(40.71, -74.00);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        cbLocation = (CheckBox) findViewById(R.id.cb_location);
        cbTraffic = (CheckBox) findViewById(R.id.cb_traffic);
        spTypes = (Spinner) findViewById(R.id.sp_types);
        spAdapter = ArrayAdapter.createFromResource(this, R.array.layers, android.R.layout.simple_spinner_item);
        spTypes.setAdapter(spAdapter);
        spTypes.setOnItemSelectedListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();
        handlerMarkers();
    }

    public void comeToMe(View view) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLocation(), 8f));
    }

    public void goToMark1(View v) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MARKERS[0], 8f));
    }

    public void goToMark2(View v) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MARKERS[1], 8f));
    }

    public void goToMark3(View v) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MARKERS[2], 8f));
    }

    public void goToMark4(View v) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MARKERS[3], 8f));
    }

    private void handlerMarkers() {
        double distance = 0f;
        distance = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[0]);
        nMarker1 = mMap.addMarker(new MarkerOptions()
                        .position(MARKERS[0])
                        .title("Louisiana")
                        .snippet("New Orleans "+distance)
        );
        distance = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[1]);
        nMarker2 = mMap.addMarker(new MarkerOptions()
                        .position(MARKERS[1])
                        .title("Florida")
                        .snippet("Miami "+distance)
        );
        distance = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[2]);
        nMarker3 = mMap.addMarker(new MarkerOptions()
                        .position(MARKERS[2])
                        .title("Texas")
                        .snippet("Dallas "+distance)
        );
        distance = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[3]);
        nMarker4 = mMap.addMarker(new MarkerOptions()
                        .position(MARKERS[3])
                        .title("New York")
                        .snippet("New York "+distance)
        );
    }

    public void enableLocation(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, getResources().getString(R.string.permission_required_toast), Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_CODE);
            cbLocation.setChecked(false);
        } else {
            mMap.setMyLocationEnabled(cbLocation.isChecked());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cbLocation.setChecked(true);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        cbLocation.setChecked(false);
                    } else {
                        mMap.setMyLocationEnabled(true);
                        cbLocation.setChecked(true);
                    }
                } else {
                }
                return;
            }
        }
    }

    public LatLng getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        } else {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1: mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2: mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 3: mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void enableTraffic(View view) {
        mMap.setTrafficEnabled(cbTraffic.isChecked());
    }

    public double calculateDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        String value = String.format("%.2f", Radius * c);
        return Double.parseDouble(value);
    }

    public void findClosest(View view) {
        double[] distances = new double[4];
        distances[0] = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[0]);
        distances[1] = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[1]);
        distances[2] = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[2]);
        distances[3] = calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[3]);
        double min = distances[0];
        for (int i=1; i<distances.length; i++) {
            if (distances[i]<min)
                min = distances[i];
        }

        int index = -1;
        for (int i=0; i<distances.length; i++) {
            boolean bol = distances[i] == min;
            Log.d("MYTAG", String.valueOf(distances[i]) + bol);
            if (distances[i]==min)
                index  = i;
        }

        Log.d("MYTAG", "Closest: " + min + " at " + index);

        traceRouteHandler(new LatLng(location.getLatitude(), location.getLongitude()), MARKERS[index]);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        mMap.addMarker(new MarkerOptions().position(MARKERS[2]));
        mMap.addMarker(new MarkerOptions().position(MARKERS[3]));

        ArrayList<LatLng> directionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
        mMap.addPolyline(DirectionConverter.createPolyline(this, directionList, 5, Color.RED));
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this, "Error getting directions", Toast.LENGTH_SHORT);
    }

    private void traceRouteHandler(LatLng pA, LatLng pB) {
        GoogleDirection.withServerKey("AIzaSyDQ0Bf3IALj0ahlVwy-VKJpMxpZMquIqkI")
                .from(pA)
                .to(pB)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }
}
