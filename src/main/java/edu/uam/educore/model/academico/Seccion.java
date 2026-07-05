/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.model.academico;

import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import java.util.ArrayList;
import java.util.List;

public class Seccion {

  private int id;
  private String codigo;
  private String nombre;

  private Aula aula;
  private Empleado docente;

  private final List<Estudiante> estudiantes = new ArrayList<>();

  public Seccion(int id, String codigo, String nombre, Aula aula, Empleado docente) {

    this.id = id;
    this.codigo = codigo;
    this.nombre = nombre;
    this.aula = aula;
    this.docente = docente;
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

  public Aula getAula() {
    return aula;
  }

  public Empleado getDocente() {
    return docente;
  }

  public List<Estudiante> getEstudiantes() {
    return estudiantes;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setAula(Aula aula) {
    this.aula = aula;
  }

  public void setDocente(Empleado docente) {
    this.docente = docente;
  }

  // ---------------- Estudiantes ----------------

  public void agregarEstudiante(Estudiante estudiante) {
    estudiantes.add(estudiante);
  }

  public void removerEstudiante(int estudianteId) {

    for (int i = 0; i < estudiantes.size(); i++) {

      if (estudiantes.get(i).getId() == estudianteId) {

        estudiantes.remove(i);
        return;
      }
    }
  }

  public String getInfo() {

    return String.format(
        "[%s] %s | Aula: %s | Docente: %s %s | Estudiantes: %d",
        codigo,
        nombre,
        aula.getNumero(),
        docente.getNombre(),
        docente.getApellidos(),
        estudiantes.size());
  }

  @Override
  public String toString() {
    return getInfo();
  }
}
