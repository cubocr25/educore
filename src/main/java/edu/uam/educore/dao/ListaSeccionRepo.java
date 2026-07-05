/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.dao;

import edu.uam.educore.model.academico.Seccion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaSeccionRepo extends Repositorio<Seccion> {

  private final List<Seccion> lista = new ArrayList<>();

  @Override
  public void guardar(Seccion seccion) {
    lista.add(seccion);
  }

  @Override
  public void actualizar(Seccion actualizada) {
    for (int i = 0; i < lista.size(); i++) {
      if (lista.get(i).getId() == actualizada.getId()) {
        lista.set(i, actualizada);
        return;
      }
    }
  }

  @Override
  public void eliminar(int id) {
    for (int i = 0; i < lista.size(); i++) {
      if (lista.get(i).getId() == id) {
        lista.remove(i);
        return;
      }
    }
  }

  @Override
  public Optional<Seccion> buscarPorId(int id) {
    for (Seccion seccion : lista) {
      if (seccion.getId() == id) {
        return Optional.of(seccion);
      }
    }
    return Optional.empty();
  }

  @Override
  public List<Seccion> buscarTodos() {
    return new ArrayList<>(lista);
  }
}
