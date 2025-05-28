package com.example.ProyectoTaw.validator;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (error1, error2) -> error2
                ));
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Error de validación en los datos de entrada",
            errores,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String nombre = ex.getName();
        String tipo = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        Object valor = ex.getValue();
        String mensaje = String.format("El parámetro '%s' debería ser de tipo '%s', pero se recibió: '%s'", 
                                      nombre, tipo, valor);
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Error de tipo de datos",
            mensaje,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errores = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    violation -> violation.getMessage(),
                    (error1, error2) -> error2
                ));
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Error de validación en los parámetros",
            errores,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            "Recurso no encontrado",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
    
    @ExceptionHandler(RecursoNoDisponibleException.class)
    public ResponseEntity<ApiError> handleRecursoNoDisponible(RecursoNoDisponibleException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.CONFLICT.value(),
            "Recurso no disponible",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Error de negocio",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFound(NoHandlerFoundException ex) {
        String requestURL = ex.getRequestURL();
        String httpMethod = ex.getHttpMethod();
        
        String mensaje = String.format("No se encontró un controlador para %s %s", 
                                    httpMethod, requestURL);
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            "Endpoint no encontrado",
            mensaje,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            "Ocurrió un error inesperado. Por favor contacte al administrador si el problema persiste.",
            LocalDateTime.now()
        );
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ex.printStackTrace();
        String mensajeUsuario = "Error en el formato de los datos enviados.";
        String detalles = ex.getMostSpecificCause().getMessage();
        if (detalles != null && detalles.contains("Cannot deserialize value of type") && detalles.contains("java.time.LocalDate")) {
            if (detalles != null && detalles.contains("Cannot deserialize value of type") && detalles.contains("java.time.LocalDate")) {
                if (detalles.contains("from Null value")) {
                    mensajeUsuario = "El campo de fecha obligatoria no puede ser nulo y debe tener formato YYYY-MM-DD.";
                } else {
                    mensajeUsuario = "El campo de fecha debe tener formato YYYY-MM-DD.";
                }
            }
        }
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            mensajeUsuario,
            detalles,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        String mensaje = "Violación de restricción de datos. Puede que algún valor ya exista o no cumpla una restricción única.";
        String detalles = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        if (detalles != null && detalles.contains("duplicate key value") && detalles.contains("email")) {
            mensaje = "El email ya está registrado. Debe ingresar un email único.";
        } else if (detalles != null && detalles.contains("duplicate key value") && detalles.contains("nro_matricula")) {
            mensaje = "El número de matrícula ya está registrado. Debe ingresar un número de matrícula único.";
        } else if (detalles != null && detalles.contains("duplicate key value") && detalles.contains("ci")) {
            mensaje = "El CI ya está registrado. Debe ingresar un CI único.";
        }
        // Eliminado: else if (detalles != null && detalles.contains("duplicate key value") && detalles.contains("nombre_usuario")) {
        //    mensaje = "El nombre de usuario ya está registrado. Debe ingresar un nombre de usuario único.";
        // }
        ApiError apiError = new ApiError(
            HttpStatus.CONFLICT.value(),
            mensaje,
            detalles,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Error de validación en los datos de entrada",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }

    public class RecursoNoDisponibleException extends RuntimeException {
        public RecursoNoDisponibleException(String mensaje) {
            super(mensaje);
        }
    }

    private static class ApiError {
        private int status;
        private String error;
        private Object message;
        private LocalDateTime timestamp;

        public ApiError(int status, String error, Object message, LocalDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public Object getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}