package com.ilumusecase.jobs_manager.resources;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
@Data
@NoArgsConstructor
public class AppUser implements UserDetails {

    
    private AppUserDetails appUserDetails;
    
    @Id
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AppUser other = (AppUser) obj;
        return username == other.getUsername();
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
