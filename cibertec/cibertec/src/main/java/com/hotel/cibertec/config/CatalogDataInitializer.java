package com.hotel.cibertec.config;

import com.hotel.cibertec.entity.*;
import com.hotel.cibertec.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
//@Profile("!test")  // desactivado para pdoer hacer prubeas unitarias
@Order(2)
public class CatalogDataInitializer implements CommandLineRunner {

    private final EstadoHabitacionRepository estadoHabitacionRepository;
    private final CategoriaRepository categoriaRepository;
    private final PisoRepository pisoRepository;
    private final ProductoRepository productoRepository;
    private final HabitacionRepository habitacionRepository;

    @Override
    public void run(String... args) {

        try {

            System.out.println("=================================");
            System.out.println("CARGANDO DATOS HOTEL...");
            System.out.println("=================================");

            cargarEstadosHabitacion();
            cargarCategorias();
            cargarPisos();
            cargarProductos();
            cargarHabitaciones();

            System.out.println("=================================");
            System.out.println("DATOS HOTEL CARGADOS");
            System.out.println("=================================");

        } catch (Exception e) {

            System.err.println("ERROR CARGANDO DATOS HOTEL");
            e.printStackTrace();

        }
    }

    private void cargarEstadosHabitacion() {
        if (estadoHabitacionRepository.count() > 0) {
            return;
        }

        // ELIMINA EL .idEstadoHabitacion(1)
        estadoHabitacionRepository.save(
                EstadoHabitacion.builder()
                        .descripcion("DISPONIBLE")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        estadoHabitacionRepository.save(
                EstadoHabitacion.builder()
                        .descripcion("OCUPADO")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );
        estadoHabitacionRepository.save(
                EstadoHabitacion.builder()
                        .descripcion("MANTENIMIENTO")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );
        // ... etc
    }

    private void cargarCategorias() {

        if (categoriaRepository.count() > 0) {
            return;
        }

        categoriaRepository.save(
                Categoria.builder()
                        .descripcion("Matrimonial")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        categoriaRepository.save(
                Categoria.builder()
                        .descripcion("Doble")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        categoriaRepository.save(
                Categoria.builder()
                        .descripcion("Individual")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        System.out.println("Categorías creadas.");
    }

    private void cargarPisos() {

        if (pisoRepository.count() > 0) {
            return;
        }

        pisoRepository.save(
                Piso.builder()
                        .descripcion("PRIMERO")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        pisoRepository.save(
                Piso.builder()
                        .descripcion("SEGUNDO")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        pisoRepository.save(
                Piso.builder()
                        .descripcion("TERCERO")
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        System.out.println("Pisos creados.");
    }

    private void cargarProductos() {

        if (productoRepository.count() > 0) {
            return;
        }

        productoRepository.save(
                Producto.builder()
                        .nombre("GALLETAS DORAS")
                        .detalle("NINGUNA")
                        .precio(BigDecimal.valueOf(0.70))
                        .cantidad(50)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("REFRESCO POCMAS")
                        .detalle("350 ML")
                        .precio(BigDecimal.valueOf(1.50))
                        .cantidad(80)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("CHOCOLATE DMX")
                        .detalle("50 GRM")
                        .precio(BigDecimal.valueOf(0.80))
                        .cantidad(60)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("PAPAS DORADAS")
                        .detalle("150 GRM")
                        .precio(BigDecimal.valueOf(2.60))
                        .cantidad(20)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("REFRESCO OXA")
                        .detalle("300 ML")
                        .precio(BigDecimal.valueOf(2))
                        .cantidad(30)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("CIGARRILLOS DEM")
                        .detalle("10 UNID")
                        .precio(BigDecimal.valueOf(3.50))
                        .cantidad(55)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("AGUA LIFE")
                        .detalle("250 ML")
                        .precio(BigDecimal.valueOf(3))
                        .cantidad(45)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("GASEOSA ALMOADA")
                        .detalle("350 ML")
                        .precio(BigDecimal.valueOf(4.50))
                        .cantidad(30)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("CEREALES PANDA")
                        .detalle("NIN")
                        .precio(BigDecimal.valueOf(2.70))
                        .cantidad(40)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        productoRepository.save(
                Producto.builder()
                        .nombre("SHAMPOO GH")
                        .detalle("200 ML")
                        .precio(BigDecimal.valueOf(2.50))
                        .cantidad(20)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );

        System.out.println("Productos creados.");
    }

    private void cargarHabitaciones() {

        if (habitacionRepository.count() > 0) {
            return;
        }

        EstadoHabitacion disponible =
                estadoHabitacionRepository.findById(1).orElseThrow();

        Piso piso1 = pisoRepository.findById(1).orElseThrow();
        Piso piso2 = pisoRepository.findById(2).orElseThrow();
        Piso piso3 = pisoRepository.findById(3).orElseThrow();

        Categoria matrimonial = categoriaRepository.findById(1).orElseThrow();
        Categoria doble = categoriaRepository.findById(2).orElseThrow();
        Categoria individual = categoriaRepository.findById(3).orElseThrow();

        crearHabitacion("001","WIFI + BAÑO + TV + CABLE",70,disponible,piso1,individual);
        crearHabitacion("002","WIFI + BAÑO + TV + CABLE",80,disponible,piso1,doble);
        crearHabitacion("003","BAÑO + TV + CABLE",60,disponible,piso1,individual);
        crearHabitacion("004","WIFI + BAÑO + TV + CABLE",80,disponible,piso1,doble);
        crearHabitacion("005","WIFI + BAÑO",50,disponible,piso1,individual);

        crearHabitacion("006","WIFI + BAÑO + TV 4K + CABLE",80,disponible,piso2,individual);
        crearHabitacion("007","WIFI + BAÑO + TV 4K + CABLE",90,disponible,piso2,doble);
        crearHabitacion("008","WIFI + BAÑO + TV + CABLE",70,disponible,piso2,individual);
        crearHabitacion("009","WIFI + BAÑO + TV + CABLE",80,disponible,piso2,doble);
        crearHabitacion("010","WIFI + BAÑO + TV + CABLE",70,disponible,piso2,individual);

        crearHabitacion("011","WIFI + BAÑO + TV 4K + CABLE",120,disponible,piso3,matrimonial);
        crearHabitacion("012","WIFI + BAÑO + TV 4K + CABLE",120,disponible,piso3,matrimonial);
        crearHabitacion("013","WIFI + BAÑO + TV 4K + CABLE",120,disponible,piso3,matrimonial);
        crearHabitacion("014","WIFI + BAÑO + TV + CABLE",85,disponible,piso3,doble);
        crearHabitacion("015","WIFI + BAÑO + TV + CABLE",75,disponible,piso3,individual);

        System.out.println("Habitaciones creadas.");
    }

    private void crearHabitacion(
            String numero,
            String detalle,
            double precio,
            EstadoHabitacion estado,
            Piso piso,
            Categoria categoria) {

        habitacionRepository.save(
                Habitacion.builder()
                        .numero(numero)
                        .detalle(detalle)
                        .precio(BigDecimal.valueOf(precio))
                        .estadoHabitacion(estado)
                        .piso(piso)
                        .categoria(categoria)
                        .estado(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
        );
    }
}