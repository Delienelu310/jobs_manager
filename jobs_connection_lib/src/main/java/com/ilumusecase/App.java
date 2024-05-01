package com.ilumusecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilumusecase.data_supplier.DataSupplierClient;
import com.ilumusecase.resources.ProjectDTO;

public class App 
{
    public static void main( String[] args )
    {
        DataSupplierClient dataSupplierClient = new DataSupplierClient();

        try{
            ProjectDTO projectDTO = dataSupplierClient.retrieveProjectById("662bd89b8a700538155cd869");

            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(projectDTO));
            System.out.println(projectDTO.id);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
