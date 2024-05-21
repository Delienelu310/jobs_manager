package com.ilumusecase.jobs_manager.s3clients;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.resources.JobEntity;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Component
public class JobS3Client {
    
    @Autowired
    private MinioClient minioClient;

    public void uploadJob(JobEntity jobEntity, MultipartFile multipartFile){
        try{

            String filename = multipartFile.getOriginalFilename();

            if(filename == null || filename.lastIndexOf(".") == -1) throw new RuntimeException();
            String extension = filename.substring(filename.lastIndexOf(".") + 1);

            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
    
            minioClient.putObject(PutObjectArgs.builder()
                .bucket("jobsmanager")
                .object("projects/" + jobEntity.getProject().getId() + "/job_nodes/" + jobEntity.getJobNode().getId() + "/jobs/"  +
                    jobEntity.getId() + "." + extension)
                .stream(inputStream, inputStream.available(), -1)
                .build());
            
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<byte[]> downloadJob(JobEntity jobEntity, String extension){
      
        try{
            GetObjectResponse getObjectResponse = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket("jobsmanager")
                    .object("projects/" + jobEntity.getProject().getId() + "/job_nodes/" + jobEntity.getJobNode().getId() + "/jobs/" + 
                       jobEntity.getId() + "." + extension)
                    .build()
            );

            return Optional.of(getObjectResponse.readAllBytes());
        }catch(Exception e){
            return Optional.empty();
        }
    }
}
