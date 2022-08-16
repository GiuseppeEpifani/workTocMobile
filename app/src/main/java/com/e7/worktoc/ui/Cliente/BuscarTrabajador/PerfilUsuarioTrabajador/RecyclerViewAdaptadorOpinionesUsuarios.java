package com.e7.worktoc.ui.Cliente.BuscarTrabajador.PerfilUsuarioTrabajador;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e7.worktoc.R;
import com.e7.worktoc.ui.BibliotecaClases.BorderedCircleTransform;
import com.e7.worktoc.ui.BibliotecaClases.UsuarioCliente;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RecyclerViewAdaptadorOpinionesUsuarios extends RecyclerView.Adapter<RecyclerViewAdaptadorOpinionesUsuarios.ViewHolder> {
    public List<UsuarioCliente> opiniones;


    public RecyclerViewAdaptadorOpinionesUsuarios(List<UsuarioCliente> opiniones) {
        this.opiniones = opiniones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista_comentarios, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombreTxt.setText(opiniones.get(position).getNombre() + " " + opiniones.get(position).getApellidoPaterno());
        holder.fechaTxt.setText(opiniones.get(position).getOpinion().getFecha());
        holder.comentarioTxt.setText(opiniones.get(position).getOpinion().getComentario());
        String foto = opiniones.get(position).getNombreFoto();
        Picasso.get()
                .load("http://35.174.185.92/servicios/uploads/" + foto)
                .resize(200, 200).transform(new BorderedCircleTransform(1, Color.BLACK))
                .into(holder.imagenUsuario);
    }

    @Override
    public int getItemCount() {
        return opiniones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreTxt, fechaTxt, comentarioTxt;
        ImageView imagenUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTxt = (TextView) itemView.findViewById(R.id.NombreUsuarioOpinionTxt);
            fechaTxt = (TextView) itemView.findViewById(R.id.FechaOpinionTxt);
            comentarioTxt = (TextView) itemView.findViewById(R.id.ComentarioOpinionTxt);
            imagenUsuario = (ImageView) itemView.findViewById(R.id.imagenUsuarioOpinion);
        }
    }


}
