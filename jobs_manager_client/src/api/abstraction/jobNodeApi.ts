import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";
import { JobNodeDetails } from "./projectApi";

import { ChannelList } from "./projectApi";
import { IlumGroup } from "../ilum_resources/ilumGroupApi";
import { QueueTypes } from "../ilum_resources/queueOperationsApi";

export interface JobNodeWithIlumGroup{

    id : string,
    jobNodeDetails : JobNodeDetails,
    input : {[key:string] :  ChannelList},
    output : {[key:string] :  ChannelList},

    project : any,
    privileges :  {[key:string] : {id: string, list: string[]}},

    ilumGroup : IlumGroup | null
}

export async function retrieveJobNodeWithIlumGroup(projectId : string, jobNodeId : string

) : Promise<AxiosResponse<JobNodeWithIlumGroup>>{
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/ilum`);
}



export async function deleteJobNode(projectId : string, jobNodeId : string) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/job_nodes/${jobNodeId}`);
}  

export async  function removeJobNodePlug(projectId : string, jobNodeId : string, rightOrientation : boolean, label : string) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/remove/${rightOrientation ? "output" : "input"}/${label}`);
}


export async function createJobNode(projectId : string, jobNodeDetails : JobNodeDetails) : Promise<AxiosResponse<string>>{
    return apiClient.post(`/projects/${projectId}/job_nodes`, jobNodeDetails);
}


export async function addJobNodePlug(projectId : string, jobNodeId : string, rightOrientation : boolean, label : string) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/add/${rightOrientation ? "output" : "input"}/${label}`);
}


export async function retrieveQueueSize(projectId : string, jobNodeId : string, queueType : QueueTypes){
    return apiClient.get(`/projects/${projectId}/job_nodes/${jobNodeId}/queue/${queueType}/count`);
}
