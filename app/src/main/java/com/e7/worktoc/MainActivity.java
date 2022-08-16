package com.e7.worktoc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e7.worktoc.ui.BibliotecaClases.BorderedCircleTransform;
import com.e7.worktoc.ui.BibliotecaClases.SolicitudTrabajo;
import com.e7.worktoc.ui.Cliente.PublicarSolicitud.FragmentMapsCliente;
import com.e7.worktoc.ui.Trabajador.BuscarTrabajo.FragmentMapsTrabajador;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private AppBarConfiguration mAppBarConfiguration;
    private NavGraph navGraph;
    private NavController navController;


    public Double latitud = 0.0;
    public Double longitud = 0.0;
    public int ID_CLIENTE = 1;
    public int ID_TRABAJADOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cargarVariablesDeSesion();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        personalizarStarDestinationDelNavigationDrawer();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_cliente, R.id.nav_editar_solicitud, R.id.nav_buscar_trabajador, R.id.nav_trabajador
                , R.id.nav_solicitar_cuenta)
                .setDrawerLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        if (redireccionarAMisSolicitudes()) ;
        else {
            redireccionarInicioClienteFragment();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void cargarVariablesDeSesion() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        String nombreCompleto;
        String fotoUsuario;

        nombreCompleto = preferences.getString("nombreCompleto", "No existe");
        ID_TRABAJADOR = preferences.getInt("idTrabajador", 0);
        ID_CLIENTE = preferences.getInt("idCliente", 0);
        fotoUsuario = preferences.getString("fotoUsuario", "No existe");

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nombre = headerView.findViewById(R.id.txtnombreHeader);
        final ImageView fotoPerfil = headerView.findViewById(R.id.perfil);

        Picasso.get()
                .load("http://35.174.185.92/servicios/uploads/" + fotoUsuario)
                .resize(200, 200).transform(new BorderedCircleTransform(3, Color.WHITE))
                .into(fotoPerfil);
        nombre.setText(nombreCompleto);
    }

    private void ocultarItemDelMenu() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_solicitar_cuenta).setVisible(false);
    }


    //seccion Cliente

    private void personalizarStarDestinationDelNavigationDrawer() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
    }

    private boolean redireccionarAMisSolicitudes() {
        //si viene un bundle del fragmentEditarSolicitud, se redireccionara al mismo fragment, para recargar el recyclerView
        Bundle extra = this.getIntent().getExtras();
        if (extra != null) {
            boolean publicado = getIntent().getExtras().getBoolean("publicado");

            if (publicado) {
                navGraph.setStartDestination(R.id.nav_editar_solicitud);
                navController.setGraph(navGraph);
            }
            return true;
        }
        return false;
    }

    private void redireccionarInicioClienteFragment() {
        navGraph.setStartDestination(R.id.nav_cliente);
        navController.setGraph(navGraph);
    }


    public void iniciarLocalizacionCliente() {
        ObtenerPermisosUbicacion();
        verificarSiEstaGpsEncendido();
        obtenerUbicacionActualYCargarFragmentsMaps(2);
    }


    public void ObtenerPermisosUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    iniciarLocalizacionCliente();
                } catch (Exception e) {

                }
                try {
                    iniciarLocalizacionTrabajador();
                } catch (Exception e) {

                }
                return;
            }
        }
    }

    public void verificarSiEstaGpsEncendido() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }


    private void obtenerUbicacionActualYCargarFragmentsMaps(final int id) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitud = location.getLatitude();
                            longitud = location.getLongitude();
                            Toast.makeText(getApplicationContext(), latitud + "," + longitud, Toast.LENGTH_SHORT).show();
                            if (id == 1) {
                                mostrarFragmentTrabajador();
                            } else {
                                mostrarFragmentCliente();
                            }
                        }
                    }
                });
    }

    private void mostrarFragmentCliente() {
        FragmentMapsCliente fragmentMapsCliente = new FragmentMapsCliente();
        FragmentManager fragmentManagerC = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionC = fragmentManagerC.beginTransaction();
        fragmentTransactionC.add(R.id.fragmentMapaCliente, fragmentMapsCliente, null);
        fragmentTransactionC.commit();
    }

    public void setCordenada(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }


    // seccion Trabajador
    private void mostrarFragmentTrabajador() {
        FragmentMapsTrabajador fragmentMapsTrabajador = new FragmentMapsTrabajador();
        FragmentManager fragmentManagerE = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionE = fragmentManagerE.beginTransaction();
        fragmentTransactionE.add(R.id.iniciotrabajador, fragmentMapsTrabajador, null);
        fragmentTransactionE.commit();
    }


    public ArrayList<SolicitudTrabajo> UbicacionesDeLasSolicitudesDeTrabajos = new ArrayList<SolicitudTrabajo>();

    public void buscarUbicacionesDeTodosLosTrabajosPublicados(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        //intanciamos la clase cordenadas para agregarle sus atributos
                        SolicitudTrabajo UibcaionDeLaSolicitud = new SolicitudTrabajo();
                        UibcaionDeLaSolicitud.setLatitud(jsonObject.getDouble("latitud"));
                        UibcaionDeLaSolicitud.setLongitud(jsonObject.getDouble("longitud"));

                        //lo pasamos a nuestra arrayList para obtenerlo dede el FragmentsMapsTrabajador para que sean visualizados en el mapa
                        UbicacionesDeLasSolicitudesDeTrabajos.add(UibcaionDeLaSolicitud);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                    }
                }
                iniciarLocalizacionTrabajador();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No hay trabajos publicados, cerca de tu ubicación.", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private SolicitudTrabajo solicitudTrabajo = new SolicitudTrabajo();

    public void bucarInformacionEspecificaDeTrabajoPorSuUbicacion(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                    try {
                        jsonObject = response.getJSONObject(0);

                        solicitudTrabajo.setCategoria(jsonObject.getString("categoria"));
                        solicitudTrabajo.setDescripcion(jsonObject.getString("descripcion"));
                        solicitudTrabajo.setHora(jsonObject.getString("hora"));
                        solicitudTrabajo.setPrecio(jsonObject.getInt("precio"));
                        solicitudTrabajo.setFoto_cliente(jsonObject.getString("foto"));
                        solicitudTrabajo.setLongitud(jsonObject.getDouble("longitud"));
                        solicitudTrabajo.setLatitud(jsonObject.getDouble("latitud"));


                        ConstruirAlertDialogYMostrarInformacionDelTrabajo();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void ConstruirAlertDialogYMostrarInformacionDelTrabajo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.informacion_trabajo, null);
        TextView precioTxt = (TextView) view.findViewById(R.id.txtPrecio);
        TextView categoriaTxt = (TextView) view.findViewById(R.id.txtTitulo);
        TextView descripcionTxt = (TextView) view.findViewById(R.id.txtDescripcion);
        TextView horaTxt = (TextView) view.findViewById(R.id.txtHora);
        ImageView imagenCliente = (ImageView) view.findViewById(R.id.imagen_cliente);
        Button aceptarBtn = (Button) view.findViewById(R.id.btnAceptar);
        Button cancelarBtn = view.findViewById(R.id.btnCerrarperfil);
        String fotoCliente = solicitudTrabajo.getFoto_cliente();

        Picasso.get()
                .load("http://35.174.185.92/servicios/uploads/" + fotoCliente)
                .resize(200, 200).transform(new BorderedCircleTransform(1, Color.BLACK))
                .into(imagenCliente);
        categoriaTxt.setText(solicitudTrabajo.getCategoria());
        descripcionTxt.setText(solicitudTrabajo.getDescripcion());
        horaTxt.setText(solicitudTrabajo.getHora());
        String precio = String.valueOf(solicitudTrabajo.getPrecio());
        precioTxt.setText(precio);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        aceptarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aceptarSolicitudTrabajo("http://35.174.185.92/servicios/aceptar_solicitud_trabajo.php?longitud=" + solicitudTrabajo.getLongitud() + "& latitud=" + solicitudTrabajo.getLatitud());
                dialog.dismiss();
               Toast.makeText(getApplicationContext(), "En Curso, No olvides llegar a las " + solicitudTrabajo.getHora(), Toast.LENGTH_SHORT).show();

            }
        });
        cancelarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }




    public void iniciarLocalizacionTrabajador() {
        ObtenerPermisosUbicacion();
        verificarSiEstaGpsEncendido();
        obtenerUbicacionActualYCargarFragmentsMaps(1);
    }

    public void getCordenadasDelFragmentEditarSolicitud(Double latitud, Double longitud)
    {
        this.latitud=latitud;
        this.longitud=longitud;
    }


    private void aceptarSolicitudTrabajo(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Algo fallo intentelo nuevamente", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }







}
