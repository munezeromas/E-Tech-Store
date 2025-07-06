package gencoders.e_tech_store_app.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {

    private final Map<String, OtpEntry> otpStore = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpEntry entry = otpStore.get(email);
        if (entry == null || entry.getExpiry().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false;
        }
        boolean isValid = entry.getOtp().equals(otp);
        if (isValid) otpStore.remove(email);
        return isValid;
    }

    private static class OtpEntry {
        private final String otp;
        private final LocalDateTime expiry;

        public OtpEntry(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiry() {
            return expiry;
        }
    }
}
