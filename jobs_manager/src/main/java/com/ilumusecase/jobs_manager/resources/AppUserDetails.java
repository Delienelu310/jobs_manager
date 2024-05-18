package com.ilumusecase.jobs_manager.resources;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserDetails {
    String fullname;
    Integer jobCreatedCounter = 0;
}
