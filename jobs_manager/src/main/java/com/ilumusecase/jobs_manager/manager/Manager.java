package com.ilumusecase.jobs_manager.manager;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.resources.JobEntity;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.s3clients.S3ClientFactory;

import reactor.core.publisher.Mono;

@Service
public class Manager {


    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @Autowired
    public S3ClientFactory s3ClientFactory;
    

    private WebClient webClient = WebClient.create();

    public String hello(){

        Mono<String> responseMono = webClient
            .get()
            .uri("http://localhost:9888/api/v1/cluster")
            .retrieve()
            .bodyToMono(String.class);

        return responseMono.block(); 
    
    }

    public String createGroup(JobNode jobNode){

        
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();

        Map<String, String> extensionMap = new HashMap<>();
        extensionMap.put("jar", "jars");
        extensionMap.put("py", "files");

        for(JobEntity jobEntity : jobNode.getJobsQueue()){
            byte[] bytes = s3ClientFactory.getJobS3Client().downloadJob(jobEntity, jobEntity.getExtension()).orElseThrow(RuntimeException::new);
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return jobEntity.getId() + "." + jobEntity.getExtension();
                }
            };
            bodyMap.add(extensionMap.get(jobEntity.getExtension()), byteArrayResource);
        }
        bodyMap.add("scale", "1");
        bodyMap.add("name", jobNode.getId());
        bodyMap.add("clusterName", "default");


        String url = "http://localhost:9888/api/v1/group";
        return webClient.post()
            .uri(url)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyMap))
            .retrieve()
            .bodyToMono(JsonNode.class).block().get("groupId").asText();
    }

    public String submitJob(JobEntity jobEntity){
        String url = "http://localhost:9888/api/v1/group/" + jobEntity.getJobNode().getCurrentGroupId() + "/job/submit";

        String jsonData = "{" + 
            "\"type\": \"interactive_job_execute\"," + 
            "\"jobClass\":\"" + jobEntity.getClassPath() + "\"," +
            "\"jobConfig\":{}" +
        "}";
        String ilumId = webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .retrieve()
            .bodyToMono(JsonNode.class).block().get("jobInstanceId").asText();

        return ilumId;
    }

    public void stopJob(JobEntity jobEntity){

        String url = "http://localhost:9888/api/v1/job/" + jobEntity.getIlumId() + "/stop";
        webClient.post()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class).block();
    }

}