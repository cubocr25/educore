/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.model.infraestructura;

import java.util.ArrayList;
import java.util.List;

public class Edificio {

  private int id;
  private String codigo;
  private String nombre;

  private final List<Aula> aulas = new ArrayList<>();

  public Edificio(int id, String codigo, String nombre) {
    this.id = id;
    this.codigo = codigo;
    this.nombre = nombre;
  }

  public int getId() {
    return id;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public List<Aula> getAulas() {
    return aulas;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  // ---------------- Composición ----------------

  public void agregarAula(Aula aula) {
    aulas.add(aula);
  }

  public void eliminarAula(int aulaId) {

    for (int i = 0; i < aulas.size(); i++) {

      if (aulas.get(i).getId() == aulaId) {

        aulas.remove(i);
        return;
      }
    }
  }

  public Aula buscarAula(int aulaId) {

    for (Aula aula : aulas) {

      if (aula.getId() == aulaId) {
        return aula;
      }
    }

    return null;
  }

  public String getInfo() {

    return String.format("[%d] %s - %s | Aulas: %d", id, codigo, nombre, aulas.size());
  }

  @Override
  public String toString() {
    return getInfo();
  }
}
