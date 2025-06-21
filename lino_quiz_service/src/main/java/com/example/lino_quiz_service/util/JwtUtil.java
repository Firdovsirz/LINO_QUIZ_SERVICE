package com.example.lino_quiz_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your-256-bit-secret";
    private static final long EXPIRATION_TIME = 3600000;

    // Create JWT Token Manually
    public static String generateToken(String email, String roles) {
        try {
            long issuedAt = System.currentTimeMillis();
            long expiresAt = issuedAt + EXPIRATION_TIME;

            Map<String, Object> payload = Map.of(
                    "email", email,
                    "roles", roles,
                    "iat", issuedAt,
                    "exp", expiresAt
            );

            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payloadJson = new ObjectMapper().writeValueAsString(payload);

            String encodedHeader = base64UrlEncode(headerJson);
            String encodedPayload = base64UrlEncode(payloadJson);
            String signature = createSignature(encodedHeader + "." + encodedPayload);

            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    // Validate JWT Token Manually

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String expectedSignature = createSignature(header + "." + payload);
            if (!expectedSignature.equals(signature)) return false;

            Map<String, Object> payloadMap = new ObjectMapper().readValue(base64UrlDecode(payload), Map.class);
            long exp = ((Number) payloadMap.get("exp")).longValue();

            return exp > System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

    // Extract Email
    public String extractEmail(String token) {
        try {
            String payload = token.split("\\.")[1];
            Map<String, Object> payloadMap = new ObjectMapper().readValue(base64UrlDecode(payload), Map.class);
            return (String) payloadMap.get("email");
        } catch (Exception e) {
            return null;
        }
    }

    // Extract isFrozen
    public Boolean exctractFrozen(String token) {
        try {
            String payload = token.split("\\.")[1];
            Map<String, Object> payloadMap = new ObjectMapper().readValue(base64UrlDecode(payload), Map.class);
            return (Boolean) payloadMap.get("isFrozen");
        } catch (Exception e) {
            return null;
        }
    }

    // Extract Roles
    public List<String> extractRoles(String token) {
        try {
            String payload = token.split("\\.")[1];
            Map<String, Object> payloadMap = new ObjectMapper().readValue(base64UrlDecode(payload), Map.class);
            return ((List<?>) payloadMap.get("roles")).stream().map(Object::toString).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    // Create HMAC-SHA256 Signature
    private static String createSignature(String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return base64UrlEncode(Arrays.toString(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8))));
    }

    // Base64 URL Encode
    private static String base64UrlEncode(String data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    // Base64 URL Decode
    private String base64UrlDecode(String data) {
        return new String(Base64.getUrlDecoder().decode(data), StandardCharsets.UTF_8);
    }
}