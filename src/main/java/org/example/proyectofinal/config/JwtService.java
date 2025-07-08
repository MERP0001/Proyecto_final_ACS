package org.example.proyectofinal.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

   @Value("${app.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
   private String secretKey;

   @Value("${app.jwt.expiration:86400000}") // 24 horas por defecto
   private Long accessTokenExpiration;

   @Value("${app.jwt.refresh-token.expiration:604800000}") // 7 días por defecto
   private Long refreshTokenExpiration;

   public String generateAccessToken(UserDetails userDetails) {
      return generateToken(new HashMap<>(), userDetails, accessTokenExpiration);
   }

   public String generateRefreshToken(UserDetails userDetails) {
      return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
   }

   private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expiration) {
      Instant now = Instant.now();
      var authorities = userDetails.getAuthorities()
            .stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .toList();

      return Jwts.builder()
            .claims(extraClaims)
            .claim("authorities", authorities)
            .subject(userDetails.getUsername())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expiration, ChronoUnit.MILLIS)))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
   }

   public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);
   }

   public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
   }

   private Claims extractAllClaims(String token) {
      try {
         return Jwts.parser()
               .verifyWith(getSigningKey())
               .build()
               .parseSignedClaims(token)
               .getPayload();
      } catch (ExpiredJwtException e) {
         log.warn("Token expirado: {}", e.getMessage());
         throw e;
      } catch (SignatureException e) {
         log.error("Firma del token inválida: {}", e.getMessage());
         throw e;
      } catch (MalformedJwtException e) {
         log.error("Token malformado: {}", e.getMessage());
         throw e;
      } catch (Exception e) {
         log.error("Error al procesar el token: {}", e.getMessage());
         throw e;
      }
   }

   public boolean isTokenValid(String token, UserDetails userDetails) {
      try {
         final String username = extractUsername(token);
         return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
      } catch (JwtException e) {
         log.warn("Token inválido: {}", e.getMessage());
         return false;
      }
   }

   private boolean isTokenExpired(String token) {
      try {
         return extractExpiration(token).before(new Date());
      } catch (ExpiredJwtException e) {
         return true;
      }
   }

   private Date extractExpiration(String token) {
      return extractClaim(token, Claims::getExpiration);
   }

   private SecretKey getSigningKey() {
      byte[] keyBytes = secretKey.getBytes();
      return Keys.hmacShaKeyFor(keyBytes);
   }

   public Long getAccessTokenExpiration() {
      return accessTokenExpiration;
   }

   public Long getRefreshTokenExpiration() {
      return refreshTokenExpiration;
   }
}