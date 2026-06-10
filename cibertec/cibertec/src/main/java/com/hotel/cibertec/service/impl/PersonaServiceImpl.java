// com/hotel/cibertec/service/impl/PersonaServiceImpl.java
package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.PersonaDto;
import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.repository.PersonaRepository;
import com.hotel.cibertec.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.ConnectionCallback;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder; // Dejamos que Spring lo inyecte desde SecurityConfig

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDto> listarTodos() {
        return repository.findAllConTipo().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDto buscarPorId(Integer id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    @Override
    @Transactional
    public PersonaDto guardar(PersonaDto dto) {
        String claveEncriptada = passwordEncoder.encode(dto.getClave());
        String sql = "CALL sp_RegistrarPersona(?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.execute((ConnectionCallback<PersonaDto>) (Connection con) -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                cs.setString(1, dto.getTipoDocumento());
                cs.setString(2, dto.getDocumento());
                cs.setString(3, dto.getNombre());
                cs.setString(4, dto.getApellido());
                cs.setString(5, dto.getCorreo());
                cs.setString(6, claveEncriptada);
                cs.setInt(7, dto.getIdTipoPersona());
                cs.registerOutParameter(8, Types.BOOLEAN);

                cs.execute();

                if (!cs.getBoolean(8)) {
                    throw new RuntimeException("Error: El documento ya existe.");
                }

                return dto;
            }
        });
    }

    @Override
    @Transactional
    public PersonaDto actualizar(Integer id, PersonaDto dto) {
        // 1. Buscamos la entidad actual para verificar su contraseña preexistente
        Persona personaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        String claveFinal;
        // 2. CORRECCIÓN CRÍTICA: Si la clave viene vacía o es idéntica a la encriptada, mantenemos la de la BD
        if (dto.getClave() == null || dto.getClave().trim().isEmpty() || dto.getClave().equals(personaExistente.getClave())) {
            claveFinal = personaExistente.getClave();
        } else {
            claveFinal = passwordEncoder.encode(dto.getClave());
        }

        String sql = "CALL sp_ModificarPersona(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        boolean exito = jdbcTemplate.execute((ConnectionCallback<Boolean>) (Connection con) -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                cs.setInt(1, id);
                cs.setString(2, dto.getTipoDocumento());
                cs.setString(3, dto.getDocumento());
                cs.setString(4, dto.getNombre());
                cs.setString(5, dto.getApellido());
                cs.setString(6, dto.getCorreo());
                cs.setString(7, claveFinal); // Enviamos la clave procesada de manera segura
                cs.setInt(8, dto.getIdTipoPersona());
                cs.setBoolean(9, dto.getEstado());
                cs.registerOutParameter(10, Types.BOOLEAN);

                cs.execute();
                return cs.getBoolean(10); // Retornamos el resultado del SP
            }
        });

        if (!exito) {
            throw new RuntimeException("Error: El documento ingresado ya está registrado en otra persona.");
        }

        return dto;
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Persona p = repository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        p.setEstado(false);
        repository.save(p);
    }

    private PersonaDto toDto(Persona e) {
        return PersonaDto.builder()
                .idPersona(e.getIdPersona())
                .tipoDocumento(e.getTipoDocumento())
                .documento(e.getDocumento())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .correo(e.getCorreo())
                .clave(e.getClave())
                .idTipoPersona(e.getTipoPersona() != null ? e.getTipoPersona().getIdTipoPersona() : null)
                .estado(e.getEstado())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}