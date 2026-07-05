/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.controller;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.enums.TipoPersonal;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import java.util.List;
import java.util.Optional;

public class SeccionController {

  private final Repositorio<Seccion> seccionRepo;
  private final Repositorio<Empleado> empleadoRepo;
  private final Repositorio<Estudiante> estudianteRepo;
  private final Repositorio<Edificio> edificioRepo;

  private int proximoId = 1;

  public SeccionController(
      Repositorio<Seccion> seccionRepo,
      Repositorio<Empleado> empleadoRepo,
      Repositorio<Estudiante> estudianteRepo,
      Repositorio<Edificio> edificioRepo) {

    this.seccionRepo = seccionRepo;
    this.empleadoRepo = empleadoRepo;
    this.estudianteRepo = estudianteRepo;
    this.edificioRepo = edificioRepo;
  }

  // ================= REGISTRAR =================

  public Seccion registrar(String codigo, String nombre, int aulaId, int docenteId)
      throws Exception {

    validar(codigo, nombre);

    // Buscar docente
    Optional<Empleado> empleado = empleadoRepo.buscarPorId(docenteId);

    if (empleado.isEmpty()) {
      throw new IllegalArgumentException("No existe empleado con ID " + docenteId + ".");
    }

    if (empleado.get().getTipoPersonal() != TipoPersonal.DOCENTE) {
      throw new IllegalArgumentException("El empleado seleccionado no es un docente.");
    }

    // Buscar aula recorriendo edificios
    Aula aula = buscarAula(aulaId);

    if (aula == null) {
      throw new IllegalArgumentException("No existe aula con ID " + aulaId + ".");
    }

    Seccion seccion = new Seccion(proximoId, codigo, nombre, aula, empleado.get());

    seccionRepo.guardar(seccion);

    proximoId++;

    return seccion;
  }

  // ================= CRUD =================

  public List<Seccion> listar() throws Exception {
    return seccionRepo.buscarTodos();
  }

  public Seccion buscarPorId(int id) throws Exception {

    Optional<Seccion> resultado = seccionRepo.buscarPorId(id);

    return resultado.orElse(null);
  }

  public void eliminar(int id) throws Exception {

    Seccion seccion = buscarPorId(id);

    if (seccion == null) {
      throw new IllegalArgumentException("No existe la sección.");
    }

    if (!seccion.getEstudiantes().isEmpty()) {
      throw new IllegalArgumentException(
          "No puede eliminar una sección con estudiantes inscritos.");
    }

    seccionRepo.eliminar(id);
  }

  // ================= ESTUDIANTES =================

  public void agregarEstudiante(int seccionId, int estudianteId) throws Exception {

    Seccion seccion = buscarPorId(seccionId);

    if (seccion == null) {
      throw new IllegalArgumentException("No existe la sección.");
    }

    Optional<Estudiante> estudiante = estudianteRepo.buscarPorId(estudianteId);

    if (estudiante.isEmpty()) {
      throw new IllegalArgumentException("No existe estudiante con ID " + estudianteId + ".");
    }

    seccion.agregarEstudiante(estudiante.get());
  }

  public void removerEstudiante(int seccionId, int estudianteId) throws Exception {

    Seccion seccion = buscarPorId(seccionId);

    if (seccion == null) {
      throw new IllegalArgumentException("No existe la sección.");
    }

    seccion.removerEstudiante(estudianteId);
  }

  // ================= AUXILIARES =================

  private Aula buscarAula(int aulaId) throws Exception {

    List<Edificio> edificios = edificioRepo.buscarTodos();

    for (Edificio edificio : edificios) {

      Aula aula = edificio.buscarAula(aulaId);

      if (aula != null) {
        return aula;
      }
    }

    return null;
  }

  private void validar(String codigo, String nombre) {

    if (codigo.isBlank() || nombre.isBlank()) {
      throw new IllegalArgumentException("Código y nombre son obligatorios.");
    }
  }
}
