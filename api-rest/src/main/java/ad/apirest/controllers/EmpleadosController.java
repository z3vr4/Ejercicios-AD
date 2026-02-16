package ad.apirest.controllers;

import ad.apirest.models.dto.EmpleadoDTO;
import ad.apirest.models.entities.EmpleadoEntity;
import ad.apirest.services.EmpleadosService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController     // para establecer que es API REST
@RequestMapping("/empleados")       // marca el endpoint
public class EmpleadosController {

    private final EmpleadosService empleadosService;

    public EmpleadosController(EmpleadosService empleadosService) {
        this.empleadosService = empleadosService;
    }

    // Metodo HTTP GET (/empleados)
    // Cuando accedamos a ese endpoint, devolverá un listado de empleados
    @GetMapping
    public List<EmpleadoEntity> findAllEmpleados() {
        return empleadosService.buscarEmpleados();
    }

    // Metodo HTTP GET (/empleados/id)
    // Cuando accedamos a ese endpoint, devolverá el empleado cuyo id
    // sea el que esté dado de alta
    // Debemos comprobar que el empleado existe
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoEntity> findEmpleadoById(
            @PathVariable(value = "id") int id) {

        return empleadosService.buscarEmpleadoPorId(id)
                .map(empleado -> ResponseEntity.ok().body(empleado))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /empleados/dto/id
    // devuelve un empleado Y los datos del departamento al que pertenece
    @GetMapping("/dto/{id}")
    public ResponseEntity<EmpleadoDTO> findEmpleadoDTOById(
            @PathVariable(value = "id") int id) {

        return empleadosService.buscarEmpleadoDTOporId(id)
                .map(dto -> ResponseEntity.ok().body(dto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Metodo POST (/empleados)
    // Guarda una entidad empleado
    @PostMapping
    public ResponseEntity<?> saveEmpleado(
            @Valid
            @RequestBody EmpleadoEntity empleado) {

        boolean guardado = empleadosService.guardarEmpleado(empleado);

        if (!guardado) {
            // devuelve bad request
            return ResponseEntity.badRequest().build();
        }

        // devuelve ok y guarda
        return ResponseEntity.ok().body(empleado);
    }

    // Metodo PUT (/empleados/id)
    // Actualiza un empleado según id
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEmpleado(
            @RequestBody EmpleadoEntity empleado,
            @PathVariable(value = "id") int id) {

        boolean actualizado =
                empleadosService.actualizarEmpleado(id, empleado);

        if (!actualizado) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .body("Empleado actualizado correctamente");
    }

    // Metodo DELETE (/empleados/id)
    // borra un empleado especificado por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpleado(
            @PathVariable(value = "id") int id) {

        boolean borrado = empleadosService.borrarEmpleado(id);

        if (!borrado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .body("Empleado borrado correctamente");
    }
}
