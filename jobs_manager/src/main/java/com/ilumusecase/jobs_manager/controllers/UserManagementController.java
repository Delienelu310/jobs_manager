package com.ilumusecase.jobs_manager.controllers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.AppUser;
import com.ilumusecase.jobs_manager.security.Roles;

import jakarta.websocket.server.PathParam;

@RestController
public class UserManagementController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;
    

    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers(){
        return jsonMappersFactory.getAppUserJsonMapper().getFullAppUserList(
            repositoryFactory.getUserDetailsManager().retrieveUsers()
        );
    }

    @GetMapping("/users/{id}")
    public MappingJacksonValue retrieveUserById(@PathVariable("id") String id){
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(
            repositoryFactory.getUserDetailsManager().retrieveUserById(id)
        );
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public MappingJacksonValue createNewUser(Authentication authentication, @RequestBody AppUser appUser, @PathParam("role") Roles[] roles){

        String[] rolesFiltered = new String[roles.length];
        int i = 0;
        for(Roles role : roles){
            rolesFiltered[i] = role.toString();           
        }

        UserDetails userDetails = User
            .withUsername(appUser.getUsername())
            .password(appUser.getPassword())
            // .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles(rolesFiltered) 
            .build();

        appUser.setNewState(userDetails);
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(
            repositoryFactory.getUserDetailsManager().saveAppUser(appUser)
        );
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public void deleteUser(@PathVariable("id") String id){

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(id);
        if(appUser.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN") || auth.toString().equals("ROLE_MODERATOR"))){
            throw new RuntimeException("Endpoint cannot be used to delete moderator");
        }

        repositoryFactory.getUserDetailsManager().deleteUserById(id);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public MappingJacksonValue udpateUserDetails(Authentication authentication, @RequestBody AppUser appUser, @PathVariable("id") String id){
          
        AppUser dbAppUser = repositoryFactory.getUserDetailsManager().retrieveUserById(id);
        if(dbAppUser.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN") || auth.toString().equals("ROLE_MODERATOR"))){
            throw new RuntimeException("Endpoint cannot be used to delete moderator");
        }

        // ?
        UserDetails userDetails = User.withUsername(appUser.getUsername())
            .password(id)
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .build();

        appUser.setUsername(userDetails.getUsername());
        appUser.setPassword(userDetails.getPassword());
        // appUser.setId(id);
    
        
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(appUser);
    }

}
