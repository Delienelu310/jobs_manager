package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;

@RestController
public class JobEntityController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;


}
