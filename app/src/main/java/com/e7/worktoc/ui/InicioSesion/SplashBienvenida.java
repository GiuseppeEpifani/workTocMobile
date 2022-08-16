package com.e7.worktoc.ui.InicioSesion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;

public class SplashBienvenida extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_bienvenida);

        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (verificarQueElGpsEsteEncendido()) {
                    enviarAlActivityActivarGPS();
                } else {
                    enviarAlMaiActivity();
                }
            }
        }, 3000);
    }

    private boolean verificarQueElGpsEsteEncendido() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return !gpsEnabled;
    }

    private void enviarAlActivityActivarGPS() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        finish();
    }

    private void enviarAlMaiActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
