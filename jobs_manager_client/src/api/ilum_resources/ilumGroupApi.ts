import apiClient from "../ApiClient";

import { AxiosResponse } from "axios";
import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import { JobEntitySimple } from "./jobEntityApi";


export interface IlumGroupOfJobResultData{
    ilumGroupId : string,
    ilumGroupDetails : IlumGroupDetails
}

export interface IlumGroupDetails{
    name : string,
    description : string,
    startTime : number | null
}

export interface IlumGroupConfiguration{
    maxJobDuration : number
}

export interface IlumGroupDTO{
    ilumGroupConfiguration : IlumGroupConfiguration,
    ilumGroupDetails : IlumGroupDetails
}

export interface IlumGroup{

    id : string,
    ilumId : string,

    ilumGroupConfiguration : IlumGroupConfiguration,
    ilumGroupDetails : IlumGroupDetails,

    currentIndex : number,
    currentTestingIndex : number,
    mod : string,
    currentJob : JobEntitySimple
    currentStartTime : Date,

    project : {
        id : string,
        projectDetails : ProjectDetails
    }
    jobNode : {
        id : string,
        jobNodeDetails : JobNodeDetails
    }

}

export function startJobNode(projectId : string, jobNodeId : string, dto : IlumGroupDTO

) : Promise<AxiosResponse<void>>{
    return apiClient.post(`/projects/${projectId}/job_nodes/${jobNodeId}/start`, dto);
}

export function stopJobNode(projectId : string, jobNodeId : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/stop`);
}