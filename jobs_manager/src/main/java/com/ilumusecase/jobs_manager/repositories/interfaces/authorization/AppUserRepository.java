package com.ilumusecase.jobs_manager.repositories.interfaces.authorization;

import java.util.List;

import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.AppUserDetails;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.validation.annotations.Username;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Validated
public interface AppUserRepository extends UserDetailsManager{
    
    public List<AppUser> retrieveProjectPrivileges(
        String projectId,
        @Size(max = 50) @NotNull  String query,
        @Size(max = 20) @NotNull  List<ProjectPrivilege> projectPrivileges,
        @Min(1)  @NotNull Integer pageSize,
        @Min(0)  @NotNull Integer pageNumber
    );


    public long retrieveProjectPrivilegesCount(
        @Size(max = 50) @NotNull  String projectId,
        @Size(max = 20) @NotNull  String query,
        List<ProjectPrivilege> projectPrivileges
    );

    public List<AppUser> retrieveJobNodePrivileges(
        String jobNodeId,
        @Size(max = 50) @NotNull String query,
        @Size(max = 20) @NotNull List<JobNodePrivilege> jobNodePrivileges,
        @Min(1) @NotNull Integer pageSize,
        @Min(0) @NotNull Integer pageNumber
    );

    public long retrieveJobNodePrivilegesCount(
        String jobNodeId,
        @Size(max = 50) @NotNull String query,
        @Size(max = 20) @NotNull List<JobNodePrivilege> jobNodePrivileges
        
    );

    public List<AppUser> retrieveUsers(
        @Size(max = 50) @NotNull String query, 
        @Size(max = 50) @NotNull String fullName, 
        @Min(1) @NotNull Integer pageSize, 
        @Min(0) @NotNull Integer pageNumber
    );
    public long retrieveUsersCount(
        @Size(max = 50) @NotNull String query, 
        @Size(max = 50) @NotNull String fullName
    );

    public AppUser retrieveUserById(@Username String id);
    public AppUser findByUsername(@Username String username);

    public void deleteUserById(@Username String id);

    public AppUser saveAppUser(@Valid @NotNull AppUser user);

    public void updateAppUserDetails(@Username String username, @NotNull @Valid AppUserDetails appUserDetails);

}
