package com.example.ProyectoTaw.validator;

import org.springframework.stereotype.Component;
import com.example.ProyectoTaw.dto.DocenteDTO;
import com.example.ProyectoTaw.model.Docente;
import com.example.ProyectoTaw.repository.DocenteRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta ruta sea correcta

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component // Indica que esta clase es un componente de Spring y puede ser inyectada
public class DocenteValidator {
    private final DocenteRepository docenteRepository;

    // Inyección de dependencias del repositorio a través del constructor
    public DocenteValidator(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    /**
     * Valida que el email sea único, excluyendo al propio Docente si se está actualizando.
     * @param email El email a validar.
     * @param ciActual El CI del Docente actual (si es una actualización) o null (si es una creación).
     * @throws BusinessException si el email ya existe para otro Docente.
     */
    public void validaEmailUnico(String email, String ciActual) { // CI ahora es String
        Optional<Docente> existente = docenteRepository.findByEmail(email);
        // Si existe un Docente con ese email Y su CI no es el CI del Docente actual
        if (existente.isPresent() && !existente.get().getCiDocente().equals(ciActual)) {
            throw new BusinessException("Ya existe un Docente con este email: " + email);
        }
    }

    /*
     * NOTA: Se ha eliminado el método validaNroMatriculaUnico
     * y las referencias a 'nroMatricula' en validacionCompletaDocente
     * y validarActualizacionDocente, ya que el campo 'nroMatricula'
     * no está presente en tu entidad 'Docente' ni en el 'DocenteDTO' actuales.
     * Si en el futuro agregas este campo, puedes reintroducir esta lógica.
     */

    /**
     * Valida que la CI sea única, excluyendo al propio Docente si se está actualizando.
     * @param ci El CI a validar.
     * @param ciActual El CI del Docente actual (si es una actualización) o null (si es una creación).
     * @throws BusinessException si la CI ya existe para otro Docente.
     */
    public void validaCiUnico(String ci, String ciActual) { // CI ahora es String
        Optional<Docente> existente = docenteRepository.findByCi(ci); // Usa findByCi para String
        // Si existe un Docente con esa CI Y su CI no es el CI del Docente actual
        if (existente.isPresent() && (ciActual == null || !existente.get().getCiDocente().equals(ciActual))) {
            throw new BusinessException("Ya existe un Docente con este CI: " + ci);
        }
    }

    /**
     * Valida que el dominio del email no esté en una lista de dominios bloqueados.
     * @param email El email a validar.
     * @throws BusinessException si el dominio del email no está permitido.
     */
    public void validaDominioEmail(String email) {
        // Validación básica de email para asegurar que contiene '@'
        if (email == null || !email.contains("@")) {
            throw new BusinessException("Formato de email inválido.");
        }
        String dominio = email.substring(email.indexOf('@') + 1);
        // Podrías obtener esta lista de una configuración o base de datos
        List<String> dominiosBloqueados = Arrays.asList("dominiobloqueado.com", "spam.com", "example.com"); 

        if (dominiosBloqueados.contains(dominio.toLowerCase())) { // Convertir a minúsculas para comparación insensible a mayúsculas
            throw new BusinessException("El dominio de email '" + dominio + "' no está permitido.");
        }
    }

    /**
     * Valida que el nombre del Docente no esté vacío o nulo.
     * @param nombre El nombre a validar.
     * @throws BusinessException si el nombre es inválido.
     */
    public void validaNombreDocente(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre no puede estar vacío o nulo.");
        }
        if (nombre.trim().length() < 2 || nombre.trim().length() > 100) {
            throw new BusinessException("El nombre debe tener entre 2 y 100 caracteres.");
        }
    }

