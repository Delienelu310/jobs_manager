package com.ilumusecase.jobs_manager.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class JWTSecurityConfiguration {

    private RepositoryFactory repositoryFactory;


    public JWTSecurityConfiguration(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;

        UserDetails adminUser = User
            .withUsername("admin")
            .password("admin")
            .passwordEncoder(str -> passwordEncoder().encode(str))
            .roles("ADMIN")
            .build()
        ;

        repositoryFactory.getUserDetailsManager().createUser(adminUser);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests(auth -> {
            auth
                .requestMatchers(new AntPathRequestMatcher("/authenticate")).permitAll()
                .anyRequest().authenticated();
        });
        http.sessionManagement(
            session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS
        ));
        // http.httpBasic();
        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();

        http.oauth2ResourceServer(
            OAuth2ResourceServerConfigurer::jwt
        );

        return http.build();
    }

    // to manage the users


    @Bean
    public UserDetailsManager getUserDetailsManager(){
        return repositoryFactory.getUserDetailsManager();
    }

    // To hash the password in the database
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    // To make signature for jwt token and check it later:
    @Bean
    public KeyPair keyPair(){
       try {
         var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
         keyPairGenerator.initialize(2048);
         return keyPairGenerator.generateKeyPair();
       } catch (Exception e) {
            throw new RuntimeException(e);
       }
    }

    @Bean
    public RSAKey rsaKey(KeyPair keyPair){
        return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
            .privateKey(keyPair.getPrivate())
            .keyID(UUID.randomUUID().toString())
            .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey){
        var jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
        
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws Exception{
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey())
            .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource){
        return new NimbusJwtEncoder(jwkSource);
    }

}
