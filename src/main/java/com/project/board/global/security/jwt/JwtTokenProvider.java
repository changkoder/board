package com.project.board.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity){
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }
    
    public String createAccessToken(Long userId, String role){
        return createToken(userId, role, accessTokenValidity);
    }

    public String createRefreshToken(Long userId, String role) {
        return createToken(userId, role, refreshTokenValidity);
    }

    public Long getUserId(String token){
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject()); //왜 parselong 하지? 그리고 getsubject가 아이디 가져오는 메서드인가
    }

    public String getRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class); //그냥 겟 메서드하면 역할을 가져오나? 메서드 네임이 너무 포괄적인거같다
    }

    public boolean validateToken(String token){
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private String createToken(Long userId, String role, long validity) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
