package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;

@Component
public class AppUserJsonMapper {

    private final FilterProvider fullUserFilter = new SimpleFilterProvider();
    
    public MappingJacksonValue getFulLAppUser(AppUser appUser){
        MappingJacksonValue result = new MappingJacksonValue(appUser);

        result.setFilters(fullUserFilter);

        return result;
    }

    public MappingJacksonValue getFullAppUserList(List<AppUser> users){
        MappingJacksonValue result = new MappingJacksonValue(users);

        result.setFilters(fullUserFilter);

        return result;
    }

}
