package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.CategoriaDto;
import com.hotel.cibertec.entity.Categoria;
import com.hotel.cibertec.repository.CategoriaRepository;
import com.hotel.cibertec.service.CategoriaService;
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
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDto buscarPorId(Integer id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        return toDto(categoria);
    }

    @Override
    @Transactional
    public CategoriaDto guardar(CategoriaDto dto) {

        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("sp_RegistrarCategoria");

        query.registerStoredProcedureParameter(
                "p_Descripcion",
                String.class,
                ParameterMode.IN
        );

        query.registerStoredProcedureParameter(
                "Resultado",
                Boolean.class,
                ParameterMode.OUT
        );

        query.setParameter(
                "p_Descripcion",
                dto.getDescripcion()
        );

        query.execute();

        Boolean resultado =
                (Boolean) query.getOutputParameterValue("Resultado");

        if (!Boolean.TRUE.equals(resultado)) {
            throw new RuntimeException("La categoría ya existe");
        }

        return dto;
    }

    @Override
    @Transactional
    public CategoriaDto actualizar(Integer id, CategoriaDto dto) {

        categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery("sp_ModificarCategoria");

        query.registerStoredProcedureParameter(
                "p_IdCategoria",
                Integer.class,
                ParameterMode.IN
        );

        query.registerStoredProcedureParameter(
                "p_Descripcion",
                String.class,
                ParameterMode.IN
        );

        query.registerStoredProcedureParameter(
                "p_Estado",
                Boolean.class,
                ParameterMode.IN
        );

        query.registerStoredProcedureParameter(
                "Resultado",
                Boolean.class,
                ParameterMode.OUT
        );

        query.setParameter("p_IdCategoria", id);
        query.setParameter("p_Descripcion", dto.getDescripcion());
        query.setParameter("p_Estado", dto.getEstado());

        query.execute();

        Boolean resultado =
                (Boolean) query.getOutputParameterValue("Resultado");

        if (!Boolean.TRUE.equals(resultado)) {
            throw new RuntimeException("Ya existe una categoría con esa descripción");
        }

        return dto;
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        categoria.setEstado(false);

        categoriaRepository.save(categoria);
    }

    private CategoriaDto toDto(Categoria entity) {
        return CategoriaDto.builder()
                .idCategoria(entity.getIdCategoria())
                .descripcion(entity.getDescripcion())
                .estado(entity.getEstado())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    private Categoria toEntity(CategoriaDto dto) {
        return Categoria.builder()
                .idCategoria(dto.getIdCategoria())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado())
                .fechaCreacion(dto.getFechaCreacion())
                .build();
    }
}