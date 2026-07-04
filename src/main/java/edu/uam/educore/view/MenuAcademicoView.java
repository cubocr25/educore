/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.view;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.infraestructura.Edificio;
import java.util.Scanner;
import edu.uam.educore.model.academico.Seccion;
import edu.uam.educore.model.personas.Empleado;
import edu.uam.educore.model.personas.Estudiante;

public class MenuAcademicoView extends VistaBase {

    private final EdificioView edificioView;
    private final SeccionView seccionView;

    public MenuAcademicoView(
        Scanner scanner,
        Repositorio<Edificio> edificioRepo,
        Repositorio<Seccion> seccionRepo,
        Repositorio<Empleado> empleadoRepo,
        Repositorio<Estudiante> estudianteRepo) {

    super(scanner);

    this.edificioView = new EdificioView(scanner, edificioRepo);

    this.seccionView = new SeccionView(
            scanner,
            seccionRepo,
            empleadoRepo,
            estudianteRepo,
            edificioRepo);
}

    public void iniciar() {

        boolean activo = true;

        while (activo) {

            switch (mostrarMenu()) {

                case 1 -> edificioView.iniciar();

                case 2 -> seccionView.iniciar();

                case 0 ->
                    activo = false;

                default ->
                    mostrarError("Opción inválida.");
            }
        }
    }

    private int mostrarMenu() {

        System.out.println("\n--- GESTIÓN ACADÉMICA ---");
        System.out.println("1. Edificios y Aulas");
        System.out.println("2. Secciones");
        System.out.println("0. Volver");
        System.out.print("Opción: ");

        return leerEntero();
    }
}
