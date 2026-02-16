# Pasos para la práctica
Simplificado y resumido mas aun que en el pdf

### Resumen vista general:
#### Controller:
- Controller gestiona petición (alguien hace GET a un recurso), invocando un Service
#### Service:
- Cada Service tiene una interfaz (por buenas practicas, no hace nada) y una implementación
- La implementación de cada Service tiene lógica de negocio (como verificar si el departamento de un empleado
  antes de intentar actualizar ese empleado).
#### DAO y Entities
- Service usa los DAO que tenemos en la carpeta models.dao, que exponen métodos CRUD básicos
  para guardar, buscar por ID, etc. (cosas sencillas).
- Los DAO se construyen a partir de las Entities


## Paso 0 - Creación del proyecto
Crear proyecto Maven, usar el Spring 3.5.10 si es posible para ahorrarse posibles problemas técnicos.

Añadir las siguientes 5 dependencias:

- Spring Boot DevTools
- Spring Web
- Spring Data JPA
- MariaDB Driver SQL
- Thymeleaf

En el application properties poner lo siguiente.
Ojo con lo del dialect, puede dar (o no) crashes.
Cambiar los datos por las contraseñas y users reales de la base de datos que toque

```
spring.application.name=SpringThymeleaf
#Database Configuration
spring.datasource.url=jdbc:mariadb://localhost:3306/proyecto_apirest
spring.datasource.username=root
spring.datasource.password=1111
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
// Permite a Hibernate generar SQL optimizado para un DBM concreto en este caso MariaDB
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
```

## Paso 1 - Controllers, Templates y CSS
### 1.1 - Templates
Los templates (htmls dinamicos) van en:

- src/main/resources/templates/

Dentro de este directorio, crear un template (en esta práctica, index.html)

### 1.2 - Controllers
Los controllers van en un package:

- ad.springthymeleaf.controllers;

Crear un WebController, que dispensará los htmls segun la dirección en la que estemos.

Ejemplo de un WebController:

```
package ad.springthymeleaf.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // ruta indica
    @GetMapping("/")
    public String index() {
    // lo que se sirve en esa ruta (el nombre del html)
        return "index";
    }
}
```

### 1.3 CSS
Los archivos .css van en la sección de recursos estaticos:

- src/main/resources/static/

Se enlazan al html dentro del html en si, como siempre. Línea para enlazar:

```
<link rel="stylesheet" th:href="@{/styles.css}" />
```

## Paso 2 - Estructura e implementación

Se reutiliza la estructura del proyecto de API REST. Algo asi:

```
java
| ad.springthymeleaf
| - controllers
| - models
| - - dao
| - - entities
| - services
| SpringThymeleafApplication.java
resources
| - static
| - templates
| application.properties
```

Asi que se copia y pega lo de la práctica anterior: models (dao y entities) y services (logica)

Copiar y pegar los models en una carpeta equivalente del proyecto, ajustar los nombres de paquetes e imports
al del proyecto actual.

#### Añadir también la dependencia de validación de datos de Spring, y la de ModelMapper en el pom.xml

(recuerda pulsar el botón de reload dependencies)

```
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
<groupId>org.modelmapper</groupId>
<artifactId>modelmapper</artifactId>
<version>3.2.2</version>
</dependency>
```

Una vez porteado todo, proseguimos, pag 14-15

## Paso 3 - Peticiones GET: VerDepartamentos, Interfaces en Services, y HTML dinámico.

### 3.1 - Ver departamentos en WebController
Metemos el template html verdepartamentos y su get asociado en nuestro controlador WebController:

```
@GetMapping("/verdepartamentos")
public String mostrarDepartamentos(Model model) {
    return "verdepartamentos";
}
```

### 3.2 - Interfaces y creación de Services
Creamos en el paquete services una interfaz IDepartamentosService, que contiene una declaración de
los métodos de la interfaz (sin implementación, porque es una interfaz).

```
package ad.springthymeleaf.services;

import ad.springthymeleaf.models.entities.DepartamentoEntity;
import java.util.List;

public interface IDepartamentosService {
    public List<DepartamentoEntity> obtenerDepartamentos();
}
```

A través del intelliJ, more actions -> implement interface, creamos una clase que implementa
esos métodos. Le quitamos la I de delante del nombre.

