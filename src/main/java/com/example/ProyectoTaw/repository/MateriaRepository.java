// Archivo: src/main/java/com/example/ProyectoTaw/repository/MateriaRepository.java

package com.example.ProyectoTaw.repository;

import com.example.ProyectoTaw.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    // *******************************************************************
    // ****** AGREGAR/VERIFICAR ESTE MÉTODO EN MateriaRepository.java ******
    // *******************************************************************
    Optional<Materia> findByCodigoUnico(String codigoUnico); // Este es el que el servicio llamará

    Boolean existsByCodigoUnico(String codigoUnico);

    // Asegúrate de que el nombre del método para buscar por nombre y descripción sea el correcto:
    List<Materia> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);
}