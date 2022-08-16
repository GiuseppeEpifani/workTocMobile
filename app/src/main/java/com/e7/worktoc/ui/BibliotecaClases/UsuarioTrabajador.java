package com.e7.worktoc.ui.BibliotecaClases;

public class UsuarioTrabajador extends UsuarioCliente {


    private String especialidad;
    private Float calificacion;
    private int id_usuario_trabajador;
    private String categoria;




    public UsuarioTrabajador(String correo, String nombre, String apellidopaterno, String nombre_foto, int numero, String especialidad, Float calificacion, String categoria, int id_usuario_trabajador) {
        super(correo,nombre,apellidopaterno,nombre_foto,numero);
        this.especialidad = especialidad;
        this.calificacion = calificacion;
        this.categoria = categoria;
        this.id_usuario_trabajador = id_usuario_trabajador;
    }



    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Float calificacion) {
        this.calificacion = calificacion;
    }

    public int getId_usuario_trabajador() {
        return id_usuario_trabajador;
    }

    public void setId_usuario_trabajador(int id_usuario_trabajador) {
        this.id_usuario_trabajador = id_usuario_trabajador;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