    /**
     * Valida que el apellido del Docente no esté vacío o nulo.
     * @param apellido El apellido a validar.
     * @throws BusinessException si el apellido es inválido.
     */
    public void validaApellidoDocente(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new BusinessException("El apellido es obligatorio y no puede estar vacío.");
        }
        if (apellido.trim().length() < 2 || apellido.trim().length() > 100) {
            throw new BusinessException("El apellido debe tener entre 2 y 100 caracteres.");
        }
    }


    /**
     * Valida que el apellido del Docente no esté vacío o nulo.
     * @param departamento El apellido a validar.
     * @throws BusinessException si el apellido es inválido.
     */
    public void validaDepartamento(String departamento) {
        if (departamento == null || departamento.trim().isEmpty()) {
            throw new BusinessException("El apellido es obligatorio y no puede estar vacío.");
        }
        if (departamento.trim().length() < 2 || departamento.trim().length() > 100) {
            throw new BusinessException("El apellido debe tener entre 2 y 100 caracteres.");
        }
    }


    /**
     * Valida que el apellido del Docente no esté vacío o nulo.
     * @param nroEmpleado El apellido a validar.
     * @throws BusinessException si el apellido es inválido.
     */
    public void validaNroEmpleado(String nroEmpleado) {
        if (nroEmpleado == null || nroEmpleado.trim().isEmpty()) {
            throw new BusinessException("El apellido es obligatorio y no puede estar vacío.");
        }
        if (nroEmpleado.trim().length() < 1 || nroEmpleado.trim().length() > 3) {
            throw new BusinessException("El apellido debe tener entre 1 y 3 caracteres.");
        }
    }

    /**
     * Realiza una validación completa para la creación de un nuevo Docente.
     * @param docenteDTO Los datos del Docente a validar.
     * @throws BusinessException si alguna validación falla.
     */
    public void validacionCompletaDocente(DocenteDTO docenteDTO) {
        // En este caso, para una creación, ciActual es null porque aún no existe en la DB
        validaCiUnico(docenteDTO.getCi(), null); // Valida que la CI no exista para NINGÚN Docente
        validaEmailUnico(docenteDTO.getEmail(), null); // Valida que el email no exista para NINGÚN Docente
        validaDominioEmail(docenteDTO.getEmail());
        validaNombreDocente(docenteDTO.getNombre());
        validaApellidoDocente(docenteDTO.getApellido());
        validaDepartamento(docenteDTO.getDepartamento());
        validaNroEmpleado(docenteDTO.getNroEmpleado());
        // Las validaciones de @NotBlank, @Size, @Email, @NotNull, @Past en el DTO
        // se realizan automáticamente si usas @Valid en tu controlador.
        // Este validador añade lógica de negocio (unicidad, dominios).
    }

    /**
     * Realiza validaciones específicas para la actualización de un Docente existente.
     * Permite que el email o la CI permanezcan iguales si pertenecen al mismo Docente.
     * @param docenteDTO Los nuevos datos del Docente.
     * @param docenteExistente La entidad del Docente tal como está actualmente en la base de datos.
     * @throws BusinessException si alguna validación falla.
     */
    public void validarActualizacionDocente(DocenteDTO docenteDTO, Docente docenteExistente) {
        // Si el email ha cambiado, validar su unicidad
        if (!docenteExistente.getEmail().equalsIgnoreCase(docenteDTO.getEmail())) {
            validaEmailUnico(docenteDTO.getEmail(), docenteExistente.getCiDocente());
        }
        // Si la CI ha cambiado (lo cual no debería ocurrir para una PK), validar su unicidad
        // Usualmente, la CI no se actualiza en una actualización de datos, solo se usa para identificar el registro.
        // Si permites que la CI cambie, esta validación es necesaria.
        if (!docenteExistente.getCiDocente().equals(docenteDTO.getCi())) {
             validaCiUnico(docenteDTO.getCi(), docenteExistente.getCiDocente());
        }

        // Validaciones que siempre deben aplicarse, sin importar si los campos cambiaron
        validaDominioEmail(docenteDTO.getEmail());
        validaNombreDocente(docenteDTO.getNombre());
        validaApellidoDocente(docenteDTO.getApellido());
        validaDepartamento(docenteDTO.getDepartamento());
        validaNroEmpleado(docenteDTO.getNroEmpleado());
    }

}
