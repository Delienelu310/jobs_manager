package com.ilumusecase.jobs_manager.resources;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
@Data
@NoArgsConstructor
public class AppUser implements UserDetails {

    @Id
    private String id;
    private AppUserDetails appUserDetails;

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public void setNewState(UserDetails userDetails){
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.authorities = userDetails.getAuthorities();
    }
   
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
