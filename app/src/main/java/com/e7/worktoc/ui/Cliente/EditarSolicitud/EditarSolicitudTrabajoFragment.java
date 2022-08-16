package com.e7.worktoc.ui.Cliente.EditarSolicitud;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.SolicitudTrabajo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditarSolicitudTrabajoFragment extends Fragment {

    private EditarSolicitudTrabajoViewModel mViewModel;
    private RecyclerView recyclerViewSolicitud;
    private RecyclerViewAdaptadorEditarSolicitud recyclerViewAdaptadorEditarSolicitud;
    public ArrayList<SolicitudTrabajo> solicitudes;

    private Double latitud = 0.0;
    private Double longitud = 0.0;

    public static EditarSolicitudTrabajoFragment newInstance() {
        return new EditarSolicitudTrabajoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.editar_solicitud_trabajo_fragment, container, false);

        recyclerViewSolicitud = (RecyclerView) rootView.findViewById(R.id.recyclerSolicitud);
        bucarTodasMisSolicitudesDeTrabajoYArmarRecyclerView("http://35.174.185.92/servicios/buscar_solicitud.php");




        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EditarSolicitudTrabajoViewModel.class);
        // TODO: Use the ViewModel
    }

    private void bucarTodasMisSolicitudesDeTrabajoYArmarRecyclerView(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                solicitudes = new ArrayList<SolicitudTrabajo>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        SolicitudTrabajo solicitud = new SolicitudTrabajo();
                        solicitud.setId_solicitud(jsonObject.getInt("id"));
                        solicitud.setLatitud(jsonObject.getDouble("latitud"));
                        solicitud.setLongitud(jsonObject.getDouble("longitud"));
                        solicitud.setCategoria(jsonObject.getString("categoria"));
                        solicitud.setDescripcion(jsonObject.getString("descripcion"));
                        solicitud.setHora(jsonObject.getString("hora"));
                        solicitud.setPrecio(jsonObject.getInt("precio"));
                        solicitud.setFecha_hora(jsonObject.getString("fecha"));
                        solicitud.setDireccion(jsonObject.getString("direccion"));

                        solicitudes.add(solicitud);

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                    }
                }
                contruirRecyclerView();

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
        recyclerViewSolicitud.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAdaptadorEditarSolicitud = new RecyclerViewAdaptadorEditarSolicitud(solicitudes);
        recyclerViewSolicitud.setAdapter(recyclerViewAdaptadorEditarSolicitud);
        recyclerViewAdaptadorEditarSolicitud.setOnClickListener(new RecyclerViewAdaptadorEditarSolicitud.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position, int id_solicitud) {
                mostrarAlerDialogSiNoPreEliminacionDeSolicitud(position, id_solicitud);
            }

            @Override
            public void onEditClick(int position) {
                SolicitudTrabajo solicitudTrabajoConLosDatosRescatados = rescatarDatosDeLaSolicitudSeleccionada(position);
                mostrarVentanaDeEdicionDeSolicitudDeTrabajo(solicitudTrabajoConLosDatosRescatados);

            }
        });
    }

    private void mostrarAlerDialogSiNoPreEliminacionDeSolicitud(final int position, final int id_solicitud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alertdialog_eliminar_solicitud, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnSi = (Button) view.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // eliminarItemDeLaLista(position);
                recogerIdSolicitudParaSerEliminada(id_solicitud);
                eliminarItemDeLaLista(position);
                dialog.dismiss();

            }
        });

        Button btnNo = view.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void eliminarItemDeLaLista(int position) {
        solicitudes.remove(position);
        recyclerViewAdaptadorEditarSolicitud.notifyDataSetChanged();
    }

    private void recogerIdSolicitudParaSerEliminada(int id_solicitud) {
        eliminarSolicitud("http://35.174.185.92/servicios/eliminar_solicitud.php", id_solicitud);
        Toast.makeText(getContext(), "Solicitud Eliminada", Toast.LENGTH_LONG).show();

    }

    private void eliminarSolicitud(String URL, final int id_solicitud) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id", String.valueOf(id_solicitud));

                return parametros;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });
    }

    private SolicitudTrabajo rescatarDatosDeLaSolicitudSeleccionada(int position) {
        SolicitudTrabajo solicitudTrabajo = new SolicitudTrabajo();
        solicitudTrabajo.setId_solicitud(solicitudes.get(position).getId_solicitud());
        solicitudTrabajo.setLatitud(solicitudes.get(position).getLatitud());
        solicitudTrabajo.setLongitud(solicitudes.get(position).getLongitud());
        solicitudTrabajo.setDescripcion(solicitudes.get(position).getDescripcion());
        solicitudTrabajo.setCategoria(solicitudes.get(position).getCategoria());
        solicitudTrabajo.setHora(solicitudes.get(position).getHora());
        solicitudTrabajo.setPrecio(solicitudes.get(position).getPrecio());
        solicitudTrabajo.setDireccion(solicitudes.get(position).getDireccion());

        return solicitudTrabajo;
    }


    SolicitudTrabajo solicitudTrabajoAAcutalizar;

    private void mostrarVentanaDeEdicionDeSolicitudDeTrabajo(final SolicitudTrabajo solicitudTrabajo) {
        ((MainActivity) getActivity()).ObtenerPermisosUbicacion();
        ((MainActivity) getActivity()).verificarSiEstaGpsEncendido();

        setCordenadasEnElMainActivity(solicitudTrabajo);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.editar_solicitud_trabajo, null);
        final TextView precioTxt = (TextView) view.findViewById(R.id.txtPrecioEdit);
        final Spinner spinnerCategoria = (Spinner) view.findViewById(R.id.SpinnercategoriaEdit);
        final TextView descripcionTxt = (TextView) view.findViewById(R.id.txtDescripcionEdit);
        final EditText horaTxt = (EditText) view.findViewById(R.id.txtHoraEdit);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        final Button modificarBtn = view.findViewById(R.id.btnModificarEdit);


        ArrayAdapter<CharSequence> categorias = cargarSpinnerCategorias(spinnerCategoria);
        int posicionDeLaCategoria = categorias.getPosition(solicitudTrabajo.getCategoria());
        spinnerCategoria.setSelection(posicionDeLaCategoria);
        precioTxt.setText(String.valueOf(solicitudTrabajo.getPrecio()));
        descripcionTxt.setText(solicitudTrabajo.getDescripcion());
        horaTxt.setText(solicitudTrabajo.getHora());


        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();


        evitarClickEnHoraTxt(horaTxt);
        horaTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogHora(horaTxt);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                recargarFragmentParaEvitarDuplicidadDelMapa();
            }
        });
        modificarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (precioTxt.getText().toString() != "" && precioTxt.getText().length() > 0 && descripcionTxt.getText().toString() != ""
                        && descripcionTxt.getText().length() > 0 && !spinnerCategoria.getSelectedItem().equals("Seleccioné Categoria")
                        && horaTxt.getText().toString() != "" && horaTxt.getText().length() > 0) {

                    bloquearBtnModificar(modificarBtn);

                    getCordenadasUbicacion();

                    solicitudTrabajoAAcutalizar = new SolicitudTrabajo();
                    obtenerDireccionMedianteLasCordenadas(solicitudTrabajoAAcutalizar);
                    solicitudTrabajoAAcutalizar.setCategoria(spinnerCategoria.getSelectedItem().toString().trim());
                    solicitudTrabajoAAcutalizar.setPrecio(Integer.parseInt(precioTxt.getText().toString().trim()));
                    solicitudTrabajoAAcutalizar.setHora(horaTxt.getText().toString());
                    solicitudTrabajoAAcutalizar.setDescripcion(descripcionTxt.getText().toString().trim());
                    solicitudTrabajoAAcutalizar.setId_solicitud(solicitudTrabajo.getId_solicitud());

                    actualizarSolicitudTrabajo("http://35.174.185.92/servicios/actualizar_solicitud_trabajo.php");


                    dialog.dismiss();
                    recargarFragmentParaEvitarDuplicidadDelMapa();
                } else {
                    Toast.makeText(getActivity(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private ArrayAdapter<CharSequence> cargarSpinnerCategorias(Spinner spinnerCategorias) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.arrayCategoria, R.layout.spinner_categoria_estilo);
        spinnerCategorias.setAdapter(adapter);
        return adapter;
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

        new TimePickerDialog(getActivity(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void evitarClickEnHoraTxt(EditText horaTxt) {
        horaTxt.setInputType(InputType.TYPE_NULL);
        horaTxt.setFocusable(false);
    }

    private void recargarFragmentParaEvitarDuplicidadDelMapa() {
        solicitudes.removeAll(solicitudes);
        recyclerViewAdaptadorEditarSolicitud.notifyDataSetChanged();
        EditarSolicitudTrabajoFragment fragmentARecargar = new EditarSolicitudTrabajoFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.editarsolicitudtrabajo, fragmentARecargar).addToBackStack(null).commit();
    }


    private void bloquearBtnModificar(Button modificarBtn) {
        modificarBtn.setSelectAllOnFocus(false);
    }

    private void desbloquearBtnModificar(Button modificarBtn) {
        modificarBtn.setSelectAllOnFocus(true);
    }

    private void obtenerDireccionMedianteLasCordenadas(SolicitudTrabajo solicitudTrabajoAAcutalizar) {
        try {
            //inicializamos el geoCoder
            Geocoder geocoder = new Geocoder(getActivity(),
                    Locale.getDefault());
            //inicializamos el addres list
            List<Address> addresses = null;

            addresses = geocoder.getFromLocation(
                    latitud, longitud, 1
            );

            String direccion = addresses.get(0).getAddressLine(0);

            String direc = direccion;
            String[] arrayColores = direc.split(",");

            for (int i = 0; i < 1; i++) {
                solicitudTrabajoAAcutalizar.setDireccion(arrayColores[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void actualizarSolicitudTrabajo(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), "Solicitud Actualizada", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error" + error, Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id", String.valueOf(solicitudTrabajoAAcutalizar.getId_solicitud()));
                parametros.put("latitud", latitud.toString());
                parametros.put("longitud", longitud.toString());
                parametros.put("precio", String.valueOf(solicitudTrabajoAAcutalizar.getPrecio()));
                parametros.put("categoria", solicitudTrabajoAAcutalizar.getCategoria());
                parametros.put("descripcion", solicitudTrabajoAAcutalizar.getDescripcion());
                parametros.put("hora", solicitudTrabajoAAcutalizar.getHora());
                parametros.put("direccion", solicitudTrabajoAAcutalizar.getDireccion());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void setCordenadasEnElMainActivity(SolicitudTrabajo solicitudTrabajo) {
        ((MainActivity) getActivity()).getCordenadasDelFragmentEditarSolicitud(solicitudTrabajo.getLatitud(), solicitudTrabajo.getLongitud());
    }

    private void getCordenadasUbicacion() {
        latitud = ((MainActivity) getActivity()).latitud;
        longitud = ((MainActivity) getActivity()).longitud;
    }


}
