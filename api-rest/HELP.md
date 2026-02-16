
# Readme del proceso
Documentación del proceso realizado y teoría.

## 1 - Creación del proyecto
Crear proyecto en intellij. Marcar:
- java
- maven
  En dependencias:
- mariadb driver

Pasar de lo de usar la página web.

## 2 - Creación de la database
Pillar el script y ejecutarlo en el SGDB que toque.

## 3 - Configuración
### Application.properties
En el application properties, poner esto:
```
 #URL desde dode accederemos al API: http://localhost:8080/api-rest

server.servlet.context-path=/api-rest

 #Database Configuration

spring.datasource.url=jdbc:mariadb://localhost:3306/ut8_empleados
spring.datasource.username=severo_ad
spring.datasource.password=severo_ad
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

 #Hibernate ddl auto (create, create-drop, update): con «update» el esquema de la base de datos se actualizará automáticamente de acuerdo con las entidades java encontradas en el proyecto

spring.jpa.hibernate.ddl-auto=update

 #Permite a Hibernate generar SQL optimizado para un DBM concreto en este caso MariaDB
 #ATENCION!!!!! ESTO CAUSA ERROR EN SPRING MODERNO; NO PONER ( A LO MEJOR )
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDB103Dialect
```


### Pom.xml
En el pom, asegurarse de que están estas dependencies:

```
<dependencies>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
<groupId>org.mariadb.jdbc</groupId>
<artifactId>mariadb-java-client</artifactId>
<scope>runtime</scope>
</dependency>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-test</artifactId>
<scope>test</scope>
</dependency>
</dependencies>
```

## 4 - Creación de la estructura MVC
Crear esta estructura de paquetes:
- ad.apirest
- ad.apirest.controllers
- ad.apirest.models.dao
- ad.apirest.models.entities

## 5 - Creación de entidades EmpleadoEntity y DepartamentoEntity
Añadimos datasource de mariadb (la base de datos que hemos creado).

Esto permite usar (con click derecho en el datasource) lo de "Create JPA Entities from DB".

Las entities las guardaremos en models.entities.

Es posible que falte alguna relación de many to one o lo que sea, se escriben a mano entonces.
En este proyecto hay ejemplos de one to many, puede que incluya un many to many.

### 5.1 - Json Ignore
Los campos interrelacionados pueden crear bucle infinito que haga que no funcione bien. Aqui template de arreglar el one
to many (añadir JsonIgnore):
```
    @OneToMany(mappedBy = "departamento")
    @JsonIgnore
    private Set<ad.apirest.models.entities.EmpleadoEntity> empleados = new LinkedHashSet<>();
```
Y aquí el template de arreglar el many to one (Añadir JsonIgnoreProperties...):

```
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DepartamentoEntity departamento;
```
## 6 - Creación de las clases de repositorio
Hay que implementar CrudRepository, que ofrecerá funcionalidad básica para acceder a todas las entidades de datos del
modelo.

Repositorio = DAO, básicamente

En la práctica dice de usar la faceta hibernate, en Spring moderno al parecer no hace falta.

Creamos interfaces (ojo, interfaces, no class) que extiendan de CrudRepository y las anotamos como @Repository

CrudRepository declara métodos como findAll(), findOne() y save() que constituyen la funcionalidad CRUD
básica de un repositorio. En concreto, los métodos que puede llamar algo que extienda de CrudRepository son:
```
- save()        // guarda una entidad en la base de datos
- saveAll()     // guarda una serie de entidades en la base de datos
- findById()    // busca por ID y devuelve objeto
- existsById()  // devuelve bool si existe algo con ID matching
- findAll()     // encuentra todas las filas con X criterio
- findAllById() // encuentra todas las filas con ID X
- count()       // hace recuento
- deleteById()  // borra por ID
- delete()      // borra la entidad pasada
- deleteAllById // borra todas las ID matching
- deleteAll     // puede pasarsele una serie de entidades o borrar todo directamente
```
## 7 - Creación del controlador
En el paquete controllers creamos la clase EmpleadosController.

La anotamos con @RestController y @RequestMapping("/endpoint") (cambiar "endpoint" por el endpoint real, por ejemplo
empleados).

