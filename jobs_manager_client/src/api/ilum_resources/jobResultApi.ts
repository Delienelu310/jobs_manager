import { Axios, AxiosResponse } from "axios";
import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import apiClient from "../ApiClient";
import { JobScriptSimple } from "./jobScriptsApi";


export interface JobResultDetails{
    errorMessage : string | null,
    errorStackTrace : string | null,
    resultStr : string | null,
    metrics : {
        [key : string] : string
    }
}

export interface JobResultSimple{
    
    id : string,
    ilumId : string,
    ilumGroupId : string,

    jobResultDetails : JobResultDetails,


    targetConfiguration : string
    target : JobScriptSimple
    tester : JobScriptSimple | null

    startTime : Number,
    endTime : Number,


    jobNode : {
        id : string,
        jobNodeDetails : JobNodeDetails
    }

    project : {
        id : string,
        projectDetails : ProjectDetails
    }
}

export function retrieveJobResults(projectId : string, jobNodeId : string, requestParams : [string, string][]

) : Promise<AxiosResponse<JobResultSimple[]>>{

    const url = `/projects/${projectId}/job_nodes/${jobNodeId}/job_results?${requestParams.map(p => p.join("=")).join("&")}`;
    console.log(url);
    return apiClient.get(url);
}

export function retrieveJobResultsCount(projectId : string, jobNodeId : string, requestParams : [string, string][]

) : Promise<AxiosResponse<number>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/job_results/count?${requestParams.map(p => p.join("=")).join("&")}`);
}

export function deleteJobResult(projectId : string, jobNodeId : string, jobResultId : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/job_results/${jobResultId}`);
}