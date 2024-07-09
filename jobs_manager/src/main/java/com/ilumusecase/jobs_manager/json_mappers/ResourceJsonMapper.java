package com.ilumusecase.jobs_manager.json_mappers;

import com.fasterxml.jackson.databind.ser.FilterProvider;

public interface ResourceJsonMapper {
    
    public FilterProvider getFilterProvider(String type);

}
