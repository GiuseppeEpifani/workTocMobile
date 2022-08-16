package com.e7.worktoc.ui.Cliente.EditarSolicitud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditarSolicitudActivity extends AppCompatActivity {

    public Double latitud = 0.0;
    public Double longitud = 0.0;
    private TextView descripcionTxt, precioTxt;
    private EditText horaTxt;
    private Spinner Spinnercategorias;
    private Button modificarBtn;
    private String categoria, descripcion, hora, direccion = "";
    private int precio, id_solicitud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_solicitud_trabajo);

        getSupportActionBar().hide();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recibirDatosDelFragmentEditar();

        Spinnercategorias = (Spinner) findViewById(R.id.SpinnercategoriaEdit);
        precioTxt = findViewById(R.id.txtPrecioEdit);
        descripcionTxt = (TextView) findViewById(R.id.txtDescripcionEdit);
        horaTxt = (EditText) findViewById(R.id.txtHoraEdit);
        modificarBtn = (Button) findViewById(R.id.btnModificarEdit);


        evitarClickEnHoraTxt();
        horaTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogHora(horaTxt);
            }
        });


        modificarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarQueEstenLLenosLosCamposSolicitud()) {
                    procesarEditarSolicitudTrabajo();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cargarSpinnerCategorias();

        setCamposSolicitud();

        iniciarLocalizacionDelMapaEditarSolicitud();
    }

    private void recibirDatosDelFragmentEditar() {
        categoria = getIntent().getExtras().getString("categoria");
        descripcion = getIntent().getExtras().getString("descripcion");
        hora = getIntent().getExtras().getString("hora");
        precio = getIntent().getExtras().getInt("precio");
        latitud = getIntent().getExtras().getDouble("latitud");
        longitud = getIntent().getExtras().getDouble("longitud");
        id_solicitud = getIntent().getExtras().getInt("id_solicitud");
        direccion = getIntent().getExtras().getString("direccion");
    }

    private void setCamposSolicitud() {
        precioTxt.setText(String.valueOf(precio));
        descripcionTxt.setText(descripcion);
        horaTxt.setText(hora);
    }

    private void evitarClickEnHoraTxt() {
        horaTxt.setInputType(InputType.TYPE_NULL);
        horaTxt.setFocusable(false);
    }

    private void mostrarDialogHora(final EditText hora) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                hora.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private boolean validarQueEstenLLenosLosCamposSolicitud() {
        return precioTxt.getText().toString() != "" && precioTxt.getText().length() > 0 && descripcionTxt.getText().toString() != "" && descripcionTxt.getText().length() > 0 && !Spinnercategorias.getSelectedItem().equals("Seleccioné Categoria")
                && horaTxt.getText().toString() != "" && horaTxt.getText().length() > 0;
    }

    private void procesarEditarSolicitudTrabajo() {
        bloquearBtnModificar();
        obtenerDireccionMedianteLasCordenadas();
        actualizarSolicitudTrabajo("http://35.174.185.92/servicios/actualizar_solicitud_trabajo.php");
    }

    private void bloquearBtnModificar() {
        modificarBtn.setSelectAllOnFocus(false);
    }

    private void desbloquearBtnModificar() {
        modificarBtn.setSelectAllOnFocus(true);
    }

    private void obtenerDireccionMedianteLasCordenadas() {
        try {
            //inicializamos el geoCoder
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            //inicializamos el addres list
            List<Address> addresses = null;

            addresses = geocoder.getFromLocation(
                    latitud, longitud, 1
            );

            direccion = addresses.get(0).getAddressLine(0);

            String direc = direccion;
            String[] arrayColores = direc.split(",");

            for (int i = 0; i < 1; i++) {
                direccion = arrayColores[i];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void actualizarSolicitudTrabajo(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Solicitud Actualizada", Toast.LENGTH_SHORT).show();
                enviarBundleMainactivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                desbloquearBtnModificar();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id", String.valueOf(id_solicitud));
                parametros.put("latitud", latitud.toString());
                parametros.put("longitud", longitud.toString());
                parametros.put("precio", precioTxt.getText().toString().trim());
                parametros.put("categoria", Spinnercategorias.getSelectedItem().toString().trim());
                parametros.put("descripcion", descripcionTxt.getText().toString().trim());
                parametros.put("hora", horaTxt.getText().toString());
                parametros.put("direccion", direccion);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void enviarBundleMainactivity() {
        Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("publicado", true);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    private void cargarSpinnerCategorias() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.arrayCategoria, R.layout.spinner_categoria_estilo);
        Spinnercategorias.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(categoria);
        Spinnercategorias.setSelection(spinnerPosition);
    }


    public void iniciarLocalizacionDelMapaEditarSolicitud() {
        ObtenerPermisosUbicacion();
        verificarSiEstaGpsEncendido();
        mostrarFragmentEditarSolicitud();
    }

    private void ObtenerPermisosUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
    }

    private void verificarSiEstaGpsEncendido() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void mostrarFragmentEditarSolicitud() {
        FragmentMapsClienteEditar fragmentEditClient = new FragmentMapsClienteEditar();
        FragmentManager fragmentManagerE = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionE = fragmentManagerE.beginTransaction();
        fragmentTransactionE.add(R.id.fragmentMapaClienteEditar, fragmentEditClient, null);
        fragmentTransactionE.commit();
    }

    public void setCordenada(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

}