Hay que definir una variable interna para asociarle la interfaz CrudRepository asociada al controlador y definir un
constructor (por la inyección de dependencias).

### 7.1 - Definición de metodos del controlador
#### 7.1.1 - Método get sin parámetros adicionales
Método get sin parámetros adicionales
- anotar con @GetMapping
- usar el crudrepository asociado para obtener lo que se busque (findAll, findById, etc.)
- truquito java para conversion, convertir iterable a lista: (List\<DepartamentoEntity\>) departamentoEntityDAO.findAll();

```
// Metodo HTTP GET (/empleados)
// Cuando accedamos a ese endpoint, devolverá un listado de empleados
@GetMapping
public List<EmpleadoEntity> findAllEmpleados() {
return (List<EmpleadoEntity>) empleadoEntityDAO.findAll();
}
```

#### 7.1.2 - Método get con parámetros
Método get con parámetros adicionales (id)
- anotar con @GetMapping("/{parametro}")
- devolver usando ResponseEntity

Template:
```
// Metodo HTTP GET (/empleados/id)
// Cuando accedamos a ese endpoint, devolverá el empleado cuyo id (empno) sea el que esté dado de alta
// Debemos comprobar que el empleado existe
@GetMapping("/{id}")
public ResponseEntity<EmpleadoEntity> findEmpleadoById(@PathVariable(value = "id") int id) {

    // comprueba si existe el empleado a través de la interfaz DAO que tenemos
    Optional<EmpleadoEntity> empleadoOpt = empleadoEntityDAO.findById(id);

    // lo devuelve o devuelve respuesta de not found
    if (empleadoOpt.isPresent()) {
        return ResponseEntity.ok().body(empleadoOpt.get());
    } else {
        return ResponseEntity.notFound().build();
    }
}
```


#### 7.1.3 - Método post
Metodo post (template, adaptar según necesidad):
```
// Metodo POST (/empleados)
// Guarda una entidad empleado
@PostMapping
public EmpleadoEntity saveEmpleado(@RequestBody EmpleadoEntity empleadoEntity) {
    return empleadoEntityDAO.save(empleadoEntity);
}
```
#### 7.1.4 - Método delete
Template, ajustar segun necesario:
```
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteEmpleado(@PathVariable(value = "id") int id) {
    if(empleadoEntityDAO.existsById(id)) {
        empleadoEntityDAO.deleteById(id);
        return ResponseEntity.ok().body("Empleado borrado correctamente");
    } else {
        return ResponseEntity.notFound().build();
    }
}
```
#### 7.1.5 - Método PUT
Template, ajustar según necesario:

```
@PutMapping("/{id}")
public ResponseEntity<?> actualizarEmpleado(@RequestBody EmpleadoEntity empleado,
                                            @PathVariable(value = "id") int id) {

    // busca el empleado que actualizar
    Optional<EmpleadoEntity> empleadoEntityOptional = empleadoEntityDAO.findById(id);
    if (empleadoEntityOptional.isPresent()) {
        // El empleado que viene en el body del PUT REQUEST no tendrá id
        // ya que se genera automáticamente por lo que debemos asignarle ese id
        // para que no inserte un nuevo empleado.
        empleado.setId(id);
        empleadoEntityDAO.save(empleado);
        return ResponseEntity.ok().body("Empleado actualizado correctamente");
    } else {
        return ResponseEntity.notFound().build();
    }
}
```
Con esto ya se puede iniciar una API REST basica.

## 8 - Testing
### 8.1 - IntelliJ - Funcionalidad Integrada
IntelliJ tiene varias funcionalidades integradas. Entre ellas, un icono al lado de cada endpoint para generar requests
y ver lo que pasa mediante un cliente HTTP integrado. (De esto habla entre las páginas 27 a 34)

Las solicitudes se generan en un archivo que permite ejecutar cada endpoint y ver lo que se devuelve.

## 8.2 - Postman

## 9 - Error proofing y enlazado de DAO
A partir de las páginas 37.

