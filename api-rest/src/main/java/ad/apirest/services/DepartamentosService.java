package ad.apirest.services;

import ad.apirest.models.dao.IDepartamentoEntityDAO;
import ad.apirest.models.entities.DepartamentoEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentosService {

    private final IDepartamentoEntityDAO departamentoEntityDAO;

    public DepartamentosService(IDepartamentoEntityDAO departamentoEntityDAO) {
        this.departamentoEntityDAO = departamentoEntityDAO;
    }

    // Devuelve todos los departamentos
    public List<DepartamentoEntity> buscarDepartamentos() {
        return (List<DepartamentoEntity>) departamentoEntityDAO.findAll();
    }

    // Devuelve un departamento por id
    public Optional<DepartamentoEntity> buscarDepartamentoPorId(int id) {
        return departamentoEntityDAO.findById(id);
    }

    // Guarda un departamento
    public boolean guardarDepartamento(DepartamentoEntity departamento) {

        // En este caso no hay dependencias externas que validar
        departamentoEntityDAO.save(departamento);
        return true;
    }

    // Actualiza un departamento según id
    public boolean actualizarDepartamento(int id, DepartamentoEntity departamento) {

        if (!departamentoEntityDAO.existsById(id)) {
            return false;
        }

        // Asignar id para evitar inserción
        departamento.setId(id);
        departamentoEntityDAO.save(departamento);

        return true;
    }

    // Borra un departamento por id
    public boolean borrarDepartamento(int id) {

        if (!departamentoEntityDAO.existsById(id)) {
            return false;
        }

        departamentoEntityDAO.deleteById(id);
        return true;
    }
}
