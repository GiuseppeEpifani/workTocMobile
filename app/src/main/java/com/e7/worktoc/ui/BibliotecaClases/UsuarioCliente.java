package com.e7.worktoc.ui.BibliotecaClases;


public class UsuarioCliente {

    private String correo;
    private String nombre;
    private String apellidoPaterno;
    private String nombreFoto;
    private int numero;
    OpinionUsuario opinion;

    private int idCliente;

    public UsuarioCliente() {

    }

    public UsuarioCliente(String correo, String nombre, String apellidoPaterno, String nombreFoto, int numero) {
        this.correo = correo;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.nombreFoto = nombreFoto;
        this.numero = numero;
    }

    public OpinionUsuario getOpinion() {
        return opinion;
    }

    public void setOpinion(OpinionUsuario opinion) {
        this.opinion = opinion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getNombreFoto() {
        return nombreFoto;
    }

    public void setNombreFoto(String nombreFoto) {
        this.nombreFoto = nombreFoto;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

}
