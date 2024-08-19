package com.ilumusecase.jobs_manager.resources.authorities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserDetails {
    
    @NotBlank(message = "Full Name must not be blank")
    @Size(min = 3, max = 50, message = "Full Name must have 3 to 50 letters")
    private String fullname;
    @Size(min = 3, max = 500, message = "Description must have 3 to 500 letters")
    private String description;
    

}