Para prevenir inserciones que den error (y capturar el error), es posible que haga falta poder comprobar los datos
que apuntan a una clave foránea.

En el caso de este proyecto, por ejemplo:
- Al insertar un empleado, se inserta un departamento
- Como comprobamos que ese departamento existe?

Pues se añade como variable privada. En este caso, añadimos un:

```
private final IDepartamentoEntityDAO departamentoEntityDAO;
```

En EmpleadosController, asi que se queda asi (constructor incluido):

```
// la interfaz CRUD DAO asociada
private final IEmpleadoEntityDAO empleadoEntityDAO;
private final IDepartamentoEntityDAO departamentoEntityDAO;

// Es obligatorio definir el constructor debido a la inyección de dependencias (DI)
public EmpleadosController(IEmpleadoEntityDAO empleadoEntityDAO, IDepartamentoEntityDAO departamentoEntityDAO) {
    this.empleadoEntityDAO = empleadoEntityDAO;
    this.departamentoEntityDAO = departamentoEntityDAO;
    }
```

Spring se encarga de darnos acceso al departamentoEntityDAO mediante inyección de dependencias.

Ahora sí podemos usar los métodos de departamentoEntity, y podríamos meter un findById como condicional para verificar
que ese departamento existe.
Pasamos de tener esto:

```
@PostMapping
public EmpleadoEntity saveEmpleado(@RequestBody EmpleadoEntity empleadoEntity) {
    return empleadoEntityDAO.save(empleadoEntity);
}
```

a esto:

```
// Metodo POST (/empleados)
// Guarda una entidad empleado
@PostMapping
public ResponseEntity<?> saveEmpleado(@RequestBody EmpleadoEntity empleado) {
    // verifica que el campo departamento no sea null
    if ( (empleado.getDepartamento() != null) &&
            // verifica que el departamento del empleado a añadir existe
            departamentoEntityDAO.existsById(empleado.getDepartamento().getId())
    )
        // devuelve ok y guarda
        return ResponseEntity.ok().body(empleadoEntityDAO.save(empleado));
    else
        // devuelve bad request
        return ResponseEntity.badRequest().build();
}
```

## 10 - Uso de DTO
Imagina que queremos pasar un empleado, y a la vez los datos del departamento al que pertenece.
O pasar un objeto que tiene información de un puñado de tablas.

No podemos pasar el empleadoEntity, ya que solo contiene la referencia al departamento.

En estos casos, creamos un DTO (Data Transfer Object) que tendrá los campos que nos interesan.

Se trata de un POJO sin anotaciones (ya que no hace referencia a una tabla), simplemente campos (atributos).

Este DTO tendrá sus getters y setters de toda la vida.

Template de endpoint que devuelve un dto mappeado:

```
// GET /dto/id
// devuelve un empleado Y los datos del departamento al que pertenece
@GetMapping
public ResponseEntity<EmpleadoDTO> findEmpleadoDTOById(@PathVariable(value = "id") int id) {

    Optional<EmpleadoEntity> empleadoEntityOptional = empleadoEntityDAO.findById(id);

    if (empleadoEntityOptional.isPresent()) {
        EmpleadoEntity empleado = empleadoEntityOptional.get();
        Optional<DepartamentoEntity> departamentoEntityOptional = departamentoEntityDAO.findById(empleado.getDepartamento().getId());

        EmpleadoDTO empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setId(empleado.getId());
        empleadoDTO.setNombre(empleado.getNombre());
        empleadoDTO.setPuesto(empleado.getPuesto());
        empleadoDTO.setDepartamentoId(empleado.getDepartamento().getId());
        empleadoDTO.setDepartamentoNombre("");
        empleadoDTO.setDepartamentoUbicacion("");
        if (departamentoEntityOptional.isPresent()) {
            DepartamentoEntity departamento = departamentoEntityOptional.get();
            empleadoDTO.setDepartamentoNombre(departamento.getNombre());
            empleadoDTO.setDepartamentoUbicacion(departamento.getUbicacion());
        }
        return ResponseEntity.ok().body(empleadoDTO);
    } else {
        return ResponseEntity.notFound().build();
    }
}
```

