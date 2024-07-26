package com.ilumusecase.jobs_manager.repositories.interfaces.authorization;

import java.util.List;

import org.springframework.security.provisioning.UserDetailsManager;

import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;

public interface AppUserRepository extends UserDetailsManager{
    
    public List<AppUser> retrieveProjectPrivileges(
        String projectId,
        String query,
        List<ProjectPrivilege> projectPrivileges,
        Integer pageSize,
        Integer pageNumber
    );


    public long retrieveProjectPrivilegesCount(
        String projectId,
        String query,
        List<ProjectPrivilege> projectPrivileges
    );

    public List<AppUser> retrieveUsers();
    public AppUser retrieveUserById(String id);
    public AppUser findByUsername(String username);

    public void deleteUserById(String id);

    public AppUser saveAppUser(AppUser user);


}
