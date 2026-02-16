package ad.apirest.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "departamentos", schema = "proyecto_apirest")
public class DepartamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 14)
    private String nombre;

    @Column(name = "ubicacion", length = 13)
    private String ubicacion;

    // asi es como establecer un OneToMany.
    @OneToMany(mappedBy = "departamento")
    @JsonIgnore
    private Set<ad.apirest.models.entities.EmpleadoEntity> empleados = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    // getter del one to many
    public Set<ad.apirest.models.entities.EmpleadoEntity> getEmpleados() {
        return empleados;
    }

    // setter del one to many
    public void setEmpleados(Set<ad.apirest.models.entities.EmpleadoEntity> empleados) {
        this.empleados = empleados;
    }
}
