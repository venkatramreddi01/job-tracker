package jobtracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * PROBLEM: Check every incoming request for a valid JWT, and if found, tell Spring
 * Security who this request belongs to.
 *
 * APPROACH: Extend OncePerRequestFilter (runs once per request). Read the
 * Authorization header, strip "Bearer ", validate the token, and if valid,
 * register the user as authenticated for THIS request only.
 *
 * WHY IT WORKS: This plugs directly into Spring Security's filter chain — once we
 * mark someone authenticated here, .anyRequest().authenticated() in SecurityConfig
 * lets the request through to the controller. Built manually in SecurityConfig
 * (NOT annotated @Component) to avoid Spring Boot's automatic duplicate filter
 * registration, which caused a real bug on Day 22.
 *
 * TIME: O(1) per request | SPACE: O(1)
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // strip "Bearer " (7 characters)

            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // pass control to the next filter/controller
    }
}