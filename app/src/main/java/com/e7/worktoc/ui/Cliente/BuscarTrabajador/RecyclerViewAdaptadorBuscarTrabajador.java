package com.e7.worktoc.ui.Cliente.BuscarTrabajador;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.BorderedCircleTransform;
import com.e7.worktoc.ui.BibliotecaClases.UsuarioTrabajador;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdaptadorBuscarTrabajador extends RecyclerView.Adapter<RecyclerViewAdaptadorBuscarTrabajador.ViewHolder> {
    private OnItemClickListener mListener;
    public List<UsuarioTrabajador> trabajadores;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onCallClick(int numero);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerViewAdaptadorBuscarTrabajador(List<UsuarioTrabajador> trabajadores) {
        this.trabajadores = trabajadores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista_usuarios_trabajdores, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.NombreTxt.setText(trabajadores.get(position).getNombre() + " " + trabajadores.get(position).getApellidoPaterno());
        holder.EspecialidadTxt.setText(trabajadores.get(position).getEspecialidad());
        holder.calificacion.setRating(trabajadores.get(position).getCalificacion());
        String foto = trabajadores.get(position).getNombreFoto();
        Picasso.get()
                .load("http://35.174.185.92/servicios/uploads/" + foto)
                .resize(200, 200).transform(new BorderedCircleTransform(1, Color.BLACK))
                .into(holder.imagenUsuario);
    }

    @Override
    public int getItemCount() {
        return trabajadores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView NombreTxt;
        private TextView EspecialidadTxt;
        RatingBar calificacion;
        ImageView ContactarBtn, imagenUsuario;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            NombreTxt = (TextView) itemView.findViewById(R.id.txtNombreUsuario);
            EspecialidadTxt = (TextView) itemView.findViewById(R.id.txtEspecialidadUsuario);
            ContactarBtn = (ImageView) itemView.findViewById(R.id.btnContactarUsuario);
            imagenUsuario = (ImageView) itemView.findViewById(R.id.imagen_usuario);
            calificacion = (RatingBar) itemView.findViewById(R.id.calificacion);


            ContactarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCallClick(trabajadores.get(position).getNumero());
                        }
                    }
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });


        }
    }

    public void filtrarTrabajadores(ArrayList<UsuarioTrabajador> filtroUsuarios) {
        this.trabajadores = filtroUsuarios;
        notifyDataSetChanged();
    }


}
