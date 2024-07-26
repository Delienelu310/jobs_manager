package com.ilumusecase.jobs_manager.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.s3clients.S3ClientFactory;

@Service
public class Manager {


    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @Value("${ilum.core.endpoint}")
    private String endpoint;
    @Value("${ilum.core.version-path}")
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

        Set<String> jobsFilesUsed = new HashSet<>();

        for(JobEntity jobEntity : ilumGroup.getJobs()){
            for(JobsFile jobsFile : jobEntity.getJobScript().getJobsFiles()){
                if(jobsFilesUsed.contains(jobsFile.getId())) continue;

                jobsFilesUsed.add(jobsFile.getId());



                byte[] bytes = s3ClientFactory.getJobS3Client().downloadJob(jobsFile).orElseThrow(RuntimeException::new);
                ByteArrayResource byteArrayResource = new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return jobsFile.getId() + "." + jobsFile.getExtension();
                    }
                };
                bodyMap.add(extensionMap.get(jobsFile.getExtension()), byteArrayResource);
            }
            
        }

        for(JobEntity jobEntity : ilumGroup.getTestingJobs()){
            for(JobsFile jobsFile : jobEntity.getJobScript().getJobsFiles()){
                if(jobsFilesUsed.contains(jobsFile.getId())) continue;

                jobsFilesUsed.add(jobsFile.getId());



                byte[] bytes = s3ClientFactory.getJobS3Client().downloadJob(jobsFile).orElseThrow(RuntimeException::new);
                ByteArrayResource byteArrayResource = new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return jobsFile.getId() + "." + jobsFile.getExtension();
                    }
                };
                bodyMap.add(extensionMap.get(jobsFile.getExtension()), byteArrayResource);
            }
            
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

    public void stopGroup(IlumGroup ilumGroup){
        String url = endpoint + versionPath + "group/" + ilumGroup.getIlumId() + "/stop";

        webClient.post()
            .uri(url)
            .retrieve()
            .toBodilessEntity()
            .block();
    }

    public void deleteGroup(IlumGroup ilumGroup){
        String url = endpoint + versionPath + "group/" + ilumGroup.getIlumId();

        webClient.delete()
            .uri(url)
            .retrieve()
            .toBodilessEntity()
            .block();
    }

    private String mapToJson(Map<String, String> map){
        StringBuilder result = new StringBuilder();
        result.append("{");
        for(String key : map.keySet()){
            result.append("\"");
            result.append(key);
            result.append("\":\"");
            result.append(map.get(key));
            result.append("\",");
        }
        result.delete(result.length() - 1, result.length());
        result.append("}");
        return result.toString();
    }


    public String submitJob(IlumGroup ilumGroup, JobEntity jobEntity, Map<String, String> config){
        String url = endpoint + versionPath + "group/" + ilumGroup.getIlumId() + "/job/submit";

        String jsonData = "{" + 
            "\"type\": \"interactive_job_execute\"," + 
            "\"jobClass\":\"" + jobEntity.getJobScript().getClassFullName() + "\"," +
            "\"jobConfig\":" + mapToJson(config) +
        "}";
        logger.info(jsonData);


        String ilumId = webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonData)
            .retrieve()
            .bodyToMono(JsonNode.class).block().get("jobInstanceId").asText();

        return ilumId;
    }

    public JsonNode getJobInfo(IlumGroup ilumGroup, JobEntity jobEntity){
        String url = endpoint + versionPath + "group/" + ilumGroup.getIlumId() + "/job/result?search=" + jobEntity.getIlumId();

        logger.info(url);

        JsonNode jsonNode = webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(JsonNode.class).block().path("content").get(0);
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