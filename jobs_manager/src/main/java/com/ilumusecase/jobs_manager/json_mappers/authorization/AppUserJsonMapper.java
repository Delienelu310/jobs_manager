package com.ilumusecase.jobs_manager.json_mappers.authorization;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.json_mappers.ResourceJsonMapper;

@Component("AppUser")
public class AppUserJsonMapper implements ResourceJsonMapper{

    private final FilterProvider fullUserFilter = new SimpleFilterProvider();
    
    private Map<String, FilterProvider> filters = new HashMap<>();
    {  
        filters.put("full", fullUserFilter);
    }

    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }

}
