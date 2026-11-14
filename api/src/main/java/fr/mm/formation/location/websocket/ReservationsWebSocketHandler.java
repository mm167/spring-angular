package fr.mm.formation.location.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ReservationsWebSocketHandler extends TextWebSocketHandler {

    private static final Logger JOURNAL = LoggerFactory.getLogger(ReservationsWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final JsonMapper mapper;

    public ReservationsWebSocketHandler(JsonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        JOURNAL.info("WebSocket ouverte : {} ({} client(s))", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus statut) {
        sessions.remove(session);
        JOURNAL.info("WebSocket fermée : {} ({} client(s))", session.getId(), sessions.size());
    }

    public void diffuser(String type, Object contenu) {
        TextMessage message = new TextMessage(mapper.writeValueAsString(new Evenement(type, contenu)));
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                synchronized (session) {
                    session.sendMessage(message);
                }
            } catch (IOException exception) {
                JOURNAL.warn("Envoi impossible à {}", session.getId(), exception);
            }
        }
    }

    public record Evenement(String type, Object contenu) {
    }
}