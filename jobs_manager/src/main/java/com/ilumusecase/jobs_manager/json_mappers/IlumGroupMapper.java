package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Component
public class IlumGroupMapper {
    public MappingJacksonValue mapIlumGroupFull(IlumGroup ilumGroup){
        return null;
    }

    public MappingJacksonValue mapIlumGroupListFull(List<IlumGroup> ilumGroups){
        return null;
    }
}
