import { Axios, AxiosResponse } from "axios";
import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import apiClient from "../ApiClient";
import { JobScriptSimple } from "./jobScriptsApi";
import { IlumGroupDetails } from "./ilumGroupApi";


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
    ilumGroupDetails : IlumGroupDetails,


    targetConfiguration : string
    target : JobScriptSimple
    tester : JobScriptSimple | null

    startTime : number,
    endTime : number,


    jobNode : {
        id : string,
        jobNodeDetails : JobNodeDetails
    }

    project : {
        id : string,
        projectDetails : ProjectDetails
    }
}


export function retrieveJobResultById(projectId : string, jobNodeId : string, jobResultId : string

) : Promise<AxiosResponse<JobResultSimple>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/job_results/${jobResultId}`);
}

export function retrieveJobResults(projectId : string, jobNodeId : string, requestParams : [string, string][]

) : Promise<AxiosResponse<JobResultSimple[]>>{

    const url = `/projects/${projectId}/job_nodes/${jobNodeId}/job_results?${requestParams.map(p => p.join("=")).join("&")}`;

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

export function clearJobResults(projectId : string, jobNodeId : string, ilumGroupId : string | null, testerId : string | null, 
    includeSuccessfull : boolean, includeJobErrors : boolean, includeTesterErrors : boolean 
) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/job_results?` + 
        `include_successfull=${includeSuccessfull ? "true" : "false"}&include_job_errors=${includeJobErrors ? "true" : false}` + 
        `&include_tester_errors=${includeTesterErrors ? "true" : "false"}` + 
        `${ilumGroupId ? `&ilum_group_id=${ilumGroupId}` : ""}` + 
        `${testerId ? `&tester_id=${testerId}` : ""}`
    );
}