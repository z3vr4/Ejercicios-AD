package ad.springthymeleaf.models.dao;

import ad.springthymeleaf.models.entities.DepartamentoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDepartamentoEntityDAO extends CrudRepository<DepartamentoEntity, Integer>
{
    @Query("select DISTINCT D.ubicacion from DepartamentoEntity D")
    List<String> findAllUbicaciones();

    List<DepartamentoEntity> findDepartamentoEntitiesByUbicacion(String ubicacion);

}