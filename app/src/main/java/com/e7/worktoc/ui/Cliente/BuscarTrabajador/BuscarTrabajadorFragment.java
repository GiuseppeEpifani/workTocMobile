package com.e7.worktoc.ui.Cliente.BuscarTrabajador;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.UsuarioTrabajador;
import com.e7.worktoc.ui.Cliente.BuscarTrabajador.PerfilUsuarioTrabajador.PerfilUsuarioTrabajador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuscarTrabajadorFragment extends Fragment {

    private BuscarTrabajadorViewModel mViewModel;

    private RecyclerView recyclerViewUsuariosTrabajo;
    private RecyclerViewAdaptadorBuscarTrabajador adaptadorUsuariosTrabajo;

    private EditText BuscarTrabajadorTxt;
    private Spinner SpinnerCategorias;
    public ArrayList<UsuarioTrabajador> trabajadores;


    public static BuscarTrabajadorFragment newInstance() {
        return new BuscarTrabajadorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView =
                inflater.inflate(R.layout.buscar_trabajador_fragment, container, false);

        recyclerViewUsuariosTrabajo = (RecyclerView) rootView.findViewById(R.id.RecyclerViewUsuariosTrabajadores);
        BuscarTrabajadorTxt = (EditText) rootView.findViewById(R.id.txtBuscadorTrabajadores);
        SpinnerCategorias = (Spinner) rootView.findViewById(R.id.filtrarPorCategorias);

        bucarUsuariosParaCargarlosEnElRecyclerView("http://35.174.185.92/servicios/buscar_usuarios_trabajador.php");

        capturarNombresParaRealizarElFiltroDeTrabajadores();

        cargarSpinnerCategorias(rootView);


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BuscarTrabajadorViewModel.class);
        // TODO: Use the ViewModel
    }

    private void bucarUsuariosParaCargarlosEnElRecyclerView(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                trabajadores = new ArrayList<UsuarioTrabajador>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        UsuarioTrabajador usu = new UsuarioTrabajador(jsonObject.getString("correo"), jsonObject.getString("nombre")
                                , jsonObject.getString("ape_paterno"), jsonObject.getString("foto"), jsonObject.getInt("numero")
                                , jsonObject.getString("especialidad"), (float) jsonObject.getDouble("AVG(COALESCE(calificacion_trabajos.puntaje, 0))")
                                , jsonObject.getString("categoria"), jsonObject.getInt("id"));
                        trabajadores.add(usu);

                        contruirRecyclerView();
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    private void contruirRecyclerView() {
        recyclerViewUsuariosTrabajo.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptadorUsuariosTrabajo = new RecyclerViewAdaptadorBuscarTrabajador(trabajadores);
        recyclerViewUsuariosTrabajo.setAdapter(adaptadorUsuariosTrabajo);

        adaptadorUsuariosTrabajo.setOnClickListener(new RecyclerViewAdaptadorBuscarTrabajador.OnItemClickListener() {


            @Override
            public void onItemClick(int position) {
                enviarDatosTrabajadorAlPerfilDeUsuario(position);
            }

            @Override
            public void onCallClick(int numero) {
                llamarContactoTrabajador(numero);
            }


        });
    }

    private void enviarDatosTrabajadorAlPerfilDeUsuario(int position) {
        Intent mIntent = new Intent(getContext(), PerfilUsuarioTrabajador.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("nombre", trabajadores.get(position).getNombre() + " " + trabajadores.get(position).getApellidoPaterno());
        mBundle.putInt("numero", trabajadores.get(position).getNumero());
        mBundle.putString("correo", trabajadores.get(position).getCorreo());
        mBundle.putString("especialidad", trabajadores.get(position).getEspecialidad());
        mBundle.putString("foto", trabajadores.get(position).getNombreFoto());
        mBundle.putFloat("calificacion", trabajadores.get(position).getCalificacion());
        mBundle.putInt("id_usuario", trabajadores.get(position).getId_usuario_trabajador());
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    private void llamarContactoTrabajador(int numero) {
        Intent activityLlamadas = new Intent(Intent.ACTION_CALL);
        activityLlamadas.setData(Uri.parse("tel:9" + numero));

        if (pedirPermisosDeContactos())
            startActivity(activityLlamadas);
    }

    private boolean pedirPermisosDeContactos() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE,}, 1);
        } else {
            return true;
        }
        return false;
    }


    private void capturarNombresParaRealizarElFiltroDeTrabajadores() {
        BuscarTrabajadorTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filtarTrabajadoresSiConincideConSuNombre(editable.toString());
            }
        });
    }

    private void filtarTrabajadoresSiConincideConSuNombre(String textoCapturado) {
        ArrayList<UsuarioTrabajador> TrabajadoresAfiltrar = new ArrayList<>();
        for (UsuarioTrabajador usuario : trabajadores) {
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellidoPaterno();
            if (nombreCompleto.toLowerCase().contains(textoCapturado.toLowerCase())) {
                TrabajadoresAfiltrar.add(usuario);
            }
        }
        adaptadorUsuariosTrabajo.filtrarTrabajadores(TrabajadoresAfiltrar);
    }

    private void cargarSpinnerCategorias(View rootView) {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext().getApplicationContext(), R.array.arrayFiltrarCategorias, R.layout.spinner_filtrar_categoria_estilo);
        SpinnerCategorias.setAdapter(adapter);
        SpinnerCategorias.setFocusable(true);

        capturarIndiceDelSipinnerCategoriaParaFiltrar(adapter);
    }

    private void capturarIndiceDelSipinnerCategoriaParaFiltrar(
            final ArrayAdapter<CharSequence> adapter) {
        SpinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    filtarPorCcategoria(adapter.getItem(position) + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada fue seleccionado. Por cierto, no he visto que este método se desencadene
            }
        });
    }

    private void filtarPorCcategoria(String textoCategoria) {
        ArrayList<UsuarioTrabajador> categoriaAFiltrar = new ArrayList<>();
        for (UsuarioTrabajador usuario : trabajadores) {
            String categoria = usuario.getCategoria();
            if (categoria.toLowerCase().contains(textoCategoria.toLowerCase())) {
                categoriaAFiltrar.add(usuario);
            }
        }
        adaptadorUsuariosTrabajo.filtrarTrabajadores(categoriaAFiltrar);
    }


}
