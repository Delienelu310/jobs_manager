package com.ilumusecase.data_supplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilumusecase.resources.ChannelDTO;
import com.ilumusecase.resources.JobNodeDTO;
import com.ilumusecase.resources.ProjectDTO;

public class DataSupplierClient {

    private final String prefix = "http://localhost:8080";

    public String retrieveJsonString(String path){
        try{
            URL url = new URL(prefix + path);
            System.out.println(url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if(connection.getResponseCode() < 200 || connection.getResponseCode() >= 300){
                System.out.println(connection.getErrorStream().toString());

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                throw new RuntimeException(line);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            return response.toString();

            
        } catch (IOException e) {
            throw new InvalidPathException();
        }
    }


    public Object jsonToObj(String jsonString, Class<?> clazz ){
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object person = mapper.readValue(jsonString, clazz);
            return person;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ProjectDTO retrieveProjectById(String id) throws Exception{

        String resposne = retrieveJsonString("/projects/" + id);
        ObjectMapper objectMapper = new ObjectMapper();
        ProjectDTO projectDTO = objectMapper.readValue(resposne, ProjectDTO.class);
        return projectDTO;
    }

    public JobNodeDTO retrieveJobNode(String projectId, String nodeId) throws Exception{
        String resposne = retrieveJsonString("/projects/" + projectId + "/job_nodes/" + nodeId);
        ObjectMapper objectMapper = new ObjectMapper();
        JobNodeDTO jobNodeDTO = objectMapper.readValue(resposne, JobNodeDTO.class);
        return jobNodeDTO;
    }
    
    public ChannelDTO retrieveChannel(String projectId, String channelId) throws Exception{
        String resposne = retrieveJsonString("/projects/" + projectId + "/channels/" + channelId);
        ObjectMapper objectMapper = new ObjectMapper();
        ChannelDTO channelDTO = objectMapper.readValue(resposne, ChannelDTO.class);
        return channelDTO;
    }


}
