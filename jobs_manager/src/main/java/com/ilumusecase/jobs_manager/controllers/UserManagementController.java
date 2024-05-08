package com.ilumusecase.jobs_manager.controllers;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.resources.AppUser;

@RestController
public class UserManagementController {
    

    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers(){
        return null;
    }

    @GetMapping("/users/{id}")
    public MappingJacksonValue retrieveUserById(@PathVariable("id") String id){
        return null;
    }

    @PostMapping("/users")
    @PreAuthorize("ADMIN")
    public MappingJacksonValue createNewUser(@RequestBody AppUser appUser){
        appUser.setId(null);
        return null;
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(){

    }

    @PutMapping("/users/{id}")
    public MappingJacksonValue udpateUserDetails(@RequestBody AppUser appUser, @PathVariable("id") String id){
        return null;
    }

}
