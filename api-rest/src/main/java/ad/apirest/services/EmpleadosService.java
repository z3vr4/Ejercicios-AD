package ad.apirest.services;


import ad.apirest.models.dao.IDepartamentoEntityDAO;
import ad.apirest.models.dao.IEmpleadoEntityDAO;
import ad.apirest.models.dto.EmpleadoDTO;
import ad.apirest.models.entities.DepartamentoEntity;
import ad.apirest.models.entities.EmpleadoEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadosService {

    private final IEmpleadoEntityDAO empleadoEntityDAO;
    private final IDepartamentoEntityDAO departamentoEntityDAO;
    private final ModelMapper mapper;

    public EmpleadosService(IEmpleadoEntityDAO empleadoEntityDAO,
                            IDepartamentoEntityDAO departamentoEntityDAO) {
        this.empleadoEntityDAO = empleadoEntityDAO;
        this.departamentoEntityDAO = departamentoEntityDAO;

        // model mapper
        this.mapper = new ModelMapper();

        // correccion para campos que tienen el mismo nombre
        // especificamente, le dice de saltar el siguiente mapeo
        // para no sobreescribir el nombre ya escrito
        this.mapper.typeMap(
                        DepartamentoEntity.class, EmpleadoDTO.class)
                .addMappings(mapping -> mapping.skip(EmpleadoDTO::setNombre));
    }

    public List<EmpleadoEntity> buscarEmpleados() {
        return (List<EmpleadoEntity>) empleadoEntityDAO.findAll();
    }

    // Metodo auxiliar
    private boolean existeDepartamento(EmpleadoEntity empleado) {
        // si el empleado tiene departamento asociado (no null)
        // y el departamento existe (comprobando por ID)
        return empleado.getDepartamento() != null
                && departamentoEntityDAO.existsById(
                empleado.getDepartamento().getId()
        );
    }

    // Devuelve un empleado por id
    public Optional<EmpleadoEntity> buscarEmpleadoPorId(int id) {
        return empleadoEntityDAO.findById(id);
    }

    // Devuelve un empleado DTO con los datos del departamento
    public Optional<EmpleadoDTO> buscarEmpleadoDTOporId(int id) {

        Optional<EmpleadoEntity> empleadoEntityOptional =
                empleadoEntityDAO.findById(id);

        if (empleadoEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        EmpleadoEntity empleado = empleadoEntityOptional.get();

        EmpleadoDTO empleadoDTO = mapper.map(empleado, EmpleadoDTO.class);

        Optional<DepartamentoEntity> departamentoEntityOptional =
                departamentoEntityDAO.findById(
                        empleado.getDepartamento().getId()
                );

        if (departamentoEntityOptional.isPresent()) {
            DepartamentoEntity departamento =
                    departamentoEntityOptional.get();
            mapper.map(departamento, empleadoDTO);
        }

        return Optional.of(empleadoDTO);
    }

    // Guarda una entidad empleado
    public boolean guardarEmpleado(EmpleadoEntity empleado) {

        // verifica que el campo departamento no sea null
        // y que el departamento exista
        if (!existeDepartamento(empleado)) {
            return false;
        }

        empleadoEntityDAO.save(empleado);
        return true;
    }

    // Actualiza un empleado según id
    public boolean actualizarEmpleado(int id, EmpleadoEntity empleado) {

        // comprueba que el departamento al que pertenece existe
        if (!existeDepartamento(empleado)) {
            return false;
        }

        // busca el empleado que actualizar
        if (!empleadoEntityDAO.existsById(id)) {
            return false;
        }

        // El empleado que viene en el body del PUT REQUEST no tendrá id
        // ya que se genera automáticamente por lo que debemos asignarle ese id
        // para que no inserte un nuevo empleado.
        empleado.setId(id);
        empleadoEntityDAO.save(empleado);

        return true;
    }

    // Borra un empleado especificado por ID
    public boolean borrarEmpleado(int id) {

        if (!empleadoEntityDAO.existsById(id)) {
            return false;
        }

        empleadoEntityDAO.deleteById(id);
        return true;
    }
}
