package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;

import org.springframework.security.provisioning.UserDetailsManager;

import com.ilumusecase.jobs_manager.resources.AppUser;

public interface AppUserRepository extends UserDetailsManager{
    
    public List<AppUser> retrieveUsers();
    public AppUser retrieveUserById(String id);
    public AppUser findByUsername(String username);

    public void deleteUserById(String id);

    public AppUser saveAppUser(AppUser user);


}
