package com.e7.worktoc.ui.Cliente.BuscarTrabajador.PerfilUsuarioTrabajador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.BorderedCircleTransform;
import com.e7.worktoc.ui.BibliotecaClases.OpinionUsuario;
import com.e7.worktoc.ui.BibliotecaClases.UsuarioCliente;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PerfilUsuarioTrabajador extends AppCompatActivity {

    private TextView nombreTxt, especialidadTxt, correoTxt, telefonoTxt;
    private ImageButton btnCerrar;
    private ImageView imagenFotoPerfil;
    private RecyclerView recyclerViewOpinones;
    private RecyclerViewAdaptadorOpinionesUsuarios adaptadorOpinionesUsuarios;
    public ArrayList<UsuarioCliente> opiniones;
    private String nombre, correo, fecha, especialidad, foto;
    private int numero;
    private float calificacion;
    private ImageButton LlamarBtn;
    private RatingBar calificacionRatingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario_trabajador);
        getSupportActionBar().hide();

        bucarOpinionesUsuarios("http://35.174.185.92/servicios/buscar_opiniones_usuarios.php?id_trabajador=" + getIntent().getExtras().getInt("id_usuario"));

        recibirDatosDelBunbleBuscarTrabajador();

        nombreTxt = (TextView) findViewById(R.id.NombrePerfilTxt);
        especialidadTxt = (TextView) findViewById(R.id.EspecialidadPerfilTxt);
        correoTxt = (TextView) findViewById(R.id.CorreoPerfilTxt);
        telefonoTxt = (TextView) findViewById(R.id.txtTelefonoPerfil);
        calificacionRatingBar = (RatingBar) findViewById(R.id.ratingBarPerfil);
        btnCerrar = (ImageButton) findViewById(R.id.CerrarPerfilBtn);
        LlamarBtn = (ImageButton) findViewById(R.id.LlamarPerfilBtn);
        imagenFotoPerfil = (ImageView) findViewById(R.id.imagenFotoPerfil);
        recyclerViewOpinones = (RecyclerView) findViewById(R.id.RecyclerViewOpiniones);


        SetDatosRecibidorDelBundleBuscarTrabajador();


        cerrarPerfilUsuario();
        llamarTrabajador();
    }

    private void bucarOpinionesUsuarios(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                opiniones = new ArrayList<UsuarioCliente>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        UsuarioCliente usuarioCliente = new UsuarioCliente();
                        usuarioCliente.setNombre(jsonObject.getString("nombre"));
                        usuarioCliente.setApellidoPaterno(jsonObject.getString("ape_paterno"));
                        OpinionUsuario opinion = new OpinionUsuario(jsonObject.getString("comentario"), jsonObject.getString("fecha"));
                        usuarioCliente.setOpinion(opinion);
                        usuarioCliente.setNombreFoto(jsonObject.getString("foto"));
                        opiniones.add(usuarioCliente);

                        contruirRecyclerView();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void contruirRecyclerView() {
        recyclerViewOpinones.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adaptadorOpinionesUsuarios = new RecyclerViewAdaptadorOpinionesUsuarios(opiniones);
        recyclerViewOpinones.setAdapter(adaptadorOpinionesUsuarios);
    }

    private void recibirDatosDelBunbleBuscarTrabajador() {
        nombre = getIntent().getExtras().getString("nombre");
        correo = getIntent().getExtras().getString("correo");
        fecha = getIntent().getExtras().getString("fecha");
        foto = getIntent().getExtras().getString("foto");
        especialidad = getIntent().getExtras().getString("especialidad");
        numero = getIntent().getExtras().getInt("numero");
        calificacion = getIntent().getExtras().getFloat("calificacion");
    }

    private void SetDatosRecibidorDelBundleBuscarTrabajador() {
        Picasso.get()
                .load("http://35.174.185.92/servicios/uploads/" + foto)
                .resize(200, 200).transform(new BorderedCircleTransform(10, Color.BLACK))
                .into(imagenFotoPerfil);
        nombreTxt.setText(nombre);
        especialidadTxt.setText(especialidad);
        telefonoTxt.setText(String.valueOf(numero));
        correoTxt.setText(correo);
        calificacionRatingBar.setRating(calificacion);
    }

    private void cerrarPerfilUsuario() {
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void llamarTrabajador() {
        LlamarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llamarContactoTrabajador(numero);
            }
        });
    }

    private void llamarContactoTrabajador(int numero) {
        Intent activityLlamadas = new Intent(Intent.ACTION_CALL);
        activityLlamadas.setData(Uri.parse("tel:9" + numero));

        if (pedirPermisosDeContactos())
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        startActivity(activityLlamadas);
    }

    private boolean pedirPermisosDeContactos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE,}, 1);
        } else {
            return true;
        }
        return false;
    }


}
