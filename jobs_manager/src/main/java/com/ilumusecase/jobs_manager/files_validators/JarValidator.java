package com.ilumusecase.jobs_manager.files_validators;

import java.util.jar.JarFile;

import org.springframework.stereotype.Component;

@Component
public class JarValidator implements FilesValidator{

    @Override
    public boolean validate(String path, String expectedClass) {
  
        boolean result = false;
        try (JarFile jarFile = new JarFile(path)) {
            result = jarFile.stream().anyMatch(entry -> entry.getName().replaceAll("/", ".").equals(expectedClass));
        }catch(Exception e){
            return false;
        }

        return result;
    }
    
}
