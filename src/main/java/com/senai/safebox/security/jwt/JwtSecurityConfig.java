package com.senai.safebox.security.jwt;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


/*Classe responsável por codificar(gerar) e decodificar(validar) tokens JWT.Além de acessar as configurações do apllication.properties(chave secreta e tempo de expiração)*/
/*Usando uma chave simétrica (HMAC) onde a mesma chave é usada para assinar e verificar tokens*/
@Configuration
public class JwtSecurityConfig {

    private final JwtProperties jwtProperties;

    public JwtSecurityConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /*Usando o @Bean para configurar uma classe externa*/
    @Bean
    public JwtEncoder jwtEncoder() {

        /*Recuperando a secret key do application.properties*/
        String secretKeyString = jwtProperties.getSecretKey();

        /*Transformando a string em bytes(sequência binária)*/
        byte[] secretKeyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);

        /*Preparando a secretKey com a senha criada e o algorítimo utilizado*/
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        /*Criando o provedor/gerenciador das secretKeys. Assina os novos tokens e valida os tokens recebidos na requisição*/
        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(secretKey);

        /*codificando a secret e retornando o gerenciador*/
        return new NimbusJwtEncoder(jwkSource);
    };

    @Bean
    public JwtDecoder jwtDecoder() {
        /*Recupera, transforma e prepara a secret key*/
        String secretKeyString = jwtProperties.getSecretKey();
        byte[] secretKeyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");


        /*Monstando a estrutura de decodificação*/
        return NimbusJwtDecoder.withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    };


}
