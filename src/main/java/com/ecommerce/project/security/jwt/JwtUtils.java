package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
//✔ generateTokenFromUsername(user) → token generate
//✔ getUsernameFromToken(token) → username nikaalo
//✔ isTokenValid(token, user) → token valid?
//✔ isExpired(token) → expire check
//JWT ko handle karne ki PURE LOGIC yahi hai.
@Component
public class JwtUtils {
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    //Getting jwt from header
    private static final Logger logger= LoggerFactory.getLogger(JwtUtils.class);//logger is used to print info msg in jwt
    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken=request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);
        if(bearerToken!=null && bearerToken.startsWith("Bearer ")) {    //check if header is not null and starts with "Bearer "
            return bearerToken.substring(7);    //extract only token part by removing "Bearer " prefix
        }
        return null;
    }
    //Generating token from Username
    public String generateTokenFromUsername(UserDetails userDetails)
    {
        String username=userDetails.getUsername();
        return Jwts.builder().subject(username).
                issuedAt(new Date()).
                expiration(new Date((new Date()).getTime()+jwtExpirationMs)).
                signWith(key()).compact();
    }
    //Getting username from token
    public String getUserNameFromJWTToken(String token)
    {
        return Jwts.parser().verifyWith((SecretKey) key()).
                build().parseSignedClaims(token).getPayload().getSubject();
    }
    //Generate signing key
    public Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
    //Validate jwt token
    public boolean validateJwtToken(String authToken)
    {
        try{
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        }
        catch (MalformedJwtException e)
        {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            logger.error("JWT token is expired: {}", e.getMessage());
        }

        catch (UnsupportedJwtException e)
        {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }

        catch (IllegalArgumentException e)
        {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}

