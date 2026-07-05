/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.view;

import edu.uam.educore.controller.EdificioController;
import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import java.util.List;
import java.util.Scanner;

public class EdificioView extends VistaBase {

  private final EdificioController controller;

  public EdificioView(Scanner scanner, Repositorio<Edificio> repo) {
    super(scanner);
    this.controller = new EdificioController(repo);
  }

  // ── Ciclo de la vista ─────────────────────────────────────────────

  public void iniciar() {
    boolean activo = true;

    while (activo) {

      int opcion = mostrarMenu();

      switch (opcion) {
        case 1 -> registrarEdificio();

        case 2 -> listarEdificios();

        case 3 -> buscarEdificio();

        case 4 -> eliminarEdificio();

        case 5 -> agregarAula();

        case 6 -> listarAulas();

        case 7 -> eliminarAula();

        case 0 -> activo = false;

        default -> mostrarError("Opción inválida.");
      }
    }
  }

  // ───────────────────────── EDIFICIOS ─────────────────────────

  private void registrarEdificio() {

    String codigo = leerTexto("Código");
    String nombre = leerTexto("Nombre");

    try {

      Edificio edificio = controller.registrar(codigo, nombre);

      mostrarMensaje("Edificio registrado.\n" + edificio.getInfo());

    } catch (Exception e) {
      mostrarError(e.getMessage());
    }
  }

  private void listarEdificios() {

    try {

      List<Edificio> lista = controller.listar();

      if (lista.isEmpty()) {

        mostrarMensaje("No hay edificios registrados.");
        return;
      }

      System.out.println("\n--- EDIFICIOS REGISTRADOS ---");

      for (Edificio e : lista) {

        System.out.println(e.getInfo());
      }

    } catch (Exception e) {

      mostrarError(e.getMessage());
    }
  }

  private void buscarEdificio() {

    int id = leerEntero("ID del edificio");

    try {

      Edificio edificio = controller.buscarPorId(id);

      if (edificio == null) {

        mostrarError("No existe edificio con ID " + id + ".");
        return;
      }

      System.out.println("\n" + edificio.getInfo());

      if (edificio.getAulas().isEmpty()) {

        mostrarMensaje("Este edificio no tiene aulas.");
        return;
      }

      System.out.println("\nAulas:");

      for (Aula aula : edificio.getAulas()) {

        System.out.println("  " + aula.getInfo());
      }

    } catch (Exception e) {

      mostrarError(e.getMessage());
    }
  }

  private void eliminarEdificio() {

    int id = leerEntero("ID del edificio");

    try {

      Edificio edificio = controller.buscarPorId(id);

      if (edificio == null) {

        mostrarError("No existe edificio con ID " + id + ".");
        return;
      }

      System.out.println(edificio.getInfo());

      String confirmar = leerTexto("¿Confirma la eliminación? (s/n)");

      if (!confirmar.equalsIgnoreCase("s")) {

        mostrarMensaje("Operación cancelada.");
        return;
      }

      controller.eliminar(id);

      mostrarMensaje("Edificio eliminado.");

    } catch (Exception e) {

      mostrarError(e.getMessage());
    }
  }

  // ───────────────────────── AULAS ─────────────────────────

  private void agregarAula() {

    int edificioId = leerEntero("ID del edificio");

    String numero = leerTexto("Número");

    int capacidad = leerEntero("Capacidad");

    String tipo = leerTexto("Tipo");

    try {

      Aula aula = controller.agregarAula(edificioId, numero, capacidad, tipo);

      mostrarMensaje("Aula registrada.\n" + aula.getInfo());

    } catch (Exception e) {

      mostrarError(e.getMessage());
    }
  }

  private void listarAulas() {

    int edificioId = leerEntero("ID del edificio");

    try {

      Edificio edificio = controller.buscarPorId(edificioId);

      if (edificio == null) {

        mostrarError("No existe edificio.");
        return;
      }

      if (edificio.getAulas().isEmpty()) {

        mostrarMensaje("Este edificio no tiene aulas.");
        return;
      }

      System.out.println("\n--- AULAS ---");

      for (Aula aula : edificio.getAulas()) {

        System.out.println(aula.getInfo());
      }

    } catch (Exception e) {

      mostrarError(e.getMessage());
    }
  }

  private void eliminarAula() {

    int edificioId = leerEntero("ID del edificio");

    int aulaId = leerEntero("ID del aula");

    try {

      controller.eliminarAula(edificioId, aulaId);

      mostrarMensaje("Aula eliminada.");

    } catch (Exception e) {

      mostrarError(e.getMessage());
    }
  }

  // ───────────────────────── MENÚ ─────────────────────────

  private int mostrarMenu() {

    System.out.println("\n--- GESTIÓN DE EDIFICIOS Y AULAS ---");
    System.out.println("1. Registrar edificio");
    System.out.println("2. Listar edificios");
    System.out.println("3. Buscar edificio");
    System.out.println("4. Eliminar edificio");
    System.out.println("5. Agregar aula");
    System.out.println("6. Listar aulas de un edificio");
    System.out.println("7. Eliminar aula");
    System.out.println("0. Volver");

    System.out.print("Opción: ");

    return leerEntero();
  }
}
