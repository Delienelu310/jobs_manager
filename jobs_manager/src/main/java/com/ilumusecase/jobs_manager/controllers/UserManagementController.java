package com.ilumusecase.jobs_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
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
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.security.Roles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthAdminRoleOnly;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.DisableDefaultAuth;

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
    @DisableDefaultAuth
    public MappingJacksonValue retrieveAllUsers(){
        return jsonMappersFactory.getAppUserJsonMapper().getFullAppUserList(
            repositoryFactory.getUserDetailsManager().retrieveUsers()
        );
    }

    @GetMapping("/users/{id}")
    @DisableDefaultAuth
    public MappingJacksonValue retrieveUserById(@PathVariable("id") String id){
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(
            repositoryFactory.getUserDetailsManager().retrieveUserById(id)
        );
    }

   
    @PostMapping("/moderators")
    @AuthAdminRoleOnly
    @DisableDefaultAuth
    public MappingJacksonValue createModerator(@RequestBody AppUser appUser){

        UserDetails userDetails = User
            .withUsername(appUser.getUsername())
            .password(appUser.getPassword())
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles("MODERATOR") 
            .build();

        appUser.setNewState(userDetails);
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(
            repositoryFactory.getUserDetailsManager().saveAppUser(appUser)
        );
    }

    @DeleteMapping("/moderators/{username}")
    @DisableDefaultAuth
    @AuthAdminRoleOnly
    public void deleteModerator(@PathVariable("username") String username){
        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(username);
        if(!appUser.getAuthorities().stream().anyMatch(auth ->  auth.toString().equals("ROLE_MODERATOR"))){
            throw new RuntimeException("Endpoint must be used to delete moderator");
        }

        repositoryFactory.getUserDetailsManager().deleteUserById(username);
    }

    @PostMapping("/users")
    @DisableDefaultAuth
    public MappingJacksonValue createNewUser( @RequestBody AppUser appUser, @PathParam("roles") Roles[] roles){

        String[] rolesFiltered = new String[roles.length];
        int i = 0;
        for(Roles role : roles){
            rolesFiltered[i] = role.toString();   
            i++;        
        }

        UserDetails userDetails = User
            .withUsername(appUser.getUsername())
            .password(appUser.getPassword())
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles(rolesFiltered) 
            .build();

        appUser.setNewState(userDetails);
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(
            repositoryFactory.getUserDetailsManager().saveAppUser(appUser)
        );
    }

    @DeleteMapping("/users/{id}")
    @DisableDefaultAuth
    public void deleteUser(@PathVariable("id") String id){
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(id);
        if(appUser.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN") || auth.toString().equals("ROLE_MODERATOR"))){
            throw new RuntimeException("Endpoint cannot be used to delete moderator");
        }
        repositoryFactory.getUserDetailsManager().deleteUserById(id);
    }

    @PutMapping("/users/{id}")
    @DisableDefaultAuth
    public MappingJacksonValue udpateUserDetails(Authentication authentication, @RequestBody AppUser appUser, @PathVariable("id") String id){
          
        AppUser dbAppUser = repositoryFactory.getUserDetailsManager().retrieveUserById(id);
        if(dbAppUser.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN") || auth.toString().equals("ROLE_MODERATOR"))){
            throw new RuntimeException("Endpoint cannot be used to update moderator");
        }

        UserDetails userDetails = User.withUsername(appUser.getUsername())
            .password(id)
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .build();

        appUser.setUsername(userDetails.getUsername());
        appUser.setPassword(userDetails.getPassword());
        
        return jsonMappersFactory.getAppUserJsonMapper().getFulLAppUser(appUser);
    }

}
