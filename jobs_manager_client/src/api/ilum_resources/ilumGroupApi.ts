import apiClient from "../ApiClient";

import { AxiosResponse } from "axios";
import { JobNodeDetails, ProjectDetails } from "../abstraction/projectApi";
import { JobEntitySimple } from "./jobEntityApi";


export interface IlumGroupConfiguration{
    maxJobDuration : number
}

export interface IlumGroup{

    id : string,
    ilumId : string,

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

export function startJobNode(projectId : string, jobNodeId : string, configuration : IlumGroupConfiguration

) : Promise<AxiosResponse<void>>{
    return apiClient.post(`/projects/${projectId}/job_nodes/${jobNodeId}/start`, configuration);
}

export function stopJobNode(projectId : string, jobNodeId : string

) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}/stop`);
}