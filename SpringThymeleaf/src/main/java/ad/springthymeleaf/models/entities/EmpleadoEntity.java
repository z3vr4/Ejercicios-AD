package ad.springthymeleaf.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "empleados", schema = "proyecto_apirest")
public class EmpleadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 10)
    private String nombre;

    @Column(name = "puesto", length = 15)
    private String puesto;

    // many to one de empleados a departamentos
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DepartamentoEntity departamento;

    // getters y setters genericos
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

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    // el getter que devuelve el departamento en empleados debe devolver un objeto de tipo DepartamentoEntity
    public DepartamentoEntity getDepartamento() {
        return departamento;
    }

    // y el setter debe tomar un DepartamentoEntity también
    public void setDepartamento(DepartamentoEntity departamento) {
        this.departamento = departamento;
    }
}