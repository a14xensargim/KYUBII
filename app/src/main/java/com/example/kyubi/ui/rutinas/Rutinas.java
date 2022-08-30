package com.example.kyubi.ui.rutinas;

public class Rutinas {
    public String id;
    String IMATGE, NOMBRE,FECHA,APELLIDO,CORREO;

    public Rutinas() {
    }

    public Rutinas(String IMATGE, String FECHA, String NOMBRE, String APELLIDO, String CORREO) {
        this.IMATGE = IMATGE;
        this.FECHA = FECHA;
        this.NOMBRE = NOMBRE;
        this.APELLIDO = APELLIDO;
        this.CORREO = CORREO;
    }

    public String getIMATGE() {
        return IMATGE;
    }

    public void setIMATGE(String IMATGE) {
        this.IMATGE = IMATGE;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getFECHA() {
        return FECHA;
    }

    public void setFECHA(String FECHA) {
        this.FECHA = FECHA;
    }

    public String getAPELLIDO() {
        return APELLIDO;
    }

    public void setAPELLIDO(String APELLIDO) {
        this.APELLIDO = APELLIDO;
    }

    public String getCORREO() {
        return CORREO;
    }

    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }
}
