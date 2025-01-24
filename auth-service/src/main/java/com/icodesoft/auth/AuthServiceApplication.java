package com.icodesoft.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import io.jsonwebtoken.security.Keys;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		codeChallengeGenerator();
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	public static String codeChallengeGenerator() throws NoSuchAlgorithmException {
		String token = "eyJraWQiOiJ4bk9tbnJoZmkwbHlMMDExbTl2OWF5cDdDTGlDY3ZWaEQwUlJjbzc0YUU4IiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULmYtYlhkVkpQMUlkbGRrLVhrOEdlUWdjd194VTZIWFpzMHhSdTJ4LU8tZTgub2FyNDE1Z2t3NVR4UU5kNkUycDciLCJpc3MiOiJodHRwczovL2Jyb2FkY29tLm9rdGEuY29tL29hdXRoMi9kZWZhdWx0IiwiYXVkIjoiYXBpOi8vZGVmYXVsdCIsImlhdCI6MTczNzYyMzg1MiwiZXhwIjoxNzM3NjI3NDUyLCJjaWQiOiIwb2ExMG00NGh2elRqMzFGejJwOCIsInVpZCI6IjAwdXFlYTlxeml6dVNLNHduMnA3Iiwic2NwIjpbIm9mZmxpbmVfYWNjZXNzIl0sImF1dGhfdGltZSI6MTczNzYyMzg1MCwic3ViIjoiZ2cwNTk0MjFAYnJvYWRjb20ubmV0Iiwic3ViX2VtYWlsIjoiZ2FyeS5nYW9AYnJvYWRjb20uY29tIn0.GRxqQTTxi_7Sg3lEYVfMaQFjIe9uvkrE_oV301HFG4Y6c-lvAzhUkLwtj2C0hGhIfmSn6AGuN-93OSfop0VGBWtnE0SNds9O5Ukgdq4dNxK5-KyZhdBZWkZpvfeK47BcQYoqyYQlsgJW5kkPK-iBiGwC6XhKMk7ne5uULJ6wvcATOJM3Y0DAovh2kiZKZpiewe2247ms-F7JEf7fSrmzhF1gCMCFXD1LAMM6W_EpqteD56ItVFEqFgCTplwcJMx-NUnAXDz-gJ7gSDMayYTZlsis1AomCY8WdYGo76gZvvqQI_JvKXm5xXFj51V91QKGW3ZzlREYx9lPwMokHsCvPQ";
//		String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InNpZ25pbmdfMiJ9";
		DefaultJwsHeader header = (DefaultJwsHeader) Jwts.parserBuilder()
				.build()
				.parseClaimsJwt(token.split("\\.")[0] + "." +token.split("\\.")[1] +".") // 不需要完整的 Token
						.getHeader();
		System.out.println("kid: " + header.toString());
		//	S256       code_challenge = BASE64URL-ENCODE(SHA256(ASCII(code_verifier)));
		String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes());
		System.out.println(codeVerifier);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
		String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
		System.out.println(codeChallenge);
		return codeChallenge;
	}
}
