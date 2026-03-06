package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
//Har request aati hi:
//Header se token nikaalo
//Username nikaalo
//DB se userDetails load karo
//Token valid?
//Haan → SecurityContext me user set karo
//Aage controller me request jaati hai
//Yeh har request ka checkpoint guard hai.
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private static final Logger logger= LoggerFactory.getLogger(AuthTokenFilter.class); //logger is used to print info msg in filter
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {//this method is called for every incoming request, it checks for JWT token and sets authentication if valid
    logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
        try {
            String jwt=parseJwt(request);//extract the JWT token from the request header
            if (jwt!=null && jwtUtils.validateJwtToken(jwt))
            {
                String username=jwtUtils.getUserNameFromJWTToken(jwt);
                logger.debug("Username extracted from JWT: {}", username);
                UserDetails userDetails=userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities()); //create an authentication token with user details and authorities
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));//adds ip address and session id to authentication details
                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication); //from this point Spring Security knows the user is authenticated.
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities()); //print the roles like ROLE_USER or ROLE_ADMIN
            }
        }
        catch (Exception e)
        {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request,response); //very imp continue the filter chain to the next filter,if this is not called the request stops.
    }

    private String parseJwt(HttpServletRequest request) {
        String jwt=jwtUtils.getJwtFromHeader(request);
        logger.debug("AuthTokenFilter.java:{}", jwt);
        return jwt;
    }
    }
