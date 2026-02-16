package ad.springthymeleaf.services;
import ad.springthymeleaf.models.dao.IDepartamentoEntityDAO;
import ad.springthymeleaf.models.dao.IEmpleadoEntityDAO;
import ad.springthymeleaf.models.dto.EmpleadoDTO;
import ad.springthymeleaf.models.entities.DepartamentoEntity;
import ad.springthymeleaf.models.entities.EmpleadoEntity;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadosService {
    private final IEmpleadoEntityDAO empleadoEntityDAO;
    private final IDepartamentoEntityDAO departamentoEntityDAO;

    public EmpleadosService(IEmpleadoEntityDAO empleadoEntityDAO, IDepartamentoEntityDAO departamentoEntityDAO) {
        this.empleadoEntityDAO = empleadoEntityDAO;
        this.departamentoEntityDAO = departamentoEntityDAO;
    }

    //Método HTTP GET (/empleados)
    //Cuando accedamos a ese endpoint, devolverá un listado de empleados
    @GetMapping
    public List<EmpleadoEntity> findAllEmpleados() {
        return (List<EmpleadoEntity>) empleadoEntityDAO.findAll();
    }

    boolean existeDepartamento(EmpleadoEntity empleado) {
        return (empleado.getDepartamento() != null) &&
                departamentoEntityDAO.existsById(empleado.getDepartamento().getId());
    }

    //Método HTTP GET (/empleados/id)
    //Cuando accedamos a ese endpoint, devolverá el empleado cuyo id (empno) sea el que esté dado de alta
    // Debemos comprobar que el empleado existe
    @GetMapping ("/{id}")
    public ResponseEntity<EmpleadoEntity> findEmpleadoById(@PathVariable(value = "id") int id) {
        Optional<EmpleadoEntity> empleadoOpt = empleadoEntityDAO.findById(id);

        if (empleadoOpt.isPresent()) {
            return ResponseEntity.ok().body(empleadoOpt.get());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    //Método POST (/empleados)
    @PostMapping
    public ResponseEntity<?> saveEmpleado(@Valid @RequestBody EmpleadoEntity empleado) {

        if ( existeDepartamento(empleado) )
            return ResponseEntity.ok().body(empleadoEntityDAO.save(empleado));
        else
            return ResponseEntity.badRequest().build();
    }

    //Método DELETE (/empleados)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpleado(@PathVariable(value = "id") int id) {
        if(empleadoEntityDAO.existsById(id)) {
            empleadoEntityDAO.deleteById(id);
            return ResponseEntity.ok().body("Empleado borrado correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Método PUT (/empleados/id)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEmpleado(@Valid @RequestBody EmpleadoEntity empleado,
                                                @PathVariable(value = "id") int id) {
        Optional<EmpleadoEntity> empleadoEntityOptional = empleadoEntityDAO.findById(id);
        if (empleadoEntityOptional.isPresent()) {
//El empleado que viene en el body del PUT REQUEST no tendrá id
// ya que se genera automáticamente por lo que debemos asignarle ese id
// para que no inserte un nuevo empleado.
            empleado.setId(id);
            if (existeDepartamento(empleado)) {
                empleadoEntityDAO.save(empleado);
                return ResponseEntity.ok().body("Empleado actualizado correctamente");
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //GET /dto/id
    @GetMapping ("/dto/{id}")
    public ResponseEntity<EmpleadoDTO> findEmpleadoDTOById(@PathVariable(value = "id") int
                                                                   id) {
        Optional<EmpleadoEntity> empleadoEntityOptional = empleadoEntityDAO.findById(id);
        if (empleadoEntityOptional.isPresent()) {
            EmpleadoEntity empleado = empleadoEntityOptional.get();
            Optional<DepartamentoEntity> departamentoEntityOptional =
                    departamentoEntityDAO.findById(empleado.getDepartamento().getId());
            EmpleadoDTO empleadoDTO = new EmpleadoDTO();
            empleadoDTO.setId(empleado.getId());
            empleadoDTO.setNombre(empleado.getNombre());
            empleadoDTO.setPuesto(empleado.getPuesto());
            empleadoDTO.setDepartamentoId(empleado.getDepartamento().getId());
            empleadoDTO.setDepartamentoNombre("");
            empleadoDTO.setDepartamentoUbicacion("");
            if (departamentoEntityOptional.isPresent()) {
                DepartamentoEntity departamento = departamentoEntityOptional.get();
                empleadoDTO.setDepartamentoNombre(departamento.getNombre());
                empleadoDTO.setDepartamentoUbicacion(departamento.getUbicacion());
            }
            return ResponseEntity.ok().body(empleadoDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //GET /dto2/id con ModelMapper
    @GetMapping ("/dto2/{id}")
    public ResponseEntity<EmpleadoDTO> findEmpleadoDTO2ById(@PathVariable(value = "id") int id) {
        Optional<EmpleadoEntity> empleadoEntityOptional = empleadoEntityDAO.findById(id);
        if (empleadoEntityOptional.isPresent()) {
            EmpleadoEntity empleado = empleadoEntityOptional.get();
            Optional<DepartamentoEntity> departamentoEntityOptional =
                    departamentoEntityDAO.findById(empleado.getDepartamento().getId());
            ModelMapper mapper = new ModelMapper();
            EmpleadoDTO empleadoDTO = mapper.map(empleado, EmpleadoDTO.class);

            if (departamentoEntityOptional.isPresent()) {
                mapper.typeMap(
                                DepartamentoEntity.class, EmpleadoDTO.class).
                        addMappings(
                                maping -> maping.skip(EmpleadoDTO::setNombre));
            }

            return ResponseEntity.ok().body(empleadoDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
