package gencoders.e_tech_store_app.auth;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthSessionService {

    private final Map<String, AuthSession> activeSessions = new HashMap<>();
    private final Map<String, String> userSessionMap = new HashMap<>(); // email -> sessionId

    public String createSession(String email, String jwt, Long userId) {
        String sessionId = UUID.randomUUID().toString();

        // Remove any existing session for this user
        String existingSessionId = userSessionMap.get(email);
        if (existingSessionId != null) {
            activeSessions.remove(existingSessionId);
        }

        // Create new session
        AuthSession session = new AuthSession(sessionId, email, jwt, userId, LocalDateTime.now().plusHours(24));
        activeSessions.put(sessionId, session);
        userSessionMap.put(email, sessionId);

        return sessionId;
    }

    public AuthSession getSession(String sessionId) {
        AuthSession session = activeSessions.get(sessionId);
        if (session == null || session.expiry().isBefore(LocalDateTime.now())) {
            if (session != null) {
                removeSession(sessionId);
            }
            return null;
        }
        return session;
    }

    public boolean isSessionValid(String sessionId) {
        return getSession(sessionId) != null;
    }

    public void removeSession(String sessionId) {
        AuthSession session = activeSessions.remove(sessionId);
        if (session != null) {
            userSessionMap.remove(session.email());
        }
    }

    public void removeUserSession(String email) {
        String sessionId = userSessionMap.remove(email);
        if (sessionId != null) {
            activeSessions.remove(sessionId);
        }
    }

    public String refreshSession(String sessionId) {
        AuthSession session = getSession(sessionId);
        if (session == null) {
            return null;
        }

        // Create new session with extended expiry
        return createSession(session.email(), session.jwt(), session.userId());
    }

    public AuthSession getSessionByEmail(String email) {
        String sessionId = userSessionMap.get(email);
        return sessionId != null ? getSession(sessionId) : null;
    }

    public record AuthSession(String sessionId, String email, String jwt, Long userId, LocalDateTime expiry) {}
}