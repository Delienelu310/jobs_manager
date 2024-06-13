package com.ilumusecase.jobs_manager.files_validators;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.jar.JarFile;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.resources.JobNode;

@Component
public class JarValidator implements FilesValidator{

    public List<String> retrieveFileClasses(MultipartFile multipartFile){
        File file = new File(multipartFile.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(file); ) {
            fos.write(multipartFile.getBytes());
            try(JarFile jarFile = new JarFile(file)){
                
                return jarFile.stream().map(e -> e.getName()).filter(e -> e.endsWith(".class")).toList();
            }   
            
        }catch(Exception exception){
            throw new RuntimeException();
        }
    }

    @Override
    public boolean validate(MultipartFile multipartFile, JobNode jobNode, List<String> jobClasses) {
  
        boolean result = true;
  
        //1. check if classes in job nodes are in the jobClasses list
        //2. check if classes in current file are in the active classes of jobnode 
        List<String> classes = retrieveFileClasses(multipartFile);
        
        result = result && !classes.stream().anyMatch(cl -> jobNode.getJobClasses().contains(cl));
        result = result && !jobClasses.stream().anyMatch(cl -> jobNode.getUsedClassnames().containsKey(cl));
    
        return result;
    }
    
}
