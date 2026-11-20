package fr.mm.formation.location.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import fr.mm.formation.location.comptes.Compte;
import fr.mm.formation.location.comptes.ComptesService;
import fr.mm.formation.location.erreurs.IdentifiantDejaPrisException;
import fr.mm.formation.location.securite.JetonService;
import fr.mm.formation.location.securite.ReponsesErreurSecurite;
import fr.mm.formation.location.securite.SecuriteConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(AuthentificationController.class)
@Import({SecuriteConfig.class, ReponsesErreurSecurite.class})
class AuthentificationControllerTest {

    private static final String INSCRIPTION = "{\"identifiant\":\"martin\",\"motDePasse\":\"motdepasse1\"}";

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ComptesService comptes;

    @MockitoBean
    private AuthenticationManager gestionnaire;

    @MockitoBean
    private JetonService jetons;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void inscrireRetourne201AvecLeRoleClient() {
        given(comptes.inscrire("martin", "motdepasse1"))
                .willReturn(new Compte("martin", "{bcrypt}hache", "CLIENT"));

        assertThat(mvc.post().uri("/api/auth/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(INSCRIPTION))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.role").isEqualTo("CLIENT");

        verify(comptes).inscrire("martin", "motdepasse1");
    }

    @Test
    void inscrireUnIdentifiantPrisRetourne409() {
        given(comptes.inscrire("martin", "motdepasse1"))
                .willThrow(new IdentifiantDejaPrisException("martin"));

        assertThat(mvc.post().uri("/api/auth/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(INSCRIPTION))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.status").isEqualTo(409);
    }
}