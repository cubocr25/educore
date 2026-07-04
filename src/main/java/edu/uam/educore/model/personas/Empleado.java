/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.model.personas;

import edu.uam.educore.enums.TipoPersonal;
import java.time.LocalDate;

public class Empleado extends Persona {

    private double salario;
    private LocalDate fechaIngreso;
    private TipoPersonal tipo;

    public Empleado(int id,
                    String nombre,
                    String apellidos,
                    String email,
                    double salario,
                    LocalDate fechaIngreso,
                    TipoPersonal tipo) {

        super(id, nombre, apellidos, email);
        this.salario = salario;
        this.fechaIngreso = fechaIngreso;
        this.tipo = tipo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public TipoPersonal getTipoPersonal() {
        return tipo;
    }

    public void setTipoPersonal(TipoPersonal tipo) {
        this.tipo = tipo;
    }

    @Override
    public String getTipo() {
        return tipo.name();
    }

    @Override
    public String getInfo() {
        return String.format(
                "[%s] %s %s | Email: %s | Salario: ₡%.2f | Ingreso: %s",
                getTipo(),
                getNombre(),
                getApellidos(),
                getEmail(),
                salario,
                fechaIngreso);
    }
}