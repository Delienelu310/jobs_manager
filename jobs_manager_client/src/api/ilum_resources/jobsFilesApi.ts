import { AxiosResponse } from "axios"
import { AppUserSimple } from "../authorization/users"
import apiClient from "../ApiClient"
import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi"


export interface JobsFileDetails{
    name : string,
    description : string
}

export enum JobsFileExtension{
    JAR = "jar",
    PY = "py"
}

export enum JobsFileState{
    UNKNOWN = "UNKNOWN",
    OK = "OK",
    NOFILE = "NOFILE"
}

export interface JobsFileSimple{
    id : string,
    extension : JobsFileExtension,
    jobDetails : JobsFileDetails,

    allClasses : string[],
    project : {
        id : string,
        projectDetails : ProjectDetails
    },
    jobNode : {
        id : string,
        jobNodeDetails: JobNodeDetails
    }
    publisher : AppUserSimple

}


export async function retrieveJobsFile(
    projectId : string, jobNodeId : string, jobsFileId : string
) : Promise<AxiosResponse<JobsFileSimple>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/${jobsFileId}`);
}

export async function uploadJobsFile(projectId : string, jobNodeId : string, 
    extension : string, jobsFileDetails : JobsFileDetails, file : File
) : Promise<AxiosResponse<string>> {

    const formData = new FormData();
    formData.append("files", file);
    formData.append("extension", extension);
    formData.append("jobs_details", new Blob([JSON.stringify(jobsFileDetails)], { type: 'application/json' }), 'jobs_details.json');

    return apiClient.post(`/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files`, formData, {headers:{
        "Content-Type": "multipart/form-data"
    }});

}


export async function checkJobsFileState(projectId : string, jobNodeId : string, jobsFileId : string

) : Promise<AxiosResponse<JobsFileState>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/${jobsFileId}/state`);
}


export async function deleteJobsFile(projectId : string, jobNodeId : string, jobsFileId : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/${jobsFileId}`);
}


export async function updateJobsFileDetails(projectId : string, jobNodeId : string, jobsFileId : string,
    jobsFileDetails : JobsFileDetails
) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/${jobsFileId}/job_details`, jobsFileDetails);
}

export async function updateJobsFileFile(projectId : string, jobNodeId : string, jobsFileId : string,
    extension : string, file : File
) : Promise<AxiosResponse<void>> {
    const formData = new FormData();
    formData.append("files", file);
    formData.append("extension", extension);

    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/jobs_files/${jobsFileId}/file`, formData, {headers:{
        "Content-Type": "multipart/form-data"
    }});
}