Luego la anotamos con @Service (pq es una clase de servicios), y le damos un:
```
private final IDepartamentoEntityDAO departamentoEntityDao
```
para tener acceso al DAO de los departamentos. Creamos también el constructor para que funcione
la inyección de dependencias.

Se nos queda algo asi:

```
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
}
```

Luego le damos un
```
private final DepartamentosServiceImpl departamentosService
```

Al WebController, y le creamos el constructor asociado. Asi podremos acceder a los departamentos
desde el controlador a través del Service.

Asi que implementamos el resto del metodo de ver departamentos del controller.

```
@GetMapping("/verdepartamentos")
public String mostrarDepartamentos(Model model) {

    //obtenemos los departamentos de la capa DepartamentosServiceImpl
    List<DepartamentoEntity> departamentos = departamentosService.obtenerDepartamentos();

    //La clase Model nos ofrece addAtribute para enviar variables/atributos a laplantilla
    model.addAttribute("departamentos", departamentos);

    return "verdepartamentos";
}
```

## Paso 4 - Peticiones POST
Es similar en ciertos aspectos. Pasos a seguir que podemos pasar rapido para crear una vista de
dar de alta departamentos:
- Crear el template de la página correspondiente
- Crear el metodo get en el controlador para la vista de alta departamento (implementación:)
```
//Accedemos por GET al endpoint /altadepartamento
@GetMapping("/altadepartamento")
public String altaDepartamento(Model model) {
    
    //Pasamos al modelo una DepartamentoEntity vacío
    model.addAttribute("departamento", new DepartamentoEntity());
    
    return "altadepartamento";
}
```
- Crear el endpoint para la petición POST en el controller (implementación:)
```
//Accedemos por POST al endpoint /altadepartamento
@PostMapping("/altadepartamento")
public String crearDepartamento(@ModelAttribute DepartamentoEntity departamento, Model
        model) {
    departamentosService.guardarDepartamento(departamento);
    return "altadepartamento";
}
```
- Implementar el método de .guardarDepartamento(departamento) :
- Definir el método en la interfaz IDepartamentosService
``` 
Optional<DepartamentoEntity> guardarDepartamento(DepartamentoEntity departamento);
```
- Implementar metodo en DepartamentosServiceImpl:
```
@Override
public Optional<DepartamentoEntity> guardarDepartamento(DepartamentoEntity departamento) {
    return Optional.of(departamentoEntityDAO.save(departamento));
}
```

Ya sería funcional. Un detalle adicional que se añade en esta practica es el uso de notificacion
de departamento creado con éxito.

Para lograr esto, metes algo asi en el template:
```
<div th:if="${mensaje}">
    <p th:text="${mensaje}"></p>
</div>
```

Y modificas el POST en el WebController, comprobando que se crea el departamento y mostrando mensaje:

```
//Accedemos por POST al endpint /altadepartamento
@PostMapping("/altadepartamento")
public String crearDepartamento(@ModelAttribute DepartamentoEntity departamento, Model model) {
    // comrprueba si se ha creado correctamente
    if (departamentosService.guardarDepartamento(departamento).isPresent()) {
        model.addAttribute("mensaje", "Departamento creado correctamente");
    }
    else {
        model.addAttribute("mensaje", "Error al crear el departamento.");
    }
    return "altadepartamento";
}
```

Tambien se puede ajustar dinamicamente la clase CSS a la que pertenece el mensaje para poder cambiarle
el color al mensaje. Se hace asi (con el addAttribute):

```
//Accedemos por POST al endpint /altadepartamento
@PostMapping("/altadepartamento")
public String crearDepartamento(@ModelAttribute DepartamentoEntity departamento, Model model) {
    if (departamentosService.guardarDepartamento(departamento).isPresent()) {
        model.addAttribute("tipo_operacion", "ok");
        model.addAttribute("mensaje", "Departamento creado correctamente");
        }
    else {
        model.addAttribute("tipo_operacion", "error");
        model.addAttribute("mensaje", "Error al crear el departamento.");
    }
    return "altadepartamento";
}
```

## Paso 5 - Pasar parámetros
Vamos a ponerle un botón "Detalles" a cada departamento que enlaza a una vista detalle del departamento
asociado. Para lo cual, hay que generar dinámicamente el hipervínculo para cada departamento.

En el template de verdepartamento, se añade esta línea:

```
<td><a th:href="${'/verdepartamento?id=' + departamento.id}">Detalles</a>
```
Obviamente hará falta un template correspondiente, implementar los metodos en el service (interfaz y
en la clase que la implementa), y un endpoint correspondiente.

