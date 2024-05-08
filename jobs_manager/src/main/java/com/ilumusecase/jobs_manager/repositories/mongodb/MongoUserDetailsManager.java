package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoAppUser;
import com.ilumusecase.jobs_manager.resources.AppUser;
import com.ilumusecase.jobs_manager.resources.AppUserDetails;

public class MongoUserDetailsManager implements UserDetailsManager{

    private MongoAppUser mongoAppUser;

    public MongoUserDetailsManager(MongoAppUser mongoAppUser){
        this.mongoAppUser = mongoAppUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = mongoAppUser.findByUsername(username);

        if(appUser.isEmpty()) throw new UsernameNotFoundException(username);

        return appUser.get();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public void createUser(UserDetails user) {
        AppUser appUser = (AppUser)user;

        mongoAppUser.save(appUser);
    }

    public void createUserWithDetails(UserDetails userDetails, AppUserDetails appUserDetails){
        AppUser appUser = (AppUser)userDetails;
        appUser.setAppUserDetails(appUserDetails);

        mongoAppUser.save(appUser);
    }

    @Override
    public void deleteUser(String username) {
        mongoAppUser.deleteByUsername(username);
    }

    @Override
    public void updateUser(UserDetails user) {
        Optional<AppUser> appUser = mongoAppUser.findByUsername(user.getUsername());
        if(appUser.isEmpty()) throw new RuntimeException();

        AppUser newUser = (AppUser)user;
        newUser.setAppUserDetails(appUser.get().getAppUserDetails());
        newUser.setId(appUser.get().getId());

        mongoAppUser.save(newUser);

    }

    @Override
    public boolean userExists(String username) {
       return mongoAppUser.findByUsername(username).isPresent();
    }
    
}
