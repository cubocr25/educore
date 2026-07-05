/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.model.personas;

import static org.junit.jupiter.api.Assertions.*;

import edu.uam.educore.enums.TipoPersonal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EmpleadoTest {

  @Test
  void constructor_crea_empleado_correctamente() {

    Empleado e =
        new Empleado(
            1,
            "Daniel",
            "Cubero",
            "cubero@test.pro",
            123456,
            LocalDate.of(2025, 1, 15),
            TipoPersonal.TECNICO);

    assertEquals(1, e.getId());
    assertEquals("Daniel", e.getNombre());
    assertEquals("Cubero", e.getApellidos());
    assertEquals("cubero@test.pro", e.getEmail());
    assertEquals(123456, e.getSalario(), 0.01);
    assertEquals(LocalDate.of(2025, 1, 15), e.getFechaIngreso());
    assertEquals(TipoPersonal.TECNICO, e.getTipoPersonal());
  }

  @Test
  void getTipo_retorna_texto_correcto() {

    Empleado e =
        new Empleado(
            1,
            "Carolina",
            "Granados",
            "granados@test.pro",
            654321,
            LocalDate.now(),
            TipoPersonal.GUARDA);

    assertEquals("GUARDA", e.getTipo());
  }

  @Test
  void getInfo_incluye_nombre_tipo_y_salario() {

    Empleado e =
        new Empleado(
            1,
            "Roko",
            "Loco",
            "Loco@test.pro",
            987654,
            LocalDate.of(2025, 1, 15),
            TipoPersonal.CONSERJE);

    String info = e.getInfo();

    assertTrue(info.contains("Roko"));
    assertTrue(info.contains("CONSERJE"));
    assertTrue(info.contains("987654"));
  }
}
