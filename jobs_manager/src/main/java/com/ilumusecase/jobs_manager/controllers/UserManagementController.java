package com.ilumusecase.jobs_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MappingJacksonValue createNewUser(@RequestBody AppUser appUser, @PathParam("role") String[] roles){

        for(int i = 0; i < roles.length; i++){
            roles[i] = "ROLE_" + roles[i].toUpperCase();
        }

        UserDetails userDetails = User
            .withUsername(appUser.getUsername())
            .password(appUser.getPassword())
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles(roles) 
            .build();

        appUser.setId(null);
        appUser.setNewState(userDetails);
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(
            repositoryFactory.getUserDetailsManager().saveAppUser(appUser)
        );
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") String id){
        repositoryFactory.getUserDetailsManager().deleteUserById(id);
    }

    @PutMapping("/users/{id}")
    public MappingJacksonValue udpateUserDetails(@RequestBody AppUser appUser, @PathVariable("id") String id){
        
    
        UserDetails userDetails = User.withUsername(appUser.getUsername())
            .password(id)
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .build();

        appUser.setUsername(userDetails.getUsername());
        appUser.setPassword(userDetails.getPassword());
        appUser.setId(id);
    
        
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(appUser);
    }

}
