package com.e7.worktoc.ui.Cliente.EditarSolicitud;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.SolicitudTrabajo;

import java.util.List;

public class RecyclerViewAdaptadorEditarSolicitud extends RecyclerView.Adapter<RecyclerViewAdaptadorEditarSolicitud.ViewHolder> {

    private OnItemClickListener mListener;
    private List<SolicitudTrabajo> Solicitudes;

    public interface OnItemClickListener {
        void onDeleteClick(int position, int id_solicitud);

        void onEditClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerViewAdaptadorEditarSolicitud(List<SolicitudTrabajo> Solicitudes) {
        this.Solicitudes = Solicitudes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista_solicitud, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtCategoria.setText(Solicitudes.get(position).getCategoria());
        holder.txtDescipcion.setText(Solicitudes.get(position).getDescripcion());
        holder.txtHora.setText(Solicitudes.get(position).getHora());
        holder.txtPrecio.setText(Solicitudes.get(position).getPrecio() + "");
        holder.txtfecha.setText(Solicitudes.get(position).getFecha_hora());
        holder.txtDireccion.setText(Solicitudes.get(position).getDireccion());


    }

    @Override
    public int getItemCount() {
        return Solicitudes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCategoria, txtHora, txtPrecio, txtDescipcion, txtfecha, txtDireccion;
        ImageView btnEliminar, btnEditar;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            txtCategoria = (TextView) itemView.findViewById(R.id.txtCategoriaRecycler);
            txtHora = (TextView) itemView.findViewById(R.id.txtHoraRecycler);
            txtPrecio = (TextView) itemView.findViewById(R.id.txtPrecio);
            txtDescipcion = (TextView) itemView.findViewById(R.id.txtDescripcionRecycler);
            txtfecha = (TextView) itemView.findViewById(R.id.txtFechaPublicacionRecycler);
            txtDireccion = (TextView) itemView.findViewById(R.id.txtDireccionRecycler);
            btnEditar = (ImageView) itemView.findViewById(R.id.btnActualizarRecycler);
            btnEliminar = (ImageView) itemView.findViewById(R.id.btnEliminarRecycler);


            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position, Solicitudes.get(position).getId_solicitud());
                        }
                    }
                }
            });

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });
        }

    }


}
