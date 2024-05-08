package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.AppUser;

@Component
public class AppUserJsonMapper {
    
    public MappingJacksonValue getFulLAppUser(AppUser appUser){
        MappingJacksonValue result = new MappingJacksonValue(appUser);
        FilterProvider filterProvider = new SimpleFilterProvider();

        result.setFilters(filterProvider);

        return result;
    }

    public MappingJacksonValue getFullAppUserList(List<AppUser> users){
        MappingJacksonValue result = new MappingJacksonValue(users);
        FilterProvider filterProvider = new SimpleFilterProvider();

        result.setFilters(filterProvider);

        return result;
    }

}
