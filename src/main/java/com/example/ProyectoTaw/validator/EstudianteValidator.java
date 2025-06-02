package com.example.ProyectoTaw.validator;

import org.springframework.stereotype.Component;
import com.example.ProyectoTaw.dto.EstudianteDTO;
import com.example.ProyectoTaw.model.Estudiante;
import com.example.ProyectoTaw.repository.EstudianteRepository;
import com.example.ProyectoTaw.validator.GlobalExceptionHandler.BusinessException; // Asegúrate de que esta ruta sea correcta

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component // Indica que esta clase es un componente de Spring y puede ser inyectada
public class EstudianteValidator {

    private final EstudianteRepository estudianteRepository;

    // Inyección de dependencias del repositorio a través del constructor
    public EstudianteValidator(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    /**
     * Valida que el email sea único, excluyendo al propio estudiante si se está actualizando.
     * @param email El email a validar.
     * @param ciActual El CI del estudiante actual (si es una actualización) o null (si es una creación).
     * @throws BusinessException si el email ya existe para otro estudiante.
     */
    public void validaEmailUnico(String email, String ciActual) { // CI ahora es String
        Optional<Estudiante> existente = estudianteRepository.findByEmail(email);
        // Si existe un estudiante con ese email Y su CI no es el CI del estudiante actual
        if (existente.isPresent() && !existente.get().getCi().equals(ciActual)) {
            throw new BusinessException("Ya existe un estudiante con este email: " + email);
        }
    }

    /*
     * NOTA: Se ha eliminado el método validaNroMatriculaUnico
     * y las referencias a 'nroMatricula' en validacionCompletaEstudiante
     * y validarActualizacionEstudiante, ya que el campo 'nroMatricula'
     * no está presente en tu entidad 'Estudiante' ni en el 'EstudianteDTO' actuales.
     * Si en el futuro agregas este campo, puedes reintroducir esta lógica.
     */

    /**
     * Valida que la CI sea única, excluyendo al propio estudiante si se está actualizando.
     * @param ci El CI a validar.
     * @param ciActual El CI del estudiante actual (si es una actualización) o null (si es una creación).
     * @throws BusinessException si la CI ya existe para otro estudiante.
     */
    public void validaCiUnico(String ci, String ciActual) { // CI ahora es String
        Optional<Estudiante> existente = estudianteRepository.findByCi(ci); // Usa findByCi para String
        // Si existe un estudiante con esa CI Y su CI no es el CI del estudiante actual
        if (existente.isPresent() && (ciActual == null || !existente.get().getCi().equals(ciActual))) {
            throw new BusinessException("Ya existe un estudiante con este CI: " + ci);
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
     * Valida que el nombre del estudiante no esté vacío o nulo.
     * @param nombre El nombre a validar.
     * @throws BusinessException si el nombre es inválido.
     */
    public void validaNombreEstudiante(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre no puede estar vacío o nulo.");
        }
        if (nombre.trim().length() < 2 || nombre.trim().length() > 100) {
            throw new BusinessException("El nombre debe tener entre 2 y 100 caracteres.");
        }
    }

    /**
     * Valida que el apellido del estudiante no esté vacío o nulo.
     * @param apellido El apellido a validar.
     * @throws BusinessException si el apellido es inválido.
     */
    public void validaApellidoEstudiante(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new BusinessException("El apellido es obligatorio y no puede estar vacío.");
        }
        if (apellido.trim().length() < 2 || apellido.trim().length() > 100) {
            throw new BusinessException("El apellido debe tener entre 2 y 100 caracteres.");
        }
    }

    /**
     * Realiza una validación completa para la creación de un nuevo estudiante.
     * @param estudianteDTO Los datos del estudiante a validar.
     * @throws BusinessException si alguna validación falla.
     */
    public void validacionCompletaEstudiante(EstudianteDTO estudianteDTO) {
        // En este caso, para una creación, ciActual es null porque aún no existe en la DB
        validaCiUnico(estudianteDTO.getCi(), null); // Valida que la CI no exista para NINGÚN estudiante
        validaEmailUnico(estudianteDTO.getEmail(), null); // Valida que el email no exista para NINGÚN estudiante
        validaDominioEmail(estudianteDTO.getEmail());
        validaNombreEstudiante(estudianteDTO.getNombre());
        validaApellidoEstudiante(estudianteDTO.getApellido());
        // Las validaciones de @NotBlank, @Size, @Email, @NotNull, @Past en el DTO
        // se realizan automáticamente si usas @Valid en tu controlador.
        // Este validador añade lógica de negocio (unicidad, dominios).
    }

    /**
     * Realiza validaciones específicas para la actualización de un estudiante existente.
     * Permite que el email o la CI permanezcan iguales si pertenecen al mismo estudiante.
     * @param estudianteDTO Los nuevos datos del estudiante.
     * @param estudianteExistente La entidad del estudiante tal como está actualmente en la base de datos.
     * @throws BusinessException si alguna validación falla.
     */
    public void validarActualizacionEstudiante(EstudianteDTO estudianteDTO, Estudiante estudianteExistente) {
        // Si el email ha cambiado, validar su unicidad
        if (!estudianteExistente.getEmail().equalsIgnoreCase(estudianteDTO.getEmail())) {
            validaEmailUnico(estudianteDTO.getEmail(), estudianteExistente.getCi());
        }
        // Si la CI ha cambiado (lo cual no debería ocurrir para una PK), validar su unicidad
        // Usualmente, la CI no se actualiza en una actualización de datos, solo se usa para identificar el registro.
        // Si permites que la CI cambie, esta validación es necesaria.
        if (!estudianteExistente.getCi().equals(estudianteDTO.getCi())) {
             validaCiUnico(estudianteDTO.getCi(), estudianteExistente.getCi());
        }

        // Validaciones que siempre deben aplicarse, sin importar si los campos cambiaron
        validaDominioEmail(estudianteDTO.getEmail());
        validaNombreEstudiante(estudianteDTO.getNombre());
        validaApellidoEstudiante(estudianteDTO.getApellido());
    }
}