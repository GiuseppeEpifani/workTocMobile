package com.e7.worktoc.ui.BibliotecaClases;

public class OpinionUsuario {



   public String comentario;
   public String fecha;




    public OpinionUsuario(String comentario, String fecha) {
        this.comentario = comentario;
        this.fecha = fecha;
    }


    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
