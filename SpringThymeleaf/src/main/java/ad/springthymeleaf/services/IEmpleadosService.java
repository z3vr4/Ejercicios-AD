package ad.springthymeleaf.services;

import ad.springthymeleaf.models.dto.EmpleadoDTO;
import ad.springthymeleaf.models.entities.EmpleadoEntity;

import java.util.List;
import java.util.Optional;

public interface IEmpleadosService {

    List<EmpleadoEntity> buscarEmpleados();

    Optional<EmpleadoEntity> buscarEmpleadoPorId(int id);

    Optional<EmpleadoDTO> buscarEmpleadoDTOporId(int id);

    boolean guardarEmpleado(EmpleadoEntity empleado);

    boolean actualizarEmpleado(int id, EmpleadoEntity empleado);

    boolean borrarEmpleado(int id);
}
