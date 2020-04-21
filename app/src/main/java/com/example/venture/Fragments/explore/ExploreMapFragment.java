package com.example.venture.Fragments.explore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.venture.MainActivity;
import com.example.venture.R;
import com.example.venture.models.Event;
import com.example.venture.viewmodels.explore.ExploreMapFragmentViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.List;


public class ExploreMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "ExploreMapFragment";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 10f;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final String CURRENT_LOCATION = "My Location";
    private double curLatitude = 0.0;
    private double curLongitude = 0.0;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;

    private ExploreMapFragmentViewModel exploreMapFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_map, container, false);
        getLocationPermission();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        toastNotification(getResources().getString(R.string.geolocateSuccess));
        getDeviceLocation();
        initData();
        mMap.setOnInfoWindowClickListener(this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                requestPermissions( permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called with request code " + requestCode);
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            toastNotification(getResources().getString(R.string.permissionReq));
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: currentLocation: " +currentLocation);
                            curLatitude = currentLocation.getLatitude();
                            curLongitude = currentLocation.getLongitude();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                            addEventMarker("", new LatLng(curLatitude, curLongitude), CURRENT_LOCATION, "");
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            toastNotification(getResources().getString(R.string.geolocateError));
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    public void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void initData() {
        Log.d(TAG, "initData: initializing map data");
        exploreMapFragmentViewModel = ExploreMapFragmentViewModel.getInstance();
        exploreMapFragmentViewModel.init();
        exploreMapFragmentViewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                Log.d(TAG, "onChanged: " + events.toString());
                mMap.clear();
                setMarkers(events);
            }
        });
    }

    public void setMarkers(List<Event> events){
        // add current location marker
        if(curLatitude!=0.0 && curLongitude!=0.0)
            addEventMarker("", new LatLng(curLatitude, curLongitude), CURRENT_LOCATION, "");
        // add events markers
        for(Event e: events) {
            Log.d(TAG, "setMarkers: "+ e.getId() + ", " + e.getTitle() +", "+ e.getLocation() +", "+ e.getLatitude()+", "+e.getLongitude());
            addEventMarker(e.getId(), new LatLng(e.getLatitude(), e.getLongitude()), e.getTitle(), e.getLocation());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void addEventMarker(String id, LatLng latLng, String title, String location) {
        Log.d(TAG, "addMarker: " + title);
        if(title.equals(CURRENT_LOCATION)) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(title);
            mMap.addMarker(options);
        }
        else {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(title)
                    .snippet(location);
            Marker marker = mMap.addMarker(options);
            marker.setTag(id);
        }
    }

    public void toastNotification(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTag()!=null) {
            Log.d(TAG, "onInfoWindowClick: open event with id: " + marker.getTag());
            ((MainActivity)getActivity()).openEventFragment(marker.getTag().toString(), "EXPLORE");
//            ((MainActivity)getActivity()).changeAppBar();
//            ((MainActivity)getActivity()).hideBottomNavigationBar();
        }
    }
}
