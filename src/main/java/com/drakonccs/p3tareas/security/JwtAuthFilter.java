package com.drakonccs.p3tareas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.drakonccs.p3tareas.repository.UserRepository;
import com.drakonccs.p3tareas.entity.User;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {
        // IGNORAR swagger y openapi docs y login
        String path = request.getRequestURI();
        if (path.startsWith("/auth") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }
        // obtener el header que contiene el jwt
        String authHeader=request.getHeader("Authorization");//Bearer jwt
        if(authHeader==null||!authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        // obtener jwt desde header
        String jwt=authHeader.split(" ")[1];
        // obtener subject/username desde el jwt
        String username=jwtService.extractUsername(jwt);
        // Setear un objeto authentication dentro del securitycontext
        User user=userRepository.findByUsername(username).get();
        if (user != null&& SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        //ejecutar el resto de filtros
        filterChain.doFilter(request, response);
    }
}
