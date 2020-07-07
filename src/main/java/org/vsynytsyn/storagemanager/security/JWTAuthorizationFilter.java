package org.vsynytsyn.storagemanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.vsynytsyn.storagemanager.security.JWTSecurityConstants.JWT_TOKEN_PREFIX;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserDetailsServiceImpl userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager,
                                  UserDetailsServiceImpl userDetailsService) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String header = request.getHeader(JWTSecurityConstants.HEADER_NAME);
        if (header == null || !header.startsWith(JWT_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken token = authenticationToken(header);
            SecurityContextHolder.getContext().setAuthentication(token);
            chain.doFilter(request, response);
        } catch (UsernameNotFoundException e){
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
        }
    }


    private UsernamePasswordAuthenticationToken authenticationToken(String header) {
        String username = JWT.require(Algorithm.HMAC256(JWTSecurityConstants.SECRET))
                .build()
                .verify(header.replace(JWT_TOKEN_PREFIX, ""))
                .getSubject();
        if (username != null) {
            // Should be user id to automatically map as @AuthenticationPrincipal
//            Long id = userDetailsService.getUserID(username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities());
        }
        return null;
    }

}
