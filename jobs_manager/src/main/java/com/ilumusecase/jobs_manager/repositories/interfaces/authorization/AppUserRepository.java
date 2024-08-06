package com.ilumusecase.jobs_manager.repositories.interfaces.authorization;

import java.util.List;

import org.springframework.security.provisioning.UserDetailsManager;

import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.AppUserDetails;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
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

    public List<AppUser> retrieveJobNodePrivileges(
        String jobNodeId,
        String query,
        List<JobNodePrivilege> jobNodePrivileges,
        Integer pageSize,
        Integer pageNumber
    );

    public long retrieveJobNodePrivilegesCount(
        String jobNodeId,
        String query,
        List<JobNodePrivilege> jobNodePrivileges
        
    );

    public List<AppUser> retrieveUsers(String query, String fullName, Integer pageSize, Integer pageNumber);
    public long retrieveUsersCount(String query, String fullName);

    public AppUser retrieveUserById(String id);
    public AppUser findByUsername(String username);

    public void deleteUserById(String id);

    public AppUser saveAppUser(AppUser user);

    public void updateAppUserDetails(String username, AppUserDetails appUserDetails);

}
