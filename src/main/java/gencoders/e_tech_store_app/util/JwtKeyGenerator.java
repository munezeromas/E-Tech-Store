package gencoders.e_tech_store_app.util;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Generate a secure 256-bit key
        String secret = Encoders.BASE64.encode(
                Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()
        );
        System.out.println("Generated JWT Secret: " + secret);
    }
}