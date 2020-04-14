package com.example.venture.Fragments.explore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.venture.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ExploreMapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



//    private MapView mMapView;
//    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
//    private GoogleMap mMap;
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//
//    public ExploreMapFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ExploreMapFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ExploreMapFragment newInstance(String param1, String param2) {
//        ExploreMapFragment fragment = new ExploreMapFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_explore_map, container, false);
//        mMapView = view.findViewById(R.id.explore_map);
//        initGoogleMap(savedInstanceState);
//        return view;
//    }
//
//    private void initGoogleMap(Bundle savedInstanceState) {
//        // *** IMPORTANT ***
//        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
//        // objects or sub-Bundles.
//        Bundle mapViewBundle = null;
//        if (savedInstanceState != null) {
//            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
//        }
//
//        mMapView.onCreate(mapViewBundle);
//
//        mMapView.getMapAsync(this);
//    }
//
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
//        if (mapViewBundle == null) {
//            mapViewBundle = new Bundle();
//            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
//        }
//
//        mMapView.onSaveInstanceState(mapViewBundle);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mMapView.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mMapView.onStop();
//    }
//
//    @Override
//    public void onMapReady(GoogleMap map) {
//        mMap = map;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
//
//    @Override
//    public void onPause() {
//        mMapView.onPause();
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        mMapView.onDestroy();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }


}
