package gencoders.e_tech_store_app.auth;

import lombok.Getter;
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
        if (entry == null || entry.expiry().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false;
        }
        boolean isValid = entry.otp().equals(otp);
        if (isValid) otpStore.remove(email);
        return isValid;
    }

    public boolean hasPendingAuth(String email) {
        OtpEntry entry = otpStore.get(email);
        return entry != null && entry.expiry().isAfter(LocalDateTime.now());
    }

    public String resendOtp(String email) {
        if (!hasPendingAuth(email)) {
            throw new RuntimeException("No pending authentication found for email: " + email);
        }
        return generateOtp(email);
    }

    private record OtpEntry(String otp, LocalDateTime expiry) {
    }
}