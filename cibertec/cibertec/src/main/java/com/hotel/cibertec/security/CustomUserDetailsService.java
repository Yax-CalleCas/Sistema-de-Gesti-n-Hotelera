package com.hotel.cibertec.security;

import com.hotel.cibertec.entity.Persona;
import com.hotel.cibertec.repository.PersonaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonaRepository personaRepository;

    public CustomUserDetailsService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Usamos el nuevo método optimizado con JOIN FETCH
        Persona persona = personaRepository.findByCorreoWithTipo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + username));

        // Ahora, al usar JOIN FETCH, persona.getTipoPersona() NO es un proxy,
        // por lo que acceder a getDescripcion() es seguro y no lanza LazyInitializationException.
        String rolFormateado =
                "ROLE_" +
                        persona.getTipoPersona()
                                .getDescripcion()
                                .trim()
                                .replace(" ", "_")
                                .toUpperCase();

        return new User(
                persona.getCorreo(),
                persona.getClave(),
                Collections.singletonList(new SimpleGrantedAuthority(rolFormateado))
        );
    }
}