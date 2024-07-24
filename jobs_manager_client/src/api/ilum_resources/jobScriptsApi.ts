import { AxiosResponse } from "axios";
import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import apiClient from "../ApiClient";
import { AppUserSimple } from "../authorization/users";
import { JobsFileExtension, JobsFileSimple } from "./jobsFilesApi";


export interface JobScriptDetails{
    name : string
}

export interface JobScriptSimple{
    id : string,
    jobScriptDetails : JobScriptDetails, 
    extension : JobsFileExtension,
    classFullName : string,
    jobsFiles : JobsFileSimple[]
    project : {
        id : string,
        projectDetails : ProjectDetails
    }
    jobNode : {
        id : string,
        jobNodeDetails : JobNodeDetails
    }
    author : AppUserSimple
}


export interface JobScriptDTO{
    jobScriptDetails : JobScriptDetails,
    extension : string,
    classFullName : string
}


export async function retreiveJobScript(projectId : string, jobNodeId : string, jobScriptId : string

) : Promise<AxiosResponse<JobScriptSimple>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts/${jobScriptId}`);
}

export async function deleteJobScript(projectId : string, jobNodeId : string, jobScriptId : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts/${jobScriptId}`);
}

export async function updateJobScriptDetails(projectId : string, jobNodeId : string, jobScriptId : string,
    jobScriptDetails : JobScriptDetails
) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts/${jobScriptId}/job_script_details`, jobScriptDetails);
}

export async function createJobScript(projectId : string, jobNodeId : string, jobsScriptDTO : JobScriptDTO

) : Promise<AxiosResponse<string>>{
    return apiClient.post(`/projects/${projectId}/job_nodes/${jobNodeId}/job_scripts`, jobsScriptDTO);
}