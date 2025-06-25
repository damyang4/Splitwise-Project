package command.session;

import user.User;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final ConcurrentHashMap<String, String> activeSessions = new ConcurrentHashMap<>();

    public String login(User user) {
        String sessionToken = UUID.randomUUID().toString();
        activeSessions.put(sessionToken, user.username());
        return sessionToken;
    }

    public boolean isAuthenticated(String sessionToken) {
        if (sessionToken == null) return false;
        return activeSessions.containsKey(sessionToken);
    }

    public String getUsername(String sessionToken) {
        return activeSessions.get(sessionToken);
    }
}

