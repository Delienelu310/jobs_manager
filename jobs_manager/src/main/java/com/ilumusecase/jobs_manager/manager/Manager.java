package com.ilumusecase.jobs_manager.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.resources.IlumGroup;
import com.ilumusecase.jobs_manager.resources.JobEntity;
import com.ilumusecase.jobs_manager.s3clients.S3ClientFactory;

@Service
public class Manager {


    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @Value("ilum.core.endpoint")
    private String endpoint;
    @Value("ilum.core.version-path")
    private String versionPath;

    @Autowired
    public S3ClientFactory s3ClientFactory;

    @Autowired
    private ResourceLoader resourceLoader;
    

    private WebClient webClient = WebClient.create();

    private byte[] loadJobConnectionLib(){
        try {
            return resourceLoader.getResource("classpath:jobs_connection_lib.jar").getContentAsByteArray();
        } catch (IOException e) {
            throw new RuntimeException("The jobs_connection_lib.jar was not found");
        }
    }

    public String createGroup(IlumGroup ilumGroup){

        
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();

        Map<String, String> extensionMap = new HashMap<>();
        extensionMap.put("jar", "jars");
        extensionMap.put("py", "files");

        for(JobEntity jobEntity : ilumGroup.getJobs()){
            byte[] bytes = s3ClientFactory.getJobS3Client().downloadJob(jobEntity.getJobsFile()).orElseThrow(RuntimeException::new);
            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return jobEntity.getId() + "." + jobEntity.getJobsFile().getExtension();
                }
            };
            bodyMap.add(extensionMap.get(jobEntity.getJobsFile().getExtension()), byteArrayResource);
        }
        //add jobs_connection_lib 
        bodyMap.add("jars", new ByteArrayResource(loadJobConnectionLib()){
            @Override
                public String getFilename() {
                    return "jobs_connection_lib.jar";
                }
        });

        bodyMap.add("scale", "1");
        bodyMap.add("name", ilumGroup.getJobNode().getId());
        bodyMap.add("clusterName", "default");


        String url = endpoint + versionPath + "group";
        return webClient.post()
            .uri(url)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(bodyMap))
            .retrieve()
            .bodyToMono(JsonNode.class).block().get("groupId").asText();
    }

    private String mapToJson(Map<String, String> map){
        StringBuilder result = new StringBuilder();
        result.append("{ ");
        for(String key : map.keySet()){
            result.append("\"" + key);
            result.append(key);
            result.append("\": \"");
            result.append(map.get(key));
            result.append("\", ");
        }
        result.delete(result.length() - 1, result.length());
        result.append("}");
        return result.toString();
    }

    public String submitJob(JobEntity jobEntity, Map<String, String> config){
        String url = endpoint + versionPath + "group/" + jobEntity.getJobsFile().getJobNode().getCurrentGroup().getIlumId() + "/job/submit";

        String jsonData = "{" + 
            "\"type\": \"interactive_job_execute\"," + 
            "\"jobClass\":\"" + jobEntity.getClassPath() + "\"," +
            "\"jobConfig\":" + mapToJson(config) +
        "}";
        String ilumId = webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .retrieve()
            .bodyToMono(JsonNode.class).block().get("jobInstanceId").asText();

        return ilumId;
    }

    public JsonNode getJobInfo(JobEntity jobEntity){
        String url = endpoint + versionPath + "job/" + jobEntity.getIlumId();

        JsonNode jsonNode = webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(JsonNode.class).block();
        return jsonNode;
        
    }

    public void stopJob(JobEntity jobEntity){

        String url = endpoint + versionPath + "job/" + jobEntity.getIlumId() + "/stop";
        webClient.post()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class).block();
    }

}