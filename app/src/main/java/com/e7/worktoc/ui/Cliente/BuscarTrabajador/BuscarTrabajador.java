package com.e7.worktoc.ui.Cliente.BuscarTrabajador;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.e7.worktoc.R;
import com.e7.worktoc.ui.Cliente.BuscarTrabajador.BuscarTrabajadorFragment;

public class BuscarTrabajador extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscar_trabajador_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, BuscarTrabajadorFragment.newInstance())
                    .commitNow();
        }
    }
}
