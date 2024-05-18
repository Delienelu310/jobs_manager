package com.ilumusecase.jobs_manager.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.ilumusecase.jobs_manager.JobsManagerApplication;

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



}