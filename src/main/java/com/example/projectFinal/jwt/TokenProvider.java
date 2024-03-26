package com.example.projectFinal.jwt;


import com.example.projectFinal.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@ConfigurationProperties
@Component
public class TokenProvider {

    private final Key jwt_key;

    public TokenProvider(@Value("${jwt.secret.key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.jwt_key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 로그인 시
    public UserDto.TokenDto generateToken(String userId) {
        long now = (new Date().getTime());

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
//                .claim("auth", authorities)
//                .setExpiration(new Date(now + 1800 * 1000))
                .setExpiration(new Date(now + 1800000))
                .signWith(jwt_key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400 * 7 * 1000))
                .signWith(jwt_key, SignatureAlgorithm.HS256)
                .compact();

        return UserDto.TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //validateAndGetUserId(): 토근 디코딩 및 파싱하고 토큰 위조 여부 확인 -> 사용자 아이디 리턴
    public UserDto.ResDto validateAndGetUserId(String token) {
        UserDto.ResDto result = new UserDto.ResDto();

        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwt_key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();

            Date currentDate = new Date();

            boolean isValid = expirationDate.after(currentDate);

            String userId = claims.getSubject();

            System.out.println("토큰에서 유저 ID 추출" + userId);

            result.setResult(true);
            result.setMsg(userId);

            return result;

        } catch(ExpiredJwtException | IllegalArgumentException e){
            System.out.println(e);
            result.setResult(false);

            return result;
        }

    }

    // authuser
    public UserDto.TokenDto generateAccessToken(String userId) {
        long now = (new Date().getTime());

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
//                .claim("auth", authorities)
//                .setExpiration(new Date(now + 1800 * 1000))
                .setExpiration(new Date(now + 1800000))
                .signWith(jwt_key, SignatureAlgorithm.HS256)
                .compact();

        return UserDto.TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }




}