En caso de tener muchos campos es tedioso escribir eso, asi que usamos la clase ModelMapper.

Aqui esta la dependencia a añadir en el pom.xml

```
<!-- https://mvnrepository.com/artifact/org.modelmapper/modelmapper -->
<dependency>
<groupId>org.modelmapper</groupId>
<artifactId>modelmapper</artifactId>
<version>3.2.2</version>
</dependency>
```

Uso de modelmapper (con correccion de sobreescritura de mappeo):

```
// GET /dto/id
// devuelve un empleado Y los datos del departamento al que pertenece
@GetMapping
public ResponseEntity<EmpleadoDTO> findEmpleadoDTOById(@PathVariable(value = "id") int id) {
    Optional<EmpleadoEntity> empleadoEntityOptional = empleadoEntityDAO.findById(id);
    if (empleadoEntityOptional.isPresent()) {
        EmpleadoEntity empleado = empleadoEntityOptional.get();
        Optional<DepartamentoEntity> departamentoEntityOptional =
                departamentoEntityDAO.findById(empleado.getDepartamento().getId());

        // model mapper
        ModelMapper mapper = new ModelMapper();
        
        EmpleadoDTO empleadoDTO = mapper.map(empleado, EmpleadoDTO.class);
        // correccion para campos que tienen el mismo nombre
        // especificamente, le dice de saltar el siguiente mapeo para no sobreescribir el nombre ya escrito
        mapper.typeMap(
                        DepartamentoEntity.class, EmpleadoDTO.class).
                addMappings(
                        maping -> maping.skip(EmpleadoDTO::setNombre));
        
        if (departamentoEntityOptional.isPresent()) {
            DepartamentoEntity departamento = departamentoEntityOptional.get();
            mapper.map(departamento, empleadoDTO);
        }
        return ResponseEntity.ok().body(empleadoDTO);
    } else {
        return ResponseEntity.notFound().build();
    }
}
```

## 11 - La capa Service

Division de responsabilidades y arquitectura manda separar las responsabilidades en:
- repositorio (accede a la base de datos)
- service (lógica de negocio)
- controlador (recepcion y gestión de solicitudes)

Para esto, creamos un nuevo paquete "services", y en el una clase EmpleadosService:

```
@Service
public class EmpleadosService {
}
```

Pasamos toda la logica al Service, ahora Controller solo invoca al Service, no piensa por si mismo
en como obtiene los datos. Ejemplo de la refactorización.

#### En el Controller
```
// Metodo HTTP GET (/empleados)
// Cuando accedamos a ese endpoint, devolverá un listado de empleados
@GetMapping
public List<EmpleadoEntity> findAllEmpleados() {
    return empleadosService.buscarEmpleados();
}
```
#### En el Service
```
public List<EmpleadoEntity> buscarEmpleados() {
    return (List<EmpleadoEntity>) empleadoEntityDAO.findAll();
}
```

En el caso de ser operaciones como guardado (que deben mostrar éxito o fallo), seguir este
patrón (intentar la operación, si se puede, hacerla y devolver true, si no, false):

```
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
```

## 12 - Validación de datos

Para usar una validación de datos facil en Spring, mete esta dependencia:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Esto permite meter anotaciones de validación como estas:
```
@Basic
@NotEmpty(message="El nombre no puede estar vacío")
@Size(min = 2, max = 10, message = "El nombre tiene que tener entre 2 y 10 caracteres")
@Column(name = "nombre", nullable = true, length = 10)
public String getNombre() {
    return nombre;
}
```

Para verificar que el objeto coincide con las restricciones, se usa la anotación @Valid

```
@PostMapping
public ResponseEntity<?> saveEmpleado(@Valid @RequestBody EmpleadoEntity empleado) {
    Optional<EmpleadoEntity> empleadoEntityOptional =
    empleadosService.saveEmpleado(empleado);
    if (empleadoEntityOptional.isPresent()) {
        return ResponseEntity.ok().body( empleadoEntityOptional.get());
    }
    else {
        return ResponseEntity.badRequest().build();
    }
}
```

No he puesto las validaciones en este proyecto, pero deberia entenderse como se hacen.