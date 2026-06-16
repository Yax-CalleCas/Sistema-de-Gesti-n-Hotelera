package com.hotel.cibertec.service.impl;

import com.hotel.cibertec.entity.ImagenHabitacion;
import com.hotel.cibertec.repository.ImagenHabitacionRepository;
import com.hotel.cibertec.repository.HabitacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImagenHabitacionService {

    private final ImagenHabitacionRepository repository;
    private final HabitacionRepository habitacionRepository;

    public ImagenHabitacionService(ImagenHabitacionRepository repo, HabitacionRepository habRepo) {
        this.repository = repo;
        this.habitacionRepository = habRepo;
    }

    @Transactional
    public void agregarImagen(Integer habitacionId, String url) {
        var habitacion = habitacionRepository.findById(habitacionId).orElseThrow();
        var imagen = ImagenHabitacion.builder()
                .urlImagen(url)
                .habitacion(habitacion)
                .build();
        repository.save(imagen);
    }

    @Transactional
    public void eliminarImagen(Integer imagenId) {
        repository.deleteById(imagenId);
    }
}