#### La implementación del endpoint:
````
@GetMapping("/verdepartamento")
public String verdepartamento(Model model, @RequestParam(name="id", required = true) int id)
{
    Optional<DepartamentoEntity> departamentoEntityOptional =
            departamentosService.obtenerDepartamentoById(id);
    if (departamentoEntityOptional.isEmpty()) {
        model.addAttribute("titulo", "Error");
        model.addAttribute("mensaje", "No se encontró el departamento con el id " + id);
        return "error";
    }
    model.addAttribute("departamento", departamentoEntityOptional.get());
    return "verdepartamento";
}
````
#### La implementacion en la clase que implementa la interfaz.
```
@Override
public Optional<DepartamentoEntity> obtenerDepartamentoById(int id) {
    return departamentoEntityDAO.findById(id);
}
```

## Paso 6 - Usar filtros en las búsquedas
Vamos a implementar una funcionalidad de filtrado mediante dropdown en la lista de departamentos.
Esto permitirá filtrar por ubicaciones.

### 6.1 - Obtener los datos necesarios para filtrar
Como queremos hacer un dropdown para ubicaciones, necesitamos obtener todas las ubicaciones, sin
repetir (DISTINCT).

Es relativamente sencillo, los pasos son:
- Crear la query personalizada en IDepartamentoEntityDAO (lo que extiende de CrudRepository) para
obtener los departamentos con distinct, y para encontrar los departamentos según su ubicación:
```
@Repository
public interface IDepartamentoEntityDAO extends CrudRepository<DepartamentoEntity, Integer>
{
    @Query("select DISTINCT D.ubicacion from DepartamentoEntity D")
    List<String> findAllUbicaciones();

    List<DepartamentoEntity> findDepartamentoEntitiesByUbicacion(String ubicacion);
}
```
- Declarar los métodos en la interfaz IDepartamentosService, y luego implementarlos en
DepartamentosServiceImp:
```
// en IDepartamentosService:
    List<DepartamentoEntity> obtenerDepartamentosPorUbicacion(String ubicacion);

// en DepartamentosServiceImpl:
    @Override
    public List<String> obtenerUbicaciones() {
        return departamentoEntityDAO.findAllUbicaciones();
    }

    @Override
    public List<DepartamentoEntity> obtenerDepartamentosPorUbicacion(String ubicacion) {
        return departamentoEntityDAO.findDepartamentoEntitiesByUbicacion(ubicacion);
    }
```
- Ajustar el endpoint de ver departamentos para que obtenga la información y que pueda filtrar según
si se le pasa el parámetro opcional o no:
```
@GetMapping("/verdepartamentos")
public String mostrarDepartamentos(Model model, @RequestParam(name = "ubicacion", required = false) String ubicacion) {

    //obtenemos los departamentos de la capa DepartamentosServiceImpl
    List<DepartamentoEntity> departamentos = departamentosService.obtenerDepartamentos();

    //Obtenemos una lista de ubicaciones de departamentos sin repetir (usando DISTINCT)
    List<String> ubicaciones = departamentosService.obtenerUbicaciones();

    model.addAttribute("ubicaciones", ubicaciones);

    if (ubicacion != null) {
        departamentos = departamentosService.obtenerDepartamentosPorUbicacion(ubicacion);
    }
    // La clase Model nos ofrece addAtribute para enviar variables/atributos a la plantilla
    model.addAttribute("departamentos", departamentos);
    return "verdepartamentos";
}
```

- Poner en el html de verdepartamentos el dropdown con el javascript necesario:
```
// justo debajo del link al stylesheet
<script>
    function cambiarUbicacion() {
        var ubicacion = document.getElementById("selectUbicacion").value;
        if (ubicacion!=="all")
            window.location.href="/verdepartamentos?ubicacion=" + ubicacion;
        else
            window.location.href="/verdepartamentos";
    }
</script>

/// y esto otro donde el h1

<h1>Lista de Departamentos</h1>
<select id="selectUbicacion" onchange="cambiarUbicacion()">
    <option value="">Selecciona una ubicación</option>
    <option value="all">Todas</option>
    <option th:each="ubicacion : ${ubicaciones}"
            th:value="${ubicacion}"
            th:text="${ubicacion}">
    </option>
</select>
```