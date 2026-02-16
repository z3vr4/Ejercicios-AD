package ad.apirest.controllers;


import ad.apirest.models.entities.DepartamentoEntity;
import ad.apirest.services.DepartamentosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentosController {

    private final DepartamentosService departamentosService;

    public DepartamentosController(DepartamentosService departamentosService) {
        this.departamentosService = departamentosService;
    }

    // Metodo HTTP GET (/departamentos)
    // Devuelve el listado de departamentos
    @GetMapping
    public List<DepartamentoEntity> findAllDepartamentos() {
        return departamentosService.buscarDepartamentos();
    }

    // Metodo HTTP GET (/departamentos/id)
    // Devuelve un departamento por id
    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoEntity> findDepartamentoById(
            @PathVariable(value = "id") int id) {

        return departamentosService.buscarDepartamentoPorId(id)
                .map(departamento -> ResponseEntity.ok().body(departamento))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Metodo POST (/departamentos)
    // Guarda un departamento
    @PostMapping
    public ResponseEntity<?> saveDepartamento(
            @RequestBody DepartamentoEntity departamento) {

        boolean guardado = departamentosService.guardarDepartamento(departamento);

        if (!guardado) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(departamento);
    }

    // Metodo PUT (/departamentos/id)
    // Actualiza un departamento por id
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDepartamento(
            @RequestBody DepartamentoEntity departamento,
            @PathVariable(value = "id") int id) {

        boolean actualizado =
                departamentosService.actualizarDepartamento(id, departamento);

        if (!actualizado) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .body("Departamento actualizado correctamente");
    }

    // Metodo DELETE (/departamentos/id)
    // Borra un departamento por id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartamento(
            @PathVariable(value = "id") int id) {

        boolean borrado = departamentosService.borrarDepartamento(id);

        if (!borrado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .body("Departamento borrado correctamente");
    }
}
