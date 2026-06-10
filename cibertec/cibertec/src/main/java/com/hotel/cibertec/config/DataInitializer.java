package com.hotel.cibertec.config;

import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.entity.TipoPersona;
import com.hotel.cibertec.repository.PersonaRepository;
import com.hotel.cibertec.repository.TipoPersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TipoPersonaRepository tipoPersonaRepository;
    private final PersonaRepository personaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("--- INICIANDO CARGA DE DATOS ---");

        // 1. Cargar tipos de persona de forma segura
        crearTipoPersonaSiNoExiste(1, "Administrador");
        crearTipoPersonaSiNoExiste(2, "Empleado");
        crearTipoPersonaSiNoExiste(3, "Cliente");

        // 2. Cargar usuarios de forma segura (por correo)
        crearUsuarioSiNoExiste("4545453", "Naomi", "Konoe", "Konoe@gmail.com", "123123", 1);
        crearUsuarioSiNoExiste("4353434", "Mizuki", "Hayashi", "Mizuki@gmail.com", "456456", 2);

        System.out.println("--- PROCESO DE CARGA FINALIZADO ---");
    }

    private void crearTipoPersonaSiNoExiste(Integer id, String desc) {
        if (tipoPersonaRepository.findById(id).isEmpty()) {
            tipoPersonaRepository.save(TipoPersona.builder()
                    .idTipoPersona(id)
                    .descripcion(desc)
                    .estado(true)
                    .fechaCreacion(LocalDateTime.now())
                    .build());
            System.out.println("Tipo creado: " + desc);
        }
    }

    private void crearUsuarioSiNoExiste(String doc, String nom, String ape, String correo, String clave, Integer idTipo) {
        if (personaRepository.findByCorreo(correo).isEmpty()) {
            TipoPersona tipo = tipoPersonaRepository.findById(idTipo)
                    .orElseThrow(() -> new RuntimeException("Tipo de persona no encontrado: " + idTipo));

            personaRepository.save(Persona.builder()
                    .documento(doc)
                    .nombre(nom)
                    .apellido(ape)
                    .correo(correo)
                    .clave(passwordEncoder.encode(clave))
                    .tipoPersona(tipo)
                    .estado(true)
                    .fechaCreacion(LocalDateTime.now())
                    .build());
            System.out.println("Usuario creado: " + correo);
        } else {
            System.out.println("Usuario ya existente, saltando: " + correo);
        }
    }
}