package com.e7.worktoc.ui.Cliente.PublicarSolicitud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.e7.worktoc.R;

public class InicioCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_cliente_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, InicioClienteFragment.newInstance())
                    .commitNow();
        }
    }
}
