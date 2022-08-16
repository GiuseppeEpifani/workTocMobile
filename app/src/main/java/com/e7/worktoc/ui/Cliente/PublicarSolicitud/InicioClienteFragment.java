package com.e7.worktoc.ui.Cliente.PublicarSolicitud;

import androidx.lifecycle.ViewModelProviders;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class InicioClienteFragment extends Fragment {

    private InicioClienteViewModel mViewModel;
    public Double latitud;
    public Double longitud;
    private Spinner Spinnercategorias;
    private Button publicarBtn;
    private EditText precioTxt, descripcionTxt, horaTxt;
    private String direccion;

    public static InicioClienteFragment newInstance() {
        return new InicioClienteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView =
                inflater.inflate(R.layout.inicio_cliente_fragment, container, false);

        ((MainActivity) getActivity()).iniciarLocalizacionCliente();

        Spinnercategorias = (Spinner) rootView.findViewById(R.id.Spinnercategoria);
        precioTxt = (EditText) rootView.findViewById(R.id.txtPrecio);
        descripcionTxt = (EditText) rootView.findViewById(R.id.txtDescripcion);
        horaTxt = (EditText) rootView.findViewById(R.id.txtHora);
        publicarBtn = (Button) rootView.findViewById(R.id.btnPublicar);
        precioTxt.requestFocus();

        evitarClickEnHoraTxt();
        horaTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogHora(horaTxt);
            }
        });

        publicarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarQueEstenLLenosLosCamposSolicitud()) {
                    bloquearBtnPublicar();
                    procesarSolicitudDeTrabajo();
                } else {
                    Toast.makeText(getContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cargarSpinnerCategorias(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(InicioClienteViewModel.class);
    }


    private void cargarSpinnerCategorias(View rootView) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext().getApplicationContext(), R.array.arrayCategoria, R.layout.spinner_categoria_estilo);
        Spinnercategorias.setAdapter(adapter);
    }

    private void evitarClickEnHoraTxt() {
        horaTxt.setInputType(InputType.TYPE_NULL);
        horaTxt.setFocusable(false);
    }

    private void bloquearBtnPublicar() {
        publicarBtn.setSelectAllOnFocus(false);
    }

    private void desbloquearBtnPublicar() {
        publicarBtn.setSelectAllOnFocus(true);
    }

    private void mostrarDialogHora(final TextView hora) {
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

        new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private boolean validarQueEstenLLenosLosCamposSolicitud() {
        return precioTxt.getText().toString() != "" && precioTxt.getText().length() > 0 && descripcionTxt.getText().toString() != "" && descripcionTxt.getText().length() > 0 && !Spinnercategorias.getSelectedItem().equals("Seleccioné Categoria")
                && horaTxt.getText().toString() != "" && horaTxt.getText().length() > 0;
    }

    private void procesarSolicitudDeTrabajo() {
        setCordenadasYDireccion();
        publicarSolicitudDeTrabajo("http://35.174.185.92/servicios/insertar_solicitud_trabajo.php");
    }

    private void setCordenadasYDireccion() {
        getCordenadasDelMainActivity();
        try {
            obtenerDireccionMedianteLasCordenadas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getCordenadasDelMainActivity() {
        latitud = ((MainActivity) getActivity()).latitud;
        longitud = ((MainActivity) getActivity()).longitud;
    }

    private void obtenerDireccionMedianteLasCordenadas() throws IOException {
        //inicializamos el geoCoder
        Geocoder geocoder = new Geocoder(getActivity(),
                Locale.getDefault());
        //inicializamos el addres list
        List<Address> addresses = geocoder.getFromLocation(
                latitud, longitud, 1
        );
        direccion = addresses.get(0).getAddressLine(0);

        String direc = direccion;
        String[] arrayColores = direc.split(",");

        for (int i = 0; i < 1; i++) {
            direccion = arrayColores[i];
        }


    }

    private void publicarSolicitudDeTrabajo(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               cargarSplashProcesandoSolicitud();
                //con esto evitamos el retroceso del fragment
                getActivity().onBackPressed();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Algo fallo intentelo nuevamente", Toast.LENGTH_LONG).show();
                desbloquearBtnPublicar();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("latitud", latitud.toString());
                parametros.put("longitud", longitud.toString());
                parametros.put("precio", precioTxt.getText().toString());
                parametros.put("categoria", Spinnercategorias.getSelectedItem().toString());
                parametros.put("descripcion", descripcionTxt.getText().toString());
                parametros.put("hora", horaTxt.getText().toString());
                parametros.put("id_cliente", String.valueOf(((MainActivity) getActivity()).ID_CLIENTE));
                parametros.put("direccion", direccion);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }






    private void cargarSplashProcesandoSolicitud() {
        Intent intent = new Intent(getContext(), SplashProcesarSolicitudTrabajo.class);
        startActivity(intent);
    }


}
