package com.example.lab_pennapar_c0874203_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lab_pennapar_c0874203_android.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // GG map & location with location manager and listener
    private static final int REQUEST_CODE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //local logic
    private Boolean isFirstTime = true;
    Marker homeLocation;
    Marker favriteLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Search Part
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_api_key));
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("0", "Place: " + place.getName() + ", " + place.getId());
                try {
                    setMarker(place.getLatLng());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("0", "An error occurred: " + status);
            }
        });

        // adding on click listener for our hybrid map button.
        binding.idBtnHybridMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change
                // the type of map to hybrid.
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });

        // adding on click listener for our terrain map button.
        binding.idBtnTerrainMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change
                // the type of terrain map.
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        });

        // adding on click listener for our satellite map button.
        binding.idBtnSatelliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change the
                // type of satellite map.
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        //Show distance on click
        binding.idBtnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favriteLocation != null) {
                    drawLine();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Toast.makeText(MapsActivity.this, "Please wait... getting your location", Toast.LENGTH_LONG).show();

        //Set current location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isFirstTime) {
                    isFirstTime = false;
                    setHomeMarker(location);
                }
            }
        };

        //Request Permission
        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();

        //Set up for GG map
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // apply long press gesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                try {
                    setMarker(latLng);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Polyline onclick
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                showDistance();
            }
        });

        //On marker drag
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {}

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                try {
                    setMarker(marker.getPosition());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {}
        });
    }

    private void setMarker(LatLng latLng) throws IOException {
        mMap.clear();
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getAddress(latLng));
        Marker marker = mMap.addMarker(options);
        assert marker != null;
        marker.showInfoWindow();
        favriteLocation = marker;
        zoomCamera(latLng);
    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeLocation = mMap.addMarker(options);
        zoomCamera(userLocation);
    }

    private void zoomCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private String getAddress(LatLng latLng) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        String address = addresses.get(0).getAddressLine(0);

        if (address.isEmpty() || address == null){
            address = Calendar.getInstance().getTime().toString();
        }

        return address;
    }

    private void drawLine() {
        PolylineOptions options = new PolylineOptions()
                .clickable(true)
                .color(Color.RED)
                .width(10)
                .add(favriteLocation.getPosition(), homeLocation.getPosition());
        mMap.addPolyline(options);
        zoomCamera(favriteLocation.getPosition());
        showDistance();
    }

    private void showDistance() {
        double distance = SphericalUtil.computeDistanceBetween(favriteLocation.getPosition(), homeLocation.getPosition());
        Toast.makeText(this, "Distance between positions is \n " + String.format("%.2f", distance / 1000) + "km", Toast.LENGTH_SHORT).show();
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
}