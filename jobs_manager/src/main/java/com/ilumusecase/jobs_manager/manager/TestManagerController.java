package com.ilumusecase.jobs_manager.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestManagerController {
    
    @Autowired
    private Manager manager;

    @GetMapping("/test-manager/hello")
    public String hello(){
        return manager.hello();
    }

    @GetMapping("/test-manager/group")
    public String createGroup(){
        return manager.createGroup();
    }

}
