package gencoders.e_tech_store_app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    public void log(String action, String username, String ip) {
        logger.info("Action: {}, User: {}, IP: {}, Timestamp: {}", action, username, ip, LocalDateTime.now());
    }
}
