package com.mapmyindia.sdk.demo.java.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mapmyindia.sdk.demo.R;
import com.mapmyindia.sdk.demo.databinding.BaseLayoutBinding;
import com.mapmyindia.sdk.maps.MapmyIndiaMap;
import com.mapmyindia.sdk.maps.OnMapReadyCallback;
import com.mapmyindia.sdk.maps.annotations.Marker;
import com.mapmyindia.sdk.maps.annotations.MarkerOptions;
import com.mapmyindia.sdk.maps.camera.CameraUpdateFactory;
import com.mapmyindia.sdk.maps.geometry.LatLng;
import com.mapmyindia.sdk.maps.geometry.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saksham on 2/12/19.
 */
public class AddCustomInfoWindowActivity extends AppCompatActivity implements OnMapReadyCallback {

    private List<LatLng> latLngList = new ArrayList<>();
    private BaseLayoutBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.base_layout);
        mBinding.mapView.onCreate(savedInstanceState);
        mBinding.mapView.getMapAsync(this);

        latLngList.add(new LatLng(25.321684, 82.987289));
        latLngList.add(new LatLng(25.331684, 82.997289));
        latLngList.add(new LatLng(25.321684, 82.887289));
        latLngList.add(new LatLng(25.311684, 82.987289));
    }

    @Override
    public void onMapReady(MapmyIndiaMap mapmyIndiaMap) {



        for (LatLng latLng : latLngList) {
            mapmyIndiaMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("XYZ"));
        }

        mapmyIndiaMap.setInfoWindowAdapter(new MapmyIndiaMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                View view = LayoutInflater.from(AddCustomInfoWindowActivity.this).inflate(R.layout.custom_info_window_layout, null);
                TextView textView = view.findViewById(R.id.text);
                textView.setText("MapmyIndia Head Office, 237, Okhla ");
                return view;
            }
        });

        mapmyIndiaMap.setOnMarkerClickListener(new MapmyIndiaMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                Toast.makeText(AddCustomInfoWindowActivity.this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .includes(latLngList)
                .build();

        mapmyIndiaMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100, 10, 100, 10));
    }

    @Override
    public void onMapError(int i, String s) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBinding.mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBinding.mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mBinding.mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBinding.mapView.onSaveInstanceState(outState);
    }
}
