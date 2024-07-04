package com.ilumusecase.jobs_manager.files_validators;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class JarValidator implements FilesValidator{

    public Set<String> retrieveFileClasses(MultipartFile multipartFile){
        File file = new File(multipartFile.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(file); ) {
            fos.write(multipartFile.getBytes());
            try(JarFile jarFile = new JarFile(file)){
                return jarFile.stream().map(e -> e.getName()).filter(e -> e.endsWith(".class")).collect(Collectors.toSet());
            }   
            
        }catch(Exception exception){
            throw new RuntimeException();
        }
    }


}
