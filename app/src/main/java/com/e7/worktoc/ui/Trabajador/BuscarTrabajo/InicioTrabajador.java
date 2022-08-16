package com.e7.worktoc.ui.Trabajador.BuscarTrabajo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.e7.worktoc.R;

public class InicioTrabajador extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_trabajador_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, InicioTrabajadorFragment.newInstance())
                    .commitNow();
        }
    }
}
