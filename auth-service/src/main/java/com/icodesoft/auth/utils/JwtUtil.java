package com.icodesoft.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class JwtUtil {

    // Secret Key for signing the JWT. It should be kept private.
    private static final String SECRET = "TmV3U2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzMTIzNDU2Nzg=\r\n" + "";


    /**
     *
     * @param subject subject
     * @param expire expire time until minutes
     * @return JWT token
     */
    public static String generateToken(String subject, long expire) {
        // Prepare claims for the token
        Map<String, Object> claims = new HashMap<>();

        // Build JWT token with claims, subject, issued time, expiration time, and signing algorithm
        // Token valid for 3 minutes
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setId(UUID.randomUUID().toString())
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("icodesoft")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expire))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }


    // Creates a signing key from the base64 encoded secret.
    //returns a Key object for signing the JWT.
    private static Key getSignKey() {
        // Decode the base64 encoded secret key and return a Key object
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // Extracts the userName from the JWT token.
    //return -> The userName contained in the token.
    public static String extractUserName(String token) {
        // Extract and return the subject claim from the token
        return extractClaim(token, Claims::getSubject);
    }


    // Extracts the expiration date from the JWT token.
    //@return The expiration date of the token.
    public static Date extractExpiration(String token) {
        // Extract and return the expiration claim from the token
        return extractClaim(token, Claims::getExpiration);
    }


    // Extracts a specific claim from the JWT token.
    // claimResolver A function to extract the claim.
    // return-> The value of the specified claim.
    private static <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        // Extract the specified claim using the provided function
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    //Extracts all claims from the JWT token.
    //return-> Claims object containing all claims.
    private static Claims extractAllClaims(String token) {
        // Parse and return all claims from the token
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token).getBody();
    }


    //Checks if the JWT token is expired.
    //return-> True if the token is expired, false otherwise.
    public static Boolean isTokenExpired(String token) {
        // Check if the token's expiration time is before the current time
        return extractExpiration(token).before(new Date());
    }

    //Validates the JWT token against the UserDetails.
    //return-> True if the token is valid, false otherwise.

    public static Boolean validateToken(String token, UserDetails userDetails) {
        // Extract username from token and check if it matches UserDetails' username
        final String userName = extractUserName(token);
        // Also check if the token is expired
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
