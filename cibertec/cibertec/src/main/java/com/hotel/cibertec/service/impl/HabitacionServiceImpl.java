package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.HabitacionDto;
import com.hotel.cibertec.entity.Habitacion;
import com.hotel.cibertec.entity.ImagenHabitacion;
import com.hotel.cibertec.repository.HabitacionRepository;
import com.hotel.cibertec.repository.ImagenHabitacionRepository;
import com.hotel.cibertec.service.HabitacionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository repository;
    private final ImagenHabitacionRepository imagenRepository; // Inyectar repositorio de imágenes
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDto> listarTodos() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionDto buscarPorId(Integer id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    @Override
    @Transactional
    public HabitacionDto guardar(HabitacionDto dto) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_RegistrarHabitacion");
            query.registerStoredProcedureParameter("p_Numero", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Detalle", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Precio", java.math.BigDecimal.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_IdPiso", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_IdCategoria", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

            query.setParameter("p_Numero", dto.getNumero());
            query.setParameter("p_Detalle", dto.getDetalle());
            query.setParameter("p_Precio", dto.getPrecio());
            query.setParameter("p_IdPiso", dto.getIdPiso());
            query.setParameter("p_IdCategoria", dto.getIdCategoria());

            query.execute();
            Boolean resultado = (Boolean) query.getOutputParameterValue("Resultado");

            if (Boolean.FALSE.equals(resultado)) {
                throw new IllegalArgumentException("El número de habitación ya se encuentra registrado.");
            }

            // Obtener la entidad recién creada para asociar las imágenes
            Habitacion nuevaHab = repository.findByNumero(dto.getNumero())
                    .orElseThrow(() -> new RuntimeException("Error al recuperar la habitación registrada"));

            guardarImagenes(nuevaHab, dto.getUrlsImagenes());

            return toDto(nuevaHab); // Retornamos el DTO actualizado con IDs
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar habitación: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public HabitacionDto actualizar(Integer id, HabitacionDto dto) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_ModificarHabitacion");
            query.registerStoredProcedureParameter("p_IdHabitacion", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Numero", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Detalle", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Precio", java.math.BigDecimal.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_IdPiso", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_IdCategoria", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_IdEstadoHabitacion", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_Estado", Boolean.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

            query.setParameter("p_IdHabitacion", id);
            query.setParameter("p_Numero", dto.getNumero());
            query.setParameter("p_Detalle", dto.getDetalle());
            query.setParameter("p_Precio", dto.getPrecio());
            query.setParameter("p_IdPiso", dto.getIdPiso());
            query.setParameter("p_IdCategoria", dto.getIdCategoria());
            query.setParameter("p_IdEstadoHabitacion", dto.getIdEstadoHabitacion());
            query.setParameter("p_Estado", dto.getEstado());

            query.execute();
            Boolean resultado = (Boolean) query.getOutputParameterValue("Resultado");

            if (Boolean.FALSE.equals(resultado)) {
                throw new IllegalArgumentException("Error al modificar: El número de habitación está duplicado.");
            }

            // Limpiar imágenes y forzar sincronización con la BD
            imagenRepository.deleteByHabitacion_IdHabitacion(id);
            imagenRepository.flush();

            Habitacion hab = repository.findById(id).orElseThrow();
            guardarImagenes(hab, dto.getUrlsImagenes());

            return toDto(hab);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar habitación: " + e.getMessage());
        }
    }

    private void guardarImagenes(Habitacion habitacion, List<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            List<ImagenHabitacion> imagenes = urls.stream()
                    .map(url -> ImagenHabitacion.builder().urlImagen(url).habitacion(habitacion).build())
                    .collect(Collectors.toList());
            imagenRepository.saveAll(imagenes); // Batch save para mayor eficiencia
        }
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Habitacion h = repository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        h.setEstado(false); // Eliminación lógica
        repository.save(h);
    }

    private HabitacionDto toDto(Habitacion e) {
        return HabitacionDto.builder()
                .idHabitacion(e.getIdHabitacion())
                .numero(e.getNumero())
                .detalle(e.getDetalle())
                .precio(e.getPrecio())
                .idPiso(e.getPiso() != null ? e.getPiso().getIdPiso() : null)
                .idCategoria(e.getCategoria() != null ? e.getCategoria().getIdCategoria() : null)
                .idEstadoHabitacion(e.getEstadoHabitacion() != null ? e.getEstadoHabitacion().getIdEstadoHabitacion() : null)
                .estado(e.getEstado())
                // --- AGREGAR ESTO ---
                .urlsImagenes(e.getImagenes() != null ?
                        e.getImagenes().stream().map(img -> img.getUrlImagen()).collect(Collectors.toList())
                        : List.of())
                .build();
    }
}