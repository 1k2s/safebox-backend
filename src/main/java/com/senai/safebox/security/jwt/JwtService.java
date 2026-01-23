package com.senai.safebox.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;



@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtProperties = jwtProperties;
    }


    /*Metodo que gera um token para um usuário*/
    public String generateToken(UserDetails userDetails) {

        /*Configurando os tempos de expiração*/
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtProperties.getExpirationTime());

        /*Recuperando as roles do userDetails que vem como objetos GrantedAuthority, e transformando em uma lista de Strings*/
        /*A lista: [SimpleGrantedAuthority("ROLE_USER"), SimpleGrantedAuthority("ROLE_ADMIN")]*/
        /*Se torna: ["ROLE_USER", "ROLE_ADMIN"]*/
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); //separando por virgulas

        /*Configurando as claims do token(informações que vão no payload)*/
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("safeBox") // quem emitiu o token
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(userDetails.getUsername()) // username
                .claim("roles", roles) // roles do usuário
                .build();


        /*Configurando o header, contém informações sobre o tipo do token e o algoritmo usado para assiná-lo)*/
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();


        /*Assinando o token*/
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));

        return jwt.getTokenValue();
    };

    /*Metodo que valida o token*/
    public boolean isTokenValid(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    };

    /*Extrai o username do token*/
    public String extractUsername(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    /*Extrai as roles do token*/
    public String extractRoles(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        return jwt.getClaim("roles");
    }

    /*Extrai o tempo de expiração do token*/
    public Long getExpirationTime() {
        return jwtProperties.getExpirationTime();
    }

    /*Atualizando a role e gerando um novo token*/
    public String updateRoleAndGenerateNewToken(String oldToken, String newRole){

        /*Decodificando o token para acessar as informações do usuário*/
        Jwt jwt = jwtDecoder.decode(oldToken);

        String username = jwt.getSubject();
        String role = "ROLE_" + newRole;
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtProperties.getExpirationTime());

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("safeBox") // quem emitiu o token
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(username) // username
            .claim("roles", role) // roles do usuário
            .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        Jwt newJwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));

        return newJwt.getTokenValue();

    }


}
