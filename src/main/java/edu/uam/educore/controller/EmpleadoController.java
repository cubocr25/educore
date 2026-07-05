/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.controller;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.enums.TipoPersonal;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.util.Validador;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmpleadoController {

  private final Repositorio<Empleado> repo;
  private int proximoId = 1;

  public EmpleadoController(Repositorio<Empleado> repo) {
    this.repo = repo;
  }

  public Empleado registrar(
      String nombre,
      String apellidos,
      String email,
      double salario,
      LocalDate fechaIngreso,
      TipoPersonal tipo)
      throws Exception {

    validarBase(nombre, apellidos, email, salario);

    Empleado empleado =
        new Empleado(proximoId, nombre, apellidos, email, salario, fechaIngreso, tipo);

    repo.guardar(empleado);
    proximoId++;

    return empleado;
  }

  public List<Empleado> listar() throws Exception {
    return repo.buscarTodos();
  }

  public Empleado buscarPorId(int id) throws Exception {
    Optional<Empleado> resultado = repo.buscarPorId(id);
    return resultado.orElse(null);
  }

  public Empleado actualizar(
      int id,
      String nombre,
      String apellidos,
      String email,
      double salario,
      LocalDate fechaIngreso,
      TipoPersonal tipo)
      throws Exception {

    Empleado empleado = buscarPorId(id);

    if (empleado == null) {
      throw new IllegalArgumentException("No existe empleado con ID " + id + ".");
    }

    validarBase(nombre, apellidos, email, salario);

    empleado.setNombre(nombre);
    empleado.setApellidos(apellidos);
    empleado.setEmail(email);
    empleado.setSalario(salario);
    empleado.setFechaIngreso(fechaIngreso);
    empleado.setTipoPersonal(tipo);

    repo.actualizar(empleado);

    return empleado;
  }

  public void eliminar(int id) throws Exception {

    Empleado empleado = buscarPorId(id);

    if (empleado == null) {
      throw new IllegalArgumentException("No existe empleado con ID " + id + ".");
    }

    repo.eliminar(id);
  }

  private void validarBase(String nombre, String apellidos, String email, double salario) {

    if (nombre.isBlank() || apellidos.isBlank()) {
      throw new IllegalArgumentException("Nombre y apellidos son obligatorios.");
    }

    if (!Validador.validarEmail(email)) {
      throw new IllegalArgumentException("Email inválido.");
    }

    if (salario < 0) {
      throw new IllegalArgumentException("El salario no puede ser negativo.");
    }
  }
}
