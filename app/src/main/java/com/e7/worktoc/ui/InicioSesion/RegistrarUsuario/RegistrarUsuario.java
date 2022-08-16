package com.e7.worktoc.ui.InicioSesion.RegistrarUsuario;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.e7.worktoc.R;

public class RegistrarUsuario extends AppCompatActivity {

    private Button registrarseBtn;
    private ImageView imagenASubir;
    private EditText nombreTxt,correoTxt,claveTxt,apeMaternoTxt,apePaternoTxt,numeroTxt;

    Bitmap bitmap;
    int PICK_IMAGE_REQUEST= 1;
    private String UPLOAD_URL="http://35.174.185.92/servicios/subir_antecedentes.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
    }
}
