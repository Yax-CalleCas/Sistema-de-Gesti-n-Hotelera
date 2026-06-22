package com.hotel.cibertec.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.hotel.cibertec.exception.BusinessException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
public class SocialAuthValidator {

    // Reemplaza con tu CLIENT_ID de Google desde Google Cloud Console
    private static final String GOOGLE_CLIENT_ID = "TU_GOOGLE_CLIENT_ID_AQUI.apps.googleusercontent.com";

    public boolean isValidToken(String token, String provider) {
        try {
            if ("GOOGLE".equalsIgnoreCase(provider)) {
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                        .build();

                GoogleIdToken idToken = verifier.verify(token);
                return idToken != null;

            } else if ("FACEBOOK".equalsIgnoreCase(provider)) {
                FacebookClient facebookClient = new DefaultFacebookClient(token, Version.LATEST);
                // Si esto no lanza excepción, el token es válido
                return facebookClient.fetchObject("me", com.restfb.types.User.class) != null;
            }
        } catch (Exception e) {
            throw new BusinessException("Token de " + provider + " inválido o expirado");
        }
        return false;
    }
}