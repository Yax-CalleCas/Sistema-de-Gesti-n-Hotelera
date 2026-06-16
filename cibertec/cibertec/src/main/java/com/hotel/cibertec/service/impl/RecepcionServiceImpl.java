package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.dto.RecepcionDto;
import com.hotel.cibertec.entity.Recepcion;
import com.hotel.cibertec.repository.RecepcionRepository;
import com.hotel.cibertec.service.RecepcionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecepcionServiceImpl implements RecepcionService {

    private final RecepcionRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<RecepcionDto> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RecepcionDto buscarPorId(Integer id) {
        return repository.findById(id)
                .map(this::toDtoConDatosCliente) // 👈 CAMBIA 'this::toDto' por 'this::toDtoConDatosCliente'
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }


    @Override
    @Transactional(readOnly = true)
    public RecepcionDto buscarActivaPorHabitacion(Integer idHabitacion) {
        // En los sistemas de Cibertec, true o 1 mapean el hospedaje vigente.
        // Filtramos por el id de la habitación y que el estado de la transacción sea activo (true)
        return repository.findFirstByHabitacionIdHabitacionAndEstado(idHabitacion, true)
                .map(this::toDtoConDatosCliente) // Usamos un mapeador extendido para pintar la vista de Angular
                .orElseThrow(() -> new RuntimeException("No se encontró ninguna recepción activa para esta habitación"));
    }

    @Override
    @Transactional
    public RecepcionDto guardar(RecepcionDto dto) {
        // Definimos el procedimiento
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_RegistrarRecepcion");

        // Registramos los parámetros de entrada (usamos java.sql.Date para p_FechaSalida)
        query.registerStoredProcedureParameter("p_IdCliente", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_TipoDocumento", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Documento", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Nombre", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Apellido", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Correo", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_IdHabitacion", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_FechaSalida", java.sql.Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_PrecioInicial", java.math.BigDecimal.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Adelanto", java.math.BigDecimal.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_PrecioRestante", java.math.BigDecimal.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_Observacion", String.class, ParameterMode.IN);

        // Registramos el parámetro de salida con su nombre exacto
        query.registerStoredProcedureParameter("Resultado", Boolean.class, ParameterMode.OUT);

        // Asignamos los valores
        query.setParameter("p_IdCliente", dto.getIdCliente() != null ? dto.getIdCliente() : 0);
        query.setParameter("p_TipoDocumento", dto.getTipoDocumento());
        query.setParameter("p_Documento", dto.getDocumento());
        query.setParameter("p_Nombre", dto.getNombre());
        query.setParameter("p_Apellido", dto.getApellido());
        query.setParameter("p_Correo", dto.getCorreo());
        query.setParameter("p_IdHabitacion", dto.getIdHabitacion());

        // Conversión crítica: LocalDate a java.sql.Date
        query.setParameter("p_FechaSalida", dto.getFechaSalida() != null ? java.sql.Date.valueOf(dto.getFechaSalida()) : null);

        query.setParameter("p_PrecioInicial", dto.getPrecioInicial());
        query.setParameter("p_Adelanto", dto.getAdelanto());
        query.setParameter("p_PrecioRestante", dto.getPrecioRestante());
        query.setParameter("p_Observacion", dto.getObservacion());

        // Ejecutamos
        query.execute();

        // Capturamos el resultado
        Boolean resultado = (Boolean) query.getOutputParameterValue("Resultado");

        if (resultado == null || !resultado) {
            throw new RuntimeException("Error: El procedimiento almacenado no pudo completar el registro.");
        }

        return dto;
    }
    @Override
    @Transactional
    public RecepcionDto actualizar(Integer id, RecepcionDto dto) {
        Recepcion recepcion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        recepcion.setFechaSalidaConfirmacion(dto.getFechaSalidaConfirmacion());
        recepcion.setTotalPagado(dto.getTotalPagado());
        recepcion.setCostoPenalidad(dto.getCostoPenalidad());
        recepcion.setEstado(dto.getEstado());

        return toDto(repository.save(recepcion));
    }
    @Override
    @Transactional
    public Boolean procesarSalida(Integer idRecepcion, Integer idHabitacion, BigDecimal penalidad, BigDecimal total) {
        // 1. Definimos el procedimiento llamando al nombre exacto de la BD
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_RegistrarSalida");

        // 2. Registramos parámetros de ENTRADA
        query.registerStoredProcedureParameter("p_IdRecepcion", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_IdHabitacion", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_CostoPenalidad", BigDecimal.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_TotalPagado", BigDecimal.class, ParameterMode.IN);

        // 3. Registramos el parámetro de SALIDA (Debe llamarse igual que en el Procedure)
        query.registerStoredProcedureParameter("p_Resultado", Boolean.class, ParameterMode.OUT);

        // 4. Asignamos valores
        query.setParameter("p_IdRecepcion", idRecepcion);
        query.setParameter("p_IdHabitacion", idHabitacion);
        query.setParameter("p_CostoPenalidad", penalidad);
        query.setParameter("p_TotalPagado", total);

        // 5. Ejecución
        query.execute();

        // 6. Captura segura del resultado
        Boolean resultado = (Boolean) query.getOutputParameterValue("p_Resultado");

        // Retornamos el resultado del SP, por defecto false si llegara a ser nulo
        return resultado != null && resultado;
    }
    @Override
    @Transactional
    public void eliminar(Integer id) {
        Recepcion recepcion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        recepcion.setEstado(false);
        repository.save(recepcion);
    }

    private RecepcionDto toDto(Recepcion entity) {
        return RecepcionDto.builder()
                .idRecepcion(entity.getIdRecepcion())
                .idCliente(entity.getCliente() != null ? entity.getCliente().getIdPersona() : null)
                .idHabitacion(entity.getHabitacion() != null ? entity.getHabitacion().getIdHabitacion() : null)
                .fechaEntrada(entity.getFechaEntrada())
                .fechaSalida(entity.getFechaSalida())
                .fechaSalidaConfirmacion(entity.getFechaSalidaConfirmacion())
                .precioInicial(entity.getPrecioInicial())
                .adelanto(entity.getAdelanto())
                .precioRestante(entity.getPrecioRestante())
                .totalPagado(entity.getTotalPagado())
                .costoPenalidad(entity.getCostoPenalidad())
                .observacion(entity.getObservacion())
                .estado(entity.getEstado())
                .build();
    }

    // ⚡ MAPEADOR ADICIONAL: Extrae los datos planos de la persona adjunta para que Angular los lea directo en el DTO
    private RecepcionDto toDtoConDatosCliente(Recepcion entity) {

        RecepcionDto dto = toDto(entity);

        if (entity.getCliente() != null) {
            dto.setNombre(entity.getCliente().getNombre());
            dto.setApellido(entity.getCliente().getApellido());
            dto.setDocumento(entity.getCliente().getDocumento());
            dto.setTipoDocumento(entity.getCliente().getTipoDocumento());
            dto.setCorreo(entity.getCliente().getCorreo());
        }

        if (entity.getHabitacion() != null) {

            dto.setNumero(entity.getHabitacion().getNumero());

            dto.setDetalleHabitacion(
                    entity.getHabitacion().getDetalle()
            );

            dto.setPrecioHabitacion(
                    entity.getHabitacion().getPrecio()
            );

            if (entity.getHabitacion().getEstadoHabitacion() != null) {
                dto.setEstadoHabitacion(
                        entity.getHabitacion()
                                .getEstadoHabitacion()
                                .getDescripcion()
                );
            }

            if (entity.getHabitacion().getCategoria() != null) {
                dto.setCategoriaNombre(
                        entity.getHabitacion()
                                .getCategoria()
                                .getDescripcion()
                );
            }

            if (entity.getHabitacion().getPiso() != null) {
                dto.setPisoNombre(
                        entity.getHabitacion()
                                .getPiso()
                                .getDescripcion()
                );
            }
        }

        return dto;
    }
    private Recepcion toEntity(RecepcionDto dto) {
        return Recepcion.builder()
                .idRecepcion(dto.getIdRecepcion())
                .fechaEntrada(dto.getFechaEntrada())
                .fechaSalida(dto.getFechaSalida())
                .fechaSalidaConfirmacion(dto.getFechaSalidaConfirmacion())
                .precioInicial(dto.getPrecioInicial())
                .adelanto(dto.getAdelanto())
                .precioRestante(dto.getPrecioRestante())
                .totalPagado(dto.getTotalPagado())
                .costoPenalidad(dto.getCostoPenalidad())
                .observacion(dto.getObservacion())
                .estado(dto.getEstado())
                .build();
    }
}