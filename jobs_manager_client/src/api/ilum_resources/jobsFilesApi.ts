import { AxiosResponse } from "axios"
import { AppUserSimple } from "../authorization/users"
import apiClient from "../ApiClient"


export interface JobsFileDetails{
    name : string,
    description : string
}

export enum JobsFileExtension{
    JAR = "jar",
    PY = "py"
}

export enum JobsFileState{
    UNKNOWN = "unknown",
    OK = "ok",
    NOFILE = "nofile"
}

export interface JobsFileSimple{
    id : string,
    extension : JobsFileExtension,
    jobDetails : JobsFileDetails,

    allClasses : string[],
    project : any,
    jobNode : any,
    publisher : AppUserSimple

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