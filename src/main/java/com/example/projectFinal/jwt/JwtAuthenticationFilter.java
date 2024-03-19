package com.example.projectFinal.jwt;


import com.example.projectFinal.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = parseCookie(request);

            System.out.println(token);

            if( token != null && !token.equalsIgnoreCase("null")) {
                //토근이 null이 아니고 "null"문자열이 아닌경우
                ///equalsIgnoreCase: 대소문자 구분 없이 비교

                //userId가져오기
                UserDto.ResDto userId = tokenProvider.validateAndGetUserId(token);

                //인증 완료시 SecurityContextHolder에 등록하여 사용자 인식
                //1. 사용자 정보를 담을 토큰 생성
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null , AuthorityUtils.NO_AUTHORITIES);

                //2. 사용자 인증 세부 설정.
                authentication.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));

                //securityContext는 현재 스레드의 보안 정보를 저장하는 역할을 함
                //SecurityContextHolder.getContext()로 SecurityContext에 접근하고 Authentication에 접근하여 방금 만든 토큰을 등록한다.
                SecurityContextHolder.getContext().setAuthentication(authentication);

                //SecurityContext는 해당 요청에서만 유지되고, 해당 요청의 다른 로직에서 Authentication 객체가 필요할 때 사용되다가,
                //클라이언트의 요청을 모두 처리하고 응답을 리턴하는 어느 시점에 더이상 Authentication 객체가 필요없을때 자동으로 삭제된다


            }


        } catch (Exception ex) {

        }

        filterChain.doFilter(request, response);

    }

    private String parseCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
