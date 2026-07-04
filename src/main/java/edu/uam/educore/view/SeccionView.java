/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.view;

import edu.uam.educore.controller.SeccionController;
import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.infraestructura.Edificio;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;
import java.util.List;
import java.util.Scanner;

public class SeccionView extends VistaBase {

    private final SeccionController controller;

    public SeccionView(
            Scanner scanner,
            Repositorio<Seccion> seccionRepo,
            Repositorio<Empleado> empleadoRepo,
            Repositorio<Estudiante> estudianteRepo,
            Repositorio<Edificio> edificioRepo) {

        super(scanner);

        controller = new SeccionController(
                seccionRepo,
                empleadoRepo,
                estudianteRepo,
                edificioRepo);
    }

    public void iniciar() {

        boolean activo = true;

        while (activo) {

            switch (mostrarMenu()) {

                case 1 -> registrar();

                case 2 -> listar();

                case 3 -> agregarEstudiante();

                case 4 -> removerEstudiante();

                case 5 -> eliminar();

                case 0 -> activo = false;

                default -> mostrarError("Opción inválida.");
            }
        }
    }

    // --------------------------------------------------------

    private void registrar() {

        String codigo = leerTexto("Código");
        String nombre = leerTexto("Nombre");

        int aulaId = leerEntero("ID del aula");

        int docenteId = leerEntero("ID del docente");

        try {

            Seccion s =
                    controller.registrar(
                            codigo,
                            nombre,
                            aulaId,
                            docenteId);

            mostrarMensaje(
                    "Sección registrada.\n"
                            + s.getInfo());

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void listar() {

        try {

            List<Seccion> lista = controller.listar();

            if (lista.isEmpty()) {

                mostrarMensaje("No hay secciones registradas.");
                return;
            }

            System.out.println("\n--- SECCIONES ---");

            for (Seccion s : lista) {

                System.out.println(s.getInfo());
            }

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void agregarEstudiante() {

        int seccionId = leerEntero("ID de la sección");

        int estudianteId = leerEntero("ID del estudiante");

        try {

            controller.agregarEstudiante(
                    seccionId,
                    estudianteId);

            mostrarMensaje("Estudiante agregado.");

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void removerEstudiante() {

        int seccionId = leerEntero("ID de la sección");

        int estudianteId = leerEntero("ID del estudiante");

        try {

            controller.removerEstudiante(
                    seccionId,
                    estudianteId);

            mostrarMensaje("Estudiante removido.");

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void eliminar() {

        int id = leerEntero("ID de la sección");

        try {

            Seccion s = controller.buscarPorId(id);

            if (s == null) {

                mostrarError("No existe la sección.");
                return;
            }

            System.out.println(s.getInfo());

            String confirmar =
                    leerTexto("¿Confirma la eliminación? (s/n)");

            if (!confirmar.equalsIgnoreCase("s")) {

                mostrarMensaje("Operación cancelada.");
                return;
            }

            controller.eliminar(id);

            mostrarMensaje("Sección eliminada.");

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    // --------------------------------------------------------

    private int mostrarMenu() {

        System.out.println("\n--- GESTIÓN DE SECCIONES ---");
        System.out.println("1. Registrar sección");
        System.out.println("2. Listar secciones");
        System.out.println("3. Agregar estudiante");
        System.out.println("4. Remover estudiante");
        System.out.println("5. Eliminar sección");
        System.out.println("0. Volver");

        return leerEntero();
    }
}
