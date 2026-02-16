package ad.springthymeleaf.controllers;

import ad.springthymeleaf.models.entities.DepartamentoEntity;
import ad.springthymeleaf.models.entities.EmpleadoEntity;
import ad.springthymeleaf.services.DepartamentosServiceImpl;
import ad.springthymeleaf.services.EmpleadosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    private final DepartamentosServiceImpl departamentosService;
    private final EmpleadosService empleadosService;

    public WebController(DepartamentosServiceImpl departamentosService, EmpleadosService empleadosService) {
        this.departamentosService = departamentosService;
        this.empleadosService = empleadosService;
    }

    // ruta indicada
    @GetMapping("/")
    public String index() {
    // lo que se sirve en esa ruta (el nombre del html)
        return "index";
    }

    // ****************************************************************************************************************
    // SECCION DE DEPARTAMENTOS
    // ****************************************************************************************************************


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

    //Accedemos por GET al endpint /altadepartamento
    @GetMapping("/altadepartamento")
    public String altaDepartamento(Model model) {

        //Pasamos al modelo una DepartamentoEntity vacío
        model.addAttribute("departamento", new DepartamentoEntity());

        return "altadepartamento";
    }

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

    //Endpoint de /verdepartamento?id=
    @GetMapping("/verdepartamento")
    public String verdepartamento(Model model, @RequestParam(name="id", required = true) int id)
    {
        // busca el departamento por id
        Optional<DepartamentoEntity> departamentoEntityOptional = departamentosService.obtenerDepartamentoById(id);
        // si no lo encuentra
        if (departamentoEntityOptional.isEmpty()) {
            model.addAttribute("titulo", "Error");
            model.addAttribute("mensaje", "No se encontró el departamento con el id " + id);
            return "error";
        }
        // si sí lo encuentra
        model.addAttribute("departamento", departamentoEntityOptional.get());
        return "verdepartamento";
    }

    // ****************************************************************************************************************
    // SECCION DE EMPLEADOS
    // ****************************************************************************************************************

    @GetMapping("/verempleados")
    public String mostrarEmpleados(Model model) {

        List<EmpleadoEntity> empleados =
                empleadosService.buscarEmpleados();

        model.addAttribute("empleados", empleados);
        return "verempleados";
    }

    @GetMapping("/altaempleado")
    public String altaEmpleado(Model model) {

        // empleado vacío para el formulario
        model.addAttribute("empleado", new EmpleadoEntity());

        // lista de departamentos para el <select>
        List<DepartamentoEntity> departamentos =
                departamentosService.obtenerDepartamentos();
        model.addAttribute("departamentos", departamentos);

        return "altaempleado";
    }

    @PostMapping("/altaempleado")
    public String crearEmpleado(@ModelAttribute EmpleadoEntity empleado,
                                Model model) {

        boolean guardado = empleadosService.guardarEmpleado(empleado);

        if (guardado) {
            model.addAttribute("tipo_operacion", "ok");
            model.addAttribute("mensaje", "Empleado creado correctamente");
        } else {
            model.addAttribute("tipo_operacion", "error");
            model.addAttribute("mensaje",
                    "Error al crear el empleado. Verifique el departamento.");
        }

        // volver a cargar departamentos para el select
        model.addAttribute("departamentos",
                departamentosService.obtenerDepartamentos());

        return "altaempleado";
    }

    @GetMapping("/verempleado")
    public String verEmpleado(Model model,
                              @RequestParam(name = "id", required = true) int id) {

        Optional<EmpleadoEntity> empleadoOpt =
                empleadosService.buscarEmpleadoPorId(id);

        if (empleadoOpt.isEmpty()) {
            model.addAttribute("titulo", "Error");
            model.addAttribute("mensaje",
                    "No se encontró el empleado con el id " + id);
            return "error";
        }

        model.addAttribute("empleado", empleadoOpt.get());
        return "verempleado";
    }
}