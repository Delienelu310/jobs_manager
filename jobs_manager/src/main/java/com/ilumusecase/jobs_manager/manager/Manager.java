package com.ilumusecase.jobs_manager.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.resources.JobEntity;

import reactor.core.publisher.Mono;

@Service
public class Manager {


    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);
    

    private WebClient webClient = WebClient.create();

    public String hello(){

        Mono<String> responseMono = webClient
            .get()
            .uri("http://localhost:9888/api/v1/cluster")
            .retrieve()
            .bodyToMono(String.class);

        return responseMono.block(); 
    
    }

    public String createGroup(){
        String url = "http://localhost:9888/api/v1/group";
        return webClient.post()
            .uri(url)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData("scale", "1")
                    .with("clusterName", "default")
                    .with("name", "my-group"))
            .retrieve()
            .bodyToMono(String.class).block();
    }

    public String submitJob(JobEntity jobEntity){
        String url = "http://localhost:9888/api/v1/group/" + jobEntity.getGroupId() + "/submit";

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
            .bodyToMono(String.class).block();

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