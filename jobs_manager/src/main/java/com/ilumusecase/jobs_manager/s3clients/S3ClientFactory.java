package com.ilumusecase.jobs_manager.s3clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class S3ClientFactory {

    @Autowired
    private JobS3Client jobS3Client;

    public JobS3Client getJobS3Client(){
        return jobS3Client;
    }
    
}
