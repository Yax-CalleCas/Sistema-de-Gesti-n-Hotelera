package com.hotel.cibertec.repository;

import com.hotel.cibertec.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    @Query("SELECT p FROM Persona p JOIN FETCH p.tipoPersona")
    List<Persona> findAllConTipo();

    Optional<Persona> findByCorreo(String correo);
}