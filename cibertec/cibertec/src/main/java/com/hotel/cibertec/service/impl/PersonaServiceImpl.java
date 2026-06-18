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
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.ConnectionCallback;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDto> listarTodos() {
        return repository.findAllConTipo().stream().map(this::toDto).collect(Collectors.toList());
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
        String clave = passwordEncoder.encode(dto.getClave());
        // Llamada a función de Postgres
        String sql = "{? = call sp_RegistrarPersona(?, ?, ?, ?, ?, ?, ?)}";

        Boolean exito = jdbcTemplate.execute((ConnectionCallback<Boolean>) con -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                cs.registerOutParameter(1, Types.BOOLEAN);
                cs.setString(2, dto.getTipoDocumento());
                cs.setString(3, dto.getDocumento());
                cs.setString(4, dto.getNombre());
                cs.setString(5, dto.getApellido());
                cs.setString(6, dto.getCorreo());
                cs.setString(7, clave);
                cs.setInt(8, dto.getIdTipoPersona());
                cs.execute();
                return cs.getBoolean(1);
            }
        });

        if (!Boolean.TRUE.equals(exito)) throw new RuntimeException("Error: El documento o correo ya existe.");
        return toDto(repository.findByCorreo(dto.getCorreo()).orElseThrow());
    }

    @Override
    @Transactional
    public PersonaDto actualizar(Integer id, PersonaDto dto) {
        Persona p = repository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        String claveFinal = (dto.getClave() == null || dto.getClave().isEmpty() || dto.getClave().equals("**********"))
                ? p.getClave() : passwordEncoder.encode(dto.getClave());

        String sql = "{? = call sp_ModificarPersona(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        Boolean exito = jdbcTemplate.execute((ConnectionCallback<Boolean>) con -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                cs.registerOutParameter(1, Types.BOOLEAN); // Retorno de la función
                cs.setInt(2, id);
                cs.setString(3, dto.getTipoDocumento());
                cs.setString(4, dto.getDocumento());
                cs.setString(5, dto.getNombre());
                cs.setString(6, dto.getApellido());
                cs.setString(7, dto.getCorreo());
                cs.setString(8, claveFinal);
                cs.setInt(9, dto.getIdTipoPersona());
                cs.setBoolean(10, dto.getEstado());
                cs.setString(11, dto.getFotoUrl() != null ? dto.getFotoUrl() : "");
                cs.execute();
                return cs.getBoolean(1);
            }
        });

        if (!Boolean.TRUE.equals(exito)) throw new RuntimeException("Error al actualizar en BD.");
        return toDto(repository.findById(id).orElseThrow());
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
                .idTipoPersona(e.getTipoPersona() != null ? e.getTipoPersona().getIdTipoPersona() : null)
                .estado(e.getEstado())
                .fotoUrl(e.getFotoUrl())
                .fechaCreacion(e.getFechaCreacion() != null ? e.getFechaCreacion() : java.time.LocalDateTime.now())
                .build();
    }
}