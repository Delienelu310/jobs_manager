package com.ilumusecase.jobs_manager.files_validators;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.jar.JarFile;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class JarValidator implements FilesValidator{

    @Override
    public Optional<String> validate(MultipartFile multipartFile, String expectedClass) {
  
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }catch(Exception exception){
            return Optional.empty();
        }
        

        boolean result = false;
        try {
            JarFile jarFile = new JarFile(file);
            result = jarFile.stream().anyMatch(entry -> entry.getName().replaceAll("/", ".").equals( "com.ilumusecase.scripts." + expectedClass + ".class"));
            jarFile.close();
        }catch(Exception e){
            return Optional.empty();
        }
        
        if(result) return Optional.of("com.ilumusecase.scripts." + expectedClass);
        else return Optional.empty();
    }
    
}
