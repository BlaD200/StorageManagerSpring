package org.vsynytsyn.storagemanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.UserCredentials;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            UserCredentials userCredentials =
                    new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentials.getUsername(),
                            userCredentials.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            try {
                response.setStatus(403);
                response.addHeader("Content-Type", "text/plain");
                response.getWriter().println(e.getMessage().split("\\(")[0]);
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult
    ) throws IOException {
        UserEntity user = (UserEntity) authResult.getPrincipal();
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() * 1000 + JWTSecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(JWTSecurityConstants.SECRET));

        response.addHeader("Content-Type", "text/plain");
        response.getWriter().println(token);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
