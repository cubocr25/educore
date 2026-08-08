/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uam.educore.controller;

import edu.uam.educore.dao.Repositorio;
import edu.uam.educore.model.infraestructura.Aula;
import edu.uam.educore.model.infraestructura.Edificio;
import java.util.List;
import java.util.Optional;
import edu.uam.educore.dao.EdificioRepoSql;

public class EdificioController {

    private final Repositorio<Edificio> repo;

    private int proximoId = 1;

    public EdificioController(Repositorio<Edificio> repo) {
        this.repo = repo;
    }

    // ================= EDIFICIOS =================
    public Edificio registrar(String codigo, String nombre) throws Exception {

        validarEdificio(codigo, nombre);

        Edificio edificio = new Edificio(proximoId, codigo, nombre);

        repo.guardar(edificio);
        proximoId++;

        return edificio;
    }

    public List<Edificio> listar() throws Exception {
        return repo.buscarTodos();
    }

    public Edificio buscarPorId(int id) throws Exception {

        Optional<Edificio> resultado = repo.buscarPorId(id);

        return resultado.orElse(null);
    }

    public void eliminar(int id) throws Exception {

        Edificio edificio = buscarPorId(id);

        if (edificio == null) {
            throw new IllegalArgumentException("No existe edificio con ID " + id + ".");
        }

        if (!edificio.getAulas().isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el edificio porque todavía tiene aulas.");
        }

        repo.eliminar(id);
    }

    public Edificio actualizar(int id, String codigo, String nombre) throws Exception {

        Edificio edificio = buscarPorId(id);

        if (edificio == null) {
            throw new IllegalArgumentException(
                    "No existe edificio con ID " + id + ".");
        }

        validarEdificio(codigo, nombre);

        edificio.setCodigo(codigo);
        edificio.setNombre(nombre);

        repo.actualizar(edificio);

        return edificio;
    }

    // ================= AULAS =================
    public Aula agregarAula(int edificioId, String numero, int capacidad, String tipo)
            throws Exception {

        Edificio edificio = buscarPorId(edificioId);

        if (edificio == null) {
            throw new IllegalArgumentException("No existe edificio con ID " + edificioId + ".");
        }

        if (numero.isBlank()) {
            throw new IllegalArgumentException("El número del aula es obligatorio.");
        }

        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }

        if (tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo del aula es obligatorio.");
        }

        Aula aula = new Aula(0, numero, capacidad, tipo, edificio);

        if (repo instanceof EdificioRepoSql repoSql) {
            repoSql.guardarAula(aula);
        }

        edificio.agregarAula(aula);

        return aula;
    }

    public void eliminarAula(int edificioId, int aulaId) throws Exception {

        Edificio edificio = buscarPorId(edificioId);

        if (edificio == null) {
            throw new IllegalArgumentException("No existe edificio.");
        }

        Aula aula = edificio.buscarAula(aulaId);

        if (aula == null) {
            throw new IllegalArgumentException("No existe el aula.");
        }

        if (repo instanceof EdificioRepoSql repoSql) {
            repoSql.eliminarAula(aulaId);
        }

        edificio.eliminarAula(aulaId);
    }

    // ================= VALIDACIONES =================
    private void validarEdificio(String codigo, String nombre) {

        if (codigo.isBlank() || nombre.isBlank()) {
            throw new IllegalArgumentException("Código y nombre son obligatorios.");
        }
    }
}
