package gencoders.e_tech_store_app.auth;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LoginAttemptService {
    private final ConcurrentMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;

    public void loginFailed(String key) {
        attemptsCache.merge(key, 1, Integer::sum);
    }

    public boolean isBlocked(String key) {
        return attemptsCache.getOrDefault(key, 0) >= MAX_ATTEMPTS;
    }

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }
}