package com.e7.worktoc.ui.Trabajador.BuscarTrabajo;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e7.worktoc.MainActivity;
import com.e7.worktoc.R;

public class InicioTrabajadorFragment extends Fragment {

    private InicioTrabajadorViewModel mViewModel;

    public static InicioTrabajadorFragment newInstance() {
        return new InicioTrabajadorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).buscarUbicacionesDeTodosLosTrabajosPublicados("http://35.174.185.92/servicios/buscar_todas_las_solicitudes_trabajo.php");
        ((MainActivity) getActivity()).iniciarLocalizacionTrabajador();
        return inflater.inflate(R.layout.inicio_trabajador_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(InicioTrabajadorViewModel.class);
        // TODO: Use the ViewModel
    }

}
