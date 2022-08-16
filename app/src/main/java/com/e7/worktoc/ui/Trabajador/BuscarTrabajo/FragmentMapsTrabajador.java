package com.e7.worktoc.ui.Trabajador.BuscarTrabajo;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.SolicitudTrabajo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;

public class FragmentMapsTrabajador extends SupportMapFragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private double latitud, longitud;

    public FragmentMapsTrabajador() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = super.onCreateView(inflater, container, bundle);


        this.latitud = ((MainActivity) getActivity()).latitud;
        this.longitud = ((MainActivity) getActivity()).longitud;


        getMapAsync(this);

        return rootView;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        latitud = marker.getPosition().latitude;
        longitud = marker.getPosition().longitude;
        ((MainActivity) getActivity()).bucarInformacionEspecificaDeTrabajoPorSuUbicacion("http://35.174.185.92/servicios/buscar_informacion_especifica_trabajo.php?longitud=" + longitud + "& latitud=" + latitud);
        enviarUbicacionAlMainActivity();
        return false;
    }

    private void enviarUbicacionAlMainActivity() {
        ((MainActivity) getActivity()).setCordenada(latitud, longitud);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        formatearMapaDeGoogle(googleMap);

        googleMap.clear();

        float zoom = 17;
        LatLng UbicacionActual = new LatLng(latitud, longitud);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UbicacionActual, zoom));
        googleMap.setMyLocationEnabled(true);

        UiSettings settings = googleMap.getUiSettings();
        configuaracionesDelMapa(settings);

        añadirUbicacionDeLosTrabajosExistentesEnElMapa(googleMap);

        googleMap.setOnMarkerClickListener(this);
    }

    private void formatearMapaDeGoogle(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.formato_mapa));
    }

    private void configuaracionesDelMapa(UiSettings settings) {
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setZoomControlsEnabled(true);
    }

    private void añadirUbicacionDeLosTrabajosExistentesEnElMapa(GoogleMap googleMap) {
        Iterator<SolicitudTrabajo> ubicacion = ((MainActivity) getActivity()).UbicacionesDeLasSolicitudesDeTrabajos.iterator();
        while (ubicacion.hasNext()) {
            SolicitudTrabajo cor = ubicacion.next();
            LatLng latLng = new LatLng(cor.getLatitud(), cor.getLongitud());
            googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.trabajo)));
        }
    }
}
