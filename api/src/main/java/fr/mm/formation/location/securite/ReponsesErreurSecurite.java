package fr.mm.formation.location.securite;

import java.io.IOException;

import fr.mm.formation.location.contrat.modele.Erreur;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ReponsesErreurSecurite implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper mapper;

    public ReponsesErreurSecurite(JsonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        ecrire(response, HttpStatus.UNAUTHORIZED, "Authentification requise ou invalide.");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        ecrire(response, HttpStatus.FORBIDDEN, "Accès refusé : droits insuffisants.");
    }

    private void ecrire(HttpServletResponse response, HttpStatus statut, String message) throws IOException {
        Erreur erreur = new Erreur();
        erreur.setStatus(statut.value());
        erreur.setMessage(message);
        response.setStatus(statut.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(erreur));
    }
}