package com.e7.worktoc.ui.Cliente.PublicarSolicitud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;

public class SplashProcesarSolicitudTrabajo extends AppCompatActivity {
    private TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_procesar_solicitud_trabajo);

        getSupportActionBar().hide();
        texto = (TextView) findViewById(R.id.texto);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    texto.setText("Solicitud Publicada!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                enviarBundleMainactivityClient();
            }
        }, 1000);
    }

    private void enviarBundleMainactivityClient() {
        Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("publicado", true);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
        finish();
    }
}
