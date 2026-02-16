package ad.springthymeleaf.services;


import ad.springthymeleaf.models.entities.DepartamentoEntity;
import java.util.List;
import java.util.Optional;


public interface IDepartamentosService {
    List<DepartamentoEntity> obtenerDepartamentos();
    Optional<DepartamentoEntity> guardarDepartamento(DepartamentoEntity departamento);
    Optional<DepartamentoEntity> obtenerDepartamentoById(int id);
    List<String> obtenerUbicaciones();
    List<DepartamentoEntity> obtenerDepartamentosPorUbicacion(String ubicacion);
}