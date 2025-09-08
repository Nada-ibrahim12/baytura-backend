package org.os.bayturabackend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.os.bayturabackend.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("username", user.getRealUsername());
        claims.put("role", user.getRole().name());
        claims.put("lastCredentialsUpdate", user.getLastCredentialsUpdate().getEpochSecond());
        return generateToken(claims, user);
    }


    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignInKey() , SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String email = extractEmail(token);

        if (!email.equals(userDetails.getUsername()) || isTokenExpired(token)) {
            return false;
        }


        if (userDetails instanceof User user) {
            Long tokenUpdateTime = extractClaim(
                    token,
                    claims -> claims.get("lastCredentialsUpdate", Long.class)
            );

            if (tokenUpdateTime == null){
                return false;
            }

            long userUpdateTime = user.getLastCredentialsUpdate().getEpochSecond();

            return tokenUpdateTime >= userUpdateTime;
        }

        return true;
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractEmail(String jwt){
        return extractClaim(jwt, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt){
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        }
        catch (Exception e){
            throw new JwtException("Invalid JWT token", e);
        }
    }

    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}