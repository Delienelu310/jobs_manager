package com.ilumusecase.jobs_manager.resources;


import org.springframework.security.core.userdetails.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
public class AppUser extends User {

    @Id
    private String id;
  

    private AppUserDetails appUserDetails;

    public AppUser() {
        super(null, null, null);
    }

    public AppUserDetails getAppUserDetails() {
        return appUserDetails;
    }

    public void setAppUserDetails(AppUserDetails appUserDetails) {
        this.appUserDetails = appUserDetails;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
