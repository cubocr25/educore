/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.dao;

import edu.uam.educore.model.infraestructura.Edificio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaEdificioRepo extends Repositorio<Edificio> {

    private final List<Edificio> lista = new ArrayList<>();

    @Override
    public void guardar(Edificio edificio) {
        lista.add(edificio);
    }

    @Override
    public void actualizar(Edificio actualizado) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == actualizado.getId()) {
                lista.set(i, actualizado);
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
    public Optional<Edificio> buscarPorId(int id) {
        for (Edificio edificio : lista) {
            if (edificio.getId() == id) {
                return Optional.of(edificio);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Edificio> buscarTodos() {
        return new ArrayList<>(lista);
    }
}
