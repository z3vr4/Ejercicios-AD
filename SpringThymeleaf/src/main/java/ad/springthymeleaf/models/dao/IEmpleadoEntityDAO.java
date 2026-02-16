package ad.springthymeleaf.models.dao;

import ad.springthymeleaf.models.entities.EmpleadoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Bean Repository que extiende de CrudRepository
@Repository
public interface IEmpleadoEntityDAO extends CrudRepository<EmpleadoEntity, Integer> {

}
