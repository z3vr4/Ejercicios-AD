package ad.apirest.models.dao;

import ad.apirest.models.entities.DepartamentoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDepartamentoEntityDAO extends CrudRepository<DepartamentoEntity, Integer>
{
}