package com.e7.worktoc.ui.Cliente.EditarSolicitud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.e7.worktoc.R;

public class EditarSolicitudTrabajo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_solicitud_trabajo_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, EditarSolicitudTrabajoFragment.newInstance())
                    .commitNow();
        }
    }
}
