package ad.springthymeleaf.services;

import ad.springthymeleaf.models.dao.IDepartamentoEntityDAO;
import ad.springthymeleaf.models.entities.DepartamentoEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentosServiceImpl implements IDepartamentosService {
    private final IDepartamentoEntityDAO departamentoEntityDAO;

    public DepartamentosServiceImpl(IDepartamentoEntityDAO departamentoEntityDAO) {
        this.departamentoEntityDAO = departamentoEntityDAO;
    }

    @Override
    public List<DepartamentoEntity> obtenerDepartamentos() {
        return (List<DepartamentoEntity>) departamentoEntityDAO.findAll();
    }

    @Override
    public Optional<DepartamentoEntity> guardarDepartamento(DepartamentoEntity departamento) {
        return Optional.of(departamentoEntityDAO.save(departamento));
    }

    @Override
    public Optional<DepartamentoEntity> obtenerDepartamentoById(int id) {
        return departamentoEntityDAO.findById(id);
    }

    @Override
    public List<String> obtenerUbicaciones() {
        return departamentoEntityDAO.findAllUbicaciones();
    }

    @Override
    public List<DepartamentoEntity> obtenerDepartamentosPorUbicacion(String ubicacion) {
        return departamentoEntityDAO.findDepartamentoEntitiesByUbicacion(ubicacion);
    }

}