package com.e7.worktoc.ui.Cliente.PublicarSolicitud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;


public class FragmentMapsCliente extends SupportMapFragment implements OnMapReadyCallback {

    private double latitud, longitud;

    public FragmentMapsCliente() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        this.latitud = ((MainActivity) getActivity()).latitud;
        this.longitud = ((MainActivity) getActivity()).longitud;

        getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        formatearMapaDeGoogle(googleMap);

        float zoom = 17;
        final LatLng latLng = new LatLng(latitud, longitud);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        googleMap.setMyLocationEnabled(true);

        UiSettings settings = googleMap.getUiSettings();
        configuaracionesDelMapa(settings);

        capturarCordenadasDelMapa(googleMap);
    }

    private void formatearMapaDeGoogle(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.formato_mapa));
    }

    private void configuaracionesDelMapa(UiSettings settings)
    {
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setZoomControlsEnabled(true);
    }

    private void capturarCordenadasDelMapa(final GoogleMap googleMap) {
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (googleMap != null) {
                    googleMap.clear();
                }
                latitud = googleMap.getCameraPosition().target.latitude;
                longitud = googleMap.getCameraPosition().target.longitude;
                //Toast.makeText(getContext(), latitud+","+longitud, Toast.LENGTH_SHORT).show();

                enviarCordenadasAlMainActivity();

            }
        });
    }

    private void enviarCordenadasAlMainActivity() {
        ((MainActivity) getActivity()).setCordenada(latitud, longitud);
    }


}
