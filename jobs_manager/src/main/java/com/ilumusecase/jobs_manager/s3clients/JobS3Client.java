package com.ilumusecase.jobs_manager.s3clients;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;

@Component
public class JobS3Client {
    
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public void uploadJob(JobsFile jobsFile, MultipartFile multipartFile){
        try{
            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
    
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object("jars/projects/" + jobsFile.getProject().getId() + "/job_nodes/" + jobsFile.getJobNode().getId() + "/jobs/"  +
                    jobsFile.getId() + "." + jobsFile.getExtension())
                .stream(inputStream, inputStream.available(), -1)
                .build());
            
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public Optional<byte[]> downloadJob(JobsFile jobsFile){
      
        try{
            GetObjectResponse getObjectResponse = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket)
                    .object("jars/projects/" + jobsFile.getProject().getId() + "/job_nodes/" + jobsFile.getJobNode().getId() + "/jobs/" + 
                       jobsFile.getId() + "." + jobsFile.getExtension())
                    .build()
            );

            return Optional.of(getObjectResponse.readAllBytes());
        }catch(Exception e){
            return Optional.empty();
        }
    }

    public Optional<StatObjectResponse> getMetadata(JobsFile jobsFile){
        try{
            StatObjectResponse statObjectResponse = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .object("jars/projects/" + jobsFile.getProject().getId() + "/job_nodes/" + jobsFile.getJobNode().getId() + "/jobs/" + 
                        jobsFile.getId() +  "." + jobsFile.getExtension())
                    .build()
            );

            return Optional.of(statObjectResponse);
        }catch(ErrorResponseException e){
            return Optional.empty();
        }catch(Exception e){
            //...
            throw new RuntimeException(e);
        }
    }

    public void deleteJob(JobsFile jobsFile){
        try{
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object("jars/projects/" + jobsFile.getProject().getId() + "/job_nodes/" + jobsFile.getJobNode().getId() + "/jobs/" + 
                       jobsFile.getId() + "." + jobsFile.getExtension())
                    .build()
            );

        }catch(Exception e){
            throw new RuntimeException();
        }
    }
}
