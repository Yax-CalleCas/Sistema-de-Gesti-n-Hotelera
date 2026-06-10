package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.PisoDto;
import com.hotel.cibertec.entity.Piso;
import com.hotel.cibertec.repository.PisoRepository;
import com.hotel.cibertec.service.PisoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PisoServiceImpl implements PisoService {

    private final PisoRepository pisoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<PisoDto> listarTodos() {
        return pisoRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PisoDto buscarPorId(Integer id) {
        return pisoRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    @Override
    @Transactional
    public PisoDto guardar(PisoDto dto) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_RegistrarPiso");
        query.registerStoredProcedureParameter("p_Descripcion", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

        query.setParameter("p_Descripcion", dto.getDescripcion());
        query.execute();

        if (!Boolean.TRUE.equals(query.getOutputParameterValue("Resultado"))) {
            throw new RuntimeException("Error al registrar el piso");
        }
        return dto;
    }

    @Override
    @Transactional
    public PisoDto actualizar(Integer id, PisoDto dto) {
        pisoRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_ModificarPiso");
        query.registerStoredProcedureParameter("p_IdPiso", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Descripcion", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Estado", Boolean.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

        query.setParameter("p_IdPiso", id);
        query.setParameter("p_Descripcion", dto.getDescripcion());
        query.setParameter("p_Estado", dto.getEstado());
        query.execute();

        if (!Boolean.TRUE.equals(query.getOutputParameterValue("Resultado"))) {
            throw new RuntimeException("Error al modificar el piso");
        }
        return dto;
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Piso piso = pisoRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        pisoRepository.delete(piso);
    }

    private PisoDto toDto(Piso entity) {
        return PisoDto.builder().idPiso(entity.getIdPiso()).descripcion(entity.getDescripcion())
                .estado(entity.getEstado()).fechaCreacion(entity.getFechaCreacion()).build();
    }
}