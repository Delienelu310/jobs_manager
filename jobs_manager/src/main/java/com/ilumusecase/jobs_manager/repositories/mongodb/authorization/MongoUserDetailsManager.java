package com.ilumusecase.jobs_manager.repositories.mongodb.authorization;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ilumusecase.jobs_manager.repositories.interfaces.authorization.AppUserRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.authorization.MongoAppUser;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.AppUserDetails;


public class MongoUserDetailsManager implements AppUserRepository{

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

    public AppUser findByUsername(String username){
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


        AppUser appUser = new AppUser();
        appUser.setNewState(user);

        AppUserDetails defaultAppUserDetails = new AppUserDetails();
        appUser.setAppUserDetails(defaultAppUserDetails);

        mongoAppUser.save(appUser);
    }


    @Override
    public void deleteUser(String username) {
        mongoAppUser.deleteByUsername(username);
    }

    @Override
    public void updateUser(UserDetails user) {
        AppUser appUser = mongoAppUser.findByUsername(user.getUsername()).orElseThrow(RuntimeException::new);

        appUser.setNewState(user);
        mongoAppUser.save(appUser);

    }

    @Override
    public boolean userExists(String username) {
       return mongoAppUser.findByUsername(username).isPresent();
    }

    @Override
    public List<AppUser> retrieveUsers() {
        return mongoAppUser.findAll();
    }

    @Override
    public AppUser retrieveUserById(String id) {
        return mongoAppUser.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public void deleteUserById(String id) {
        mongoAppUser.deleteById(id);
    }

    @Override
    public AppUser saveAppUser(AppUser user) {
        return mongoAppUser.save(user);
    }
    
}
