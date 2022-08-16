package com.e7.worktoc.ui.InicioSesion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.UsuarioCliente;
import com.e7.worktoc.ui.InicioSesion.RegistrarUsuario.RegistrarUsuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InicioSesion extends AppCompatActivity{


    Button iniciarSesionBtn;
    EditText correoTxt;
    EditText claveTxt;
    TextView registrarseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        getSupportActionBar().hide();

        pedirPermisosDeUbicacion();
        pedirPermisosDeContacto();

        iniciarSesionBtn = (Button) findViewById(R.id.accederBtn);
        correoTxt = (EditText) findViewById(R.id.correoTxt);
        claveTxt = (EditText) findViewById(R.id.claveTxt);
        registrarseBtn = (TextView) findViewById(R.id.registrarseBtn);

        clickEnEliniciarSesionBtn();
        clickEnElregistrarseBtn();


        if (cargarVariablesDeSesionParaIniciarLaAplicacionInmediatamente()) {
            pasarAlMainActivity();
        }

    }





    private void pedirPermisosDeUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
    }

    private void pedirPermisosDeContacto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE,}, 1);
        }
    }

    private void clickEnEliniciarSesionBtn() {
        iniciarSesionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCamposInicio()) {
                   consultarUsuarioYGuardarVariablesDeSesion("http://35.174.185.92/servicios/consultar_usuario_cliente.php?correo=" + correoTxt.getText().toString() + "& clave=" + claveTxt.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private boolean validarCamposInicio() {
        return correoTxt.getText().toString() != "" && correoTxt.getText().length() > 0
                && claveTxt.getText().toString() != "" && claveTxt.getText().length() > 0;
    }



    private void consultarUsuarioYGuardarVariablesDeSesion(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                    evitarHacerClickEnEliniciarSesionBtn();
                    //ventanaIniciandoSesion();
                        try {
                            UsuarioCliente usuarioCliente  = new UsuarioCliente();
                            jsonObject = response.getJSONObject(0);
                            usuarioCliente.setNombre(jsonObject.getString("nombre"));
                            usuarioCliente.setApellidoPaterno(jsonObject.getString("ape_paterno"));
                            // idTrabajador=(jsonObject.getInt("id"));
                            usuarioCliente.setIdCliente(jsonObject.getInt("id"));
                            usuarioCliente.setNombreFoto(jsonObject.getString("foto"));

                            guardarVariablesDeSesion(usuarioCliente);

                            pasarAlMainActivity();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                        }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Usuario Inexistente", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    private void evitarHacerClickEnEliniciarSesionBtn() {
        iniciarSesionBtn.setSelectAllOnFocus(false);
    }

    /*

    private ProgressDialog cargandoSesion;

    private void ventanaIniciandoSesion() {
        cargandoSesion = ProgressDialog.show(this, "Iniciando Sesión...", "Espere por favor");
    }



    private void cerrarVentanaInicioSesion() {
        cargandoSesion.dismiss();
    }
*/
    private void guardarVariablesDeSesion( UsuarioCliente usuarioCliente) {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String correo = correoTxt.getText().toString();
        String clave = claveTxt.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("correo", correo);
        editor.putString("clave", clave);
        editor.putString("fotoUsuario", usuarioCliente.getNombreFoto());
        // editor.putInt("id_trabajador", id_trabajador);
        editor.putInt("idCliente", usuarioCliente.getIdCliente());
        editor.putString("nombreCompleto", usuarioCliente.getNombre() + " " + usuarioCliente.getApellidoPaterno());
        editor.commit();
    }

    private void pasarAlMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void clickEnElregistrarseBtn() {
        registrarseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrarUsuario.class);
                startActivity(intent);
            }
        });
    }

    private boolean cargarVariablesDeSesionParaIniciarLaAplicacionInmediatamente() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        String correo = preferences.getString("correo", "No existe");
        String clave = preferences.getString("clave", "No existe");

        return !correo.equals("No existe") && !clave.equals("No existe");
    }



}
