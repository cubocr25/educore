/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.view;

import edu.uam.educore.controller.EmpleadoController;
import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.enums.TipoPersonal;
import edu.uam.educore.model.personas.Empleado;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class EmpleadoView extends VistaBase {

    private final EmpleadoController controller;

    public EmpleadoView(Scanner scanner, Repositorio<Empleado> repo) {
        super(scanner);
        this.controller = new EmpleadoController(repo);
    }

    // ── Ciclo de la vista ─────────────────────────────────────────────

    public void iniciar() {
        boolean activo = true;

        while (activo) {
            int opcion = mostrarMenu();

            if (opcion == 1) {
                registrar();
            } else if (opcion == 2) {
                listar();
            } else if (opcion == 3) {
                buscar();
            } else if (opcion == 4) {
                actualizar();
            } else if (opcion == 5) {
                eliminar();
            } else if (opcion == 0) {
                activo = false;
            } else {
                mostrarError("Opción inválida.");
            }
        }
    }

    // ── Acciones ─────────────────────────────────────────────────────

    private void registrar() {

        String nombre = leerTexto("Nombre");
        String apellidos = leerTexto("Apellidos");
        String email = leerTexto("Email");
        double salario = leerDecimal("Salario");
        String fecha = leerTexto("Fecha de ingreso (AAAA-MM-DD)");

        TipoPersonal tipo = seleccionarTipo();

        try {

            Empleado empleado = controller.registrar(
                    nombre,
                    apellidos,
                    email,
                    salario,
                    LocalDate.parse(fecha),
                    tipo);

            mostrarMensaje(
                    "Registrado - ID: "
                            + empleado.getId()
                            + "\n"
                            + empleado.getInfo());

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void listar() {

        try {

            List<Empleado> lista = controller.listar();

            if (lista.isEmpty()) {
                mostrarMensaje("No hay empleados registrados.");
                return;
            }

            System.out.println("\n--- EMPLEADOS REGISTRADOS (" + lista.size() + ") ---");

            for (Empleado e : lista) {
                System.out.println("  " + e.getInfo());
            }

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void buscar() {

        int id = leerEntero("ID del empleado");

        try {

            Empleado e = controller.buscarPorId(id);

            if (e == null) {
                mostrarError("No existe empleado con ID " + id + ".");
            } else {
                System.out.println("\n" + e.getInfo());
            }

        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void actualizar() {

        int id = leerEntero("ID del empleado");

        try {

            Empleado existente = controller.buscarPorId(id);

            if (existente == null) {
                mostrarError("No existe empleado.");
                return;
            }

            System.out.println("\nDatos actuales:");
            System.out.println(existente.getInfo());

            String nombre = leerTexto("Nombre");
            String apellidos = leerTexto("Apellidos");
            String email = leerTexto("Email");
            double salario = leerDecimal("Salario");
            String fecha = leerTexto("Fecha de ingreso (AAAA-MM-DD)");

            TipoPersonal tipo = seleccionarTipo();

            Empleado actualizado = controller.actualizar(
                    id,
                    nombre,
                    apellidos,
                    email,
                    salario,
                    LocalDate.parse(fecha),
                    tipo);

            mostrarMensaje("Empleado actualizado.");
            System.out.println(actualizado.getInfo());

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void eliminar() {

        int id = leerEntero("ID del empleado");

        try {

            Empleado e = controller.buscarPorId(id);

            if (e == null) {
                mostrarError("No existe empleado.");
                return;
            }

            System.out.println(e.getInfo());

            String confirmar = leerTexto("¿Eliminar? (s/n)");

            if (!confirmar.equalsIgnoreCase("s")) {
                mostrarMensaje("Operación cancelada.");
                return;
            }

            controller.eliminar(id);

            mostrarMensaje("Empleado eliminado.");

        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    // ── Menús ────────────────────────────────────────────────────────

    private int mostrarMenu() {

        System.out.println("\n--- GESTIÓN DE EMPLEADOS ---");
        System.out.println("1. Registrar empleado");
        System.out.println("2. Listar empleados");
        System.out.println("3. Buscar por ID");
        System.out.println("4. Actualizar empleado");
        System.out.println("5. Eliminar empleado");
        System.out.println("0. Volver");

        return leerEntero();
    }

    private TipoPersonal seleccionarTipo() {

        System.out.println("\nTipo de empleado:");
        System.out.println("1. DOCENTE");
        System.out.println("2. CONSERJE");
        System.out.println("3. GUARDA");
        System.out.println("4. TECNICO");
        System.out.println("5. MANTENIMIENTO");

        int opcion = leerEntero("Tipo");

        return switch (opcion) {
            case 1 -> TipoPersonal.DOCENTE;
            case 2 -> TipoPersonal.CONSERJE;
            case 3 -> TipoPersonal.GUARDA;
            case 4 -> TipoPersonal.TECNICO;
            case 5 -> TipoPersonal.MANTENIMIENTO;
            default -> throw new IllegalArgumentException("Tipo inválido.");
        };
    }
}
