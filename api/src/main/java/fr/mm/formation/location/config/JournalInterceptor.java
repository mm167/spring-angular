package fr.mm.formation.location.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class JournalInterceptor implements HandlerInterceptor {

    private static final Logger JOURNAL = LoggerFactory.getLogger(JournalInterceptor.class);
    private static final String ATTRIBUT_DEBUT = JournalInterceptor.class.getName() + ".debut";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(ATTRIBUT_DEBUT, System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long debut = (long) request.getAttribute(ATTRIBUT_DEBUT);
        long dureeMs = (System.nanoTime() - debut) / 1_000_000;
        JOURNAL.info("{} {} -> {} ({} ms)",
                request.getMethod(), request.getRequestURI(), response.getStatus(), dureeMs);
    }
}