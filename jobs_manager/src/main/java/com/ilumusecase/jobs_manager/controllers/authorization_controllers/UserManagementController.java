package com.ilumusecase.jobs_manager.controllers.authorization_controllers;

import java.util.Base64;

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
import com.ilumusecase.jobs_manager.resources.authorities.AppUserDetails;
import com.ilumusecase.jobs_manager.security.Roles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthAdminRoleOnly;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeRoles;

import jakarta.validation.constraints.Min;

@RestController
public class UserManagementController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private RepositoryFactory repositoryFactory;
    

    @GetMapping("/users")
    @JsonMapperRequest(type="full", resource = "AppUser")
    @AuthorizeRoles
    public Object retrieveAllUsers(
        @RequestParam(name= "query", required = false, defaultValue = "") String query,
        @RequestParam(name= "fullname", required = false, defaultValue = "") String fullname,
        @RequestParam(name= "pageSize", required = false, defaultValue = "10") @Min(1) Integer pageSize,
        @RequestParam(name= "pageNumber", required = false, defaultValue = "0") @Min(0) Integer pageNumber
    ){
        return repositoryFactory.getUserDetailsManager().retrieveUsers(query, fullname, pageSize,  pageNumber);
    }

    @GetMapping("/users/count")
    @AuthorizeRoles
    public long retrieveAllUsers(
        @RequestParam(name= "query", required = false, defaultValue = "") String query,
        @RequestParam(name= "fullname", required = false, defaultValue = "") String fullname
    ){
        return repositoryFactory.getUserDetailsManager().retrieveUsersCount(query, fullname);
    }

    @GetMapping("/users/{id}")
    @JsonMapperRequest(type="full", resource = "AppUser")
    @AuthorizeRoles
    public Object retrieveUserById(@PathVariable("id") String id){
        return  repositoryFactory.getUserDetailsManager().retrieveUserById(id);
    }


    private record AppUserRequestBody(
        String passwordEncoded,
        String username,
        Roles[] roles,
        AppUserDetails appUserDetails
    ){

    }

   
    @PostMapping("/moderators")
    @AuthAdminRoleOnly
    public void createModerator(@RequestBody AppUserRequestBody appUserBody){

        UserDetails userDetails = User
            .withUsername(appUserBody.username)
            .password(new String(Base64.getDecoder().decode(appUserBody.passwordEncoded)))
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles("MODERATOR") 
            .build();

        AppUser appUser = new AppUser();
        appUser.setNewState(userDetails);
        appUser.setAppUserDetails(appUserBody.appUserDetails);

        repositoryFactory.getUserDetailsManager().saveAppUser(appUser);
    }

    @DeleteMapping("/moderators/{username}")
    @AuthAdminRoleOnly
    public void deleteModerator(@PathVariable("username") String username){
        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(username);
        if(!appUser.getAuthorities().stream().anyMatch(auth ->  auth.toString().equals("ROLE_MODERATOR"))){
            throw new RuntimeException("Endpoint must be used to delete moderator");
        }

        repositoryFactory.getUserDetailsManager().deleteUserById(username);
    }


    @PostMapping("/users")
    // only moderator and admin
    public void createNewUser( @RequestBody AppUserRequestBody appUserBody){

        if(repositoryFactory.getUserDetailsManager().userExists(appUserBody.username)){
            throw new RuntimeException("User with username\""  + appUserBody.username + "\" exists already");
        }

        String[] rolesFiltered = new String[appUserBody.roles.length];
        int i = 0;
        for(Roles role : appUserBody.roles){
            rolesFiltered[i] = role.toString();   
            i++;        
        }

        UserDetails userDetails = User
            .withUsername(appUserBody.username)
            .password(new String(Base64.getDecoder().decode(appUserBody.passwordEncoded)))
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .roles(rolesFiltered) 
            .build();

        AppUser appUser = new AppUser();
        appUser.setNewState(userDetails);
        appUser.setAppUserDetails(appUserBody.appUserDetails);

        repositoryFactory.getUserDetailsManager().saveAppUser(appUser);
    }

    @DeleteMapping("/users/{id}")
    //only moderator and admin
    public void deleteUser(@PathVariable("id") String id){
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(id);
        if(appUser.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN") || auth.toString().equals("ROLE_MODERATOR"))){
            throw new RuntimeException("Endpoint cannot be used to delete moderator");
        }
        repositoryFactory.getUserDetailsManager().deleteUserById(id);
    }

    private record OldNewPassword(String oldBase64EncodedPassword, String newBase64EncodedPassword){

    }

    @PutMapping("/users/password")
    @AuthorizeRoles
    public void updateMyPassword( @RequestBody OldNewPassword oldNewPassword){

        repositoryFactory.getUserDetailsManager().changePassword(
            new String(Base64.getDecoder().decode(oldNewPassword.oldBase64EncodedPassword)),
            new String(Base64.getDecoder().decode(oldNewPassword.newBase64EncodedPassword))    
        );

    }

    @PutMapping("/moderators/{username}/password")
    @AuthAdminRoleOnly
    public void updateModeratorPassword(@PathVariable("username") String username, @RequestBody String base64EncodedPassword ){

        AppUser user = repositoryFactory.getUserDetailsManager().findByUsername(username);
        
        boolean isAdmin = user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isModerator = user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_MODERATOR"));
        
        if(!isAdmin && !isModerator) throw new RuntimeException("End point should be used for moderators and admins only");

        UserDetails userDetails = User.withUsername(username)
            .password(new String(Base64.getDecoder().decode(base64EncodedPassword)))
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .authorities(user.getAuthorities())
            .build();
        repositoryFactory.getUserDetailsManager().updateUser(userDetails);

    }

   
    @PutMapping("/users/{username}/password")
    //only admin and moderator
    public void updatePassword(@PathVariable("username") String username, @RequestBody String base64EncodedPassword ){

        AppUser user = repositoryFactory.getUserDetailsManager().findByUsername(username);
        
        if(user.getAuthorities().stream().anyMatch(auth -> 
            auth.getAuthority().equals("ROLE_MODERATOR")
            ||
            auth.getAuthority().equals("ROLE_ADMIN")
        )) throw new RuntimeException("The endpoint should not be used for admin or moderator");

        UserDetails userDetails = User.withUsername(username)
            .password(new String(Base64.getDecoder().decode(base64EncodedPassword)))
            .passwordEncoder(str -> passwordEncoder.encode(str))
            .authorities(user.getAuthorities())
            .build();
        repositoryFactory.getUserDetailsManager().updateUser(userDetails);

    }

    @PutMapping("/users/{username}/roles")
    //only admin and moderator
    public void updateRoles(@RequestBody Roles[] newRoles, @PathVariable("username") String username){
        AppUser user = repositoryFactory.getUserDetailsManager().findByUsername(username);
        
        if(user.getAuthorities().stream().anyMatch(auth -> 
            auth.getAuthority().equals("ROLE_MODERATOR")
            ||
            auth.getAuthority().equals("ROLE_ADMIN")
        )) throw new RuntimeException("The endpoint should not be used for admin or moderator");

        String[] rolesStr = new String[newRoles.length];
        for(int i = 0; i < newRoles.length; i++ ){
            rolesStr[i] = newRoles[i].toString();
        }
        UserDetails userDetails = User.withUsername(username)
            .password(user.getPassword())
            .roles(rolesStr)
            .build();
        repositoryFactory.getUserDetailsManager().updateUser(userDetails);

    }

    @PutMapping("/users/{username}/details")
    @AuthorizeRoles
    public void udpateAppUserDetails(Authentication authentication, @RequestBody AppUserDetails appUserDetails, @PathVariable("username") String username){
          
        AppUser user = repositoryFactory.getUserDetailsManager().retrieveUserById(username);

        if(
            !authentication.getAuthorities().stream().map(auth -> auth.getAuthority()).anyMatch(auth -> auth.equals("ROLE_ADMIN"))
            &&
            !authentication.getName().equals(username)
            &&
            !(
                authentication.getAuthorities().stream().map(auth -> auth.getAuthority()).anyMatch(auth -> auth.equals("ROLE_MODERATOR"))
                &&
                !user.getAuthorities().stream().map(auth -> auth.getAuthority()).anyMatch(auth -> auth.equals("ROLE_MODERATOR") || auth.equals("ROLE_ADMIN"))
            )

        ){
            throw new RuntimeException(" You are not authorized to change details of the user");
        } 
        

        repositoryFactory.getUserDetailsManager().updateAppUserDetails(username, appUserDetails);
    }

}
