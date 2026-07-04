/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.model.infraestructura;

public class Aula {

    private int id;
    private String numero;
    private int capacidad;
    private String tipo;
    private Edificio edificio;

    public Aula(int id, String numero, int capacidad, String tipo, Edificio edificio) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.edificio = edificio;
    }

    public int getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getTipo() {
        return tipo;
    }

    public Edificio getEdificio() {
        return edificio;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getInfo() {
        return String.format(
                "Aula %s | Capacidad: %d | Tipo: %s",
                numero,
                capacidad,
                tipo);
    }

    @Override
    public String toString() {
        return getInfo();
    }
}
