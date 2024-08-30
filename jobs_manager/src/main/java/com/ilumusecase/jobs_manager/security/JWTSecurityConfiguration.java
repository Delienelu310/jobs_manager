package com.ilumusecase.jobs_manager.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.AppUserDetails;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class JWTSecurityConfiguration {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);


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
        http.httpBasic(Customizer.withDefaults());
        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();

        http.oauth2ResourceServer(
            OAuth2ResourceServerConfigurer::jwt
        );

        return http.build();
    }

    // to manage the users
    @Value("${jobs_manager.admin.username}")
    private String adminUsername;

    @Value("${jobs_manager.admin.password}")
    private String adminPassword;

    @Value("${jobs_manager.admin.fullname}")
    private String adminFullName;

    @Bean
    public UserDetailsManager getUserDetailsManager(){

        // the roles:
        // ADMIN - has access throughout all of the application, can create and delete moderators
        // MODERATOR - has the same privileges as ADMIN, but cannot manage moderators - just admin helpers
        // MANAGER - can create projects and maange them
        // WORKER - cannot create projects, but can update projects, if the MANAGER has granted him with privileges

        UserDetails adminUser = User
            .withUsername(adminUsername)
            .password(adminPassword)
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles("ADMIN")
            .build()
        ;

        repositoryFactory.getUserDetailsManager().createUser(adminUser);

        AppUser user = repositoryFactory.getUserDetailsManager().findByUsername(adminUsername);
        AppUserDetails appUserDetails = new AppUserDetails();
        appUserDetails.setFullname(adminFullName);
        user.setAppUserDetails(appUserDetails);
        repositoryFactory.getUserDetailsManager().saveAppUser(user);

        return repositoryFactory.getUserDetailsManager();
    }

}
