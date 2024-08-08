package com.ilumusecase.jobs_manager.resources.authorities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserDetails {
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String fullname;
    @Size(min = 3, max = 500)
    private String description;
    

}
