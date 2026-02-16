package ad.springthymeleaf.controllers;

import ad.springthymeleaf.models.entities.DepartamentoEntity;
import ad.springthymeleaf.models.entities.EmpleadoEntity;
import ad.springthymeleaf.services.DepartamentosServiceImpl;
import ad.springthymeleaf.services.EmpleadosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/")
    public String index() {
        //renderiza la vista index ubicada en resources/templates/index.html
        return "index";
    }

    //
    //      EMPLEADOS
    //

    @GetMapping("/verempleados")
    public String mostrarEmpleados(Model model) {

        List<EmpleadoEntity> empleados =
                empleadosService.findAllEmpleados();

        model.addAttribute("empleados", empleados);
        return "verempleados";
    }

    @GetMapping("/altaempleado")
    public String altaEmpleado(Model model) {
        //Pasamos al modelo una DepartamentoEntity vacío
        model.addAttribute("empleado", new EmpleadoEntity());
        return "altaempleado";
    }

    //Accedemos por POST al endpoint /altadepartamento
    @PostMapping("/altaempleado")
    public String crearEmpleado(@ModelAttribute EmpleadoEntity empleado, Model model) {
        empleadosService.saveEmpleado(empleado);
        return "altaempleado";
    }


    //
    //      DEPARTAMENTOS
    //

    @GetMapping("/verdepartamentos")
    public String mostrarDepartamentos(Model model, @RequestParam(name = "ubicacion", required =
            false) String ubicacion) {
        //obtenemos los departamentos de la capa DepartamentosServiceImpl
        List<DepartamentoEntity> departamentos = departamentosService.obtenerDepartamentos();
        //Obtenemos una lista de ubicaciones de departamentos sin repetir (usando DISTINCT)
        List<String> ubicaciones = departamentosService.obtenerUbicaciones();
        model.addAttribute("ubicaciones", ubicaciones);
        if (ubicacion != null) {
            departamentos = departamentosService.obtenerDepartamentosPorUbicacion(ubicacion);
        }
        //La clase Model nos ofrece addAtribute para enviar variables/atributos a la plantilla
        model.addAttribute("departamentos", departamentos);
        return "verdepartamentos";
    }

    //Accedemos por GET al endpoint /altadepartamento
    @GetMapping("/altadepartamento")
    public String altaDepartamento(Model model) {
    //Pasamos al modelo una DepartamentoEntity vacío
        model.addAttribute("departamento", new DepartamentoEntity());
        return "altadepartamento";
    }

    //Accedemos por POST al endpoint /altadepartamento
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

    // borrar departamento
    @GetMapping("/borrardpto")
    public String borrardpto(Model model, @RequestParam(name="id", required = true) int id)
    {
        Optional<DepartamentoEntity> departamentoEntityOptional =
                departamentosService.obtenerDepartamentoById(id);
        if (departamentoEntityOptional.isEmpty()) {
            model.addAttribute("titulo", "Error");
            model.addAttribute("mensaje", "No se encontró el departamento con el id " + id);
        }
        departamentosService.borrarDepartamentoPorId(id);
        return "index";
    }


}