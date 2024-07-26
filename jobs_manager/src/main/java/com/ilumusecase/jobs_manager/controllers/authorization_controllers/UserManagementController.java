package com.ilumusecase.jobs_manager.controllers.authorization_controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.security.Roles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthAdminRoleOnly;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.DisableDefaultAuth;

import jakarta.validation.constraints.Min;
import jakarta.websocket.server.PathParam;

@RestController
public class UserManagementController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private RepositoryFactory repositoryFactory;
    

    @GetMapping("/users")
    @JsonMapperRequest(type="full", resource = "AppUser")
    @DisableDefaultAuth
    public Object retrieveAllUsers(
        @RequestParam(name= "query", required = false, defaultValue = "") String query,
        @RequestParam(name= "fullname", required = false, defaultValue = "") String fullname,
        @RequestParam(name= "pageSize", required = false, defaultValue = "10") @Min(1) Integer pageSize,
        @RequestParam(name= "pageNumber", required = false, defaultValue = "0") @Min(0) Integer pageNumber
    ){
        return repositoryFactory.getUserDetailsManager().retrieveUsers(query, fullname, pageSize,  pageNumber);
    }

    @GetMapping("/users/count")
    @DisableDefaultAuth
    public long retrieveAllUsers(
        @RequestParam(name= "query", required = false, defaultValue = "") String query,
        @RequestParam(name= "fullname", required = false, defaultValue = "") String fullname
    ){
        return repositoryFactory.getUserDetailsManager().retrieveUsersCount(query, fullname);
    }

    @GetMapping("/users/{id}")
    @JsonMapperRequest(type="full", resource = "AppUser")
    @DisableDefaultAuth
    public Object retrieveUserById(@PathVariable("id") String id){
        return  repositoryFactory.getUserDetailsManager().retrieveUserById(id);
    }

   
    @PostMapping("/moderators")
    @AuthAdminRoleOnly
    @DisableDefaultAuth
    public void createModerator(@RequestBody AppUser appUser){

        UserDetails userDetails = User
            .withUsername(appUser.getUsername())
            .password(appUser.getPassword())
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles("MODERATOR") 
            .build();

        appUser.setNewState(userDetails);
        repositoryFactory.getUserDetailsManager().saveAppUser(appUser);
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
    public void createNewUser( @RequestBody AppUser appUser, @PathParam("roles") Roles[] roles){

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

        repositoryFactory.getUserDetailsManager().saveAppUser(appUser);
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
    public void udpateUserDetails(Authentication authentication, @RequestBody AppUser appUser, @PathVariable("id") String id){
          
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
        
        repositoryFactory.getUserDetailsManager().updateUser(userDetails);
    }

}
