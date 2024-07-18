import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";


export interface Channel{
    id: string, 
    channelDetails: {
        name : string,
        type : string,
        headers : string[]
    }
}

export interface JobNodeVertice{
    x : number,
    y : number,
    id : string,
    jobNode : {
        id : string,
        jobNodeDetails: {
            name : string
        },
        input : Channel[]
        ouput : Channel[]
    }
}

export interface JobNodeVerticeDetails{
    x : number,
    y : number
}

export interface ProjectGraph{
    id : string,
    project : {
        id : string
    }
    vertices : JobNodeVertice[]
}


export async function retrieveProjectGraph(projectId : string) : Promise<ProjectGraph>{
    return apiClient.get(`/projects/${projectId}/graph`).then(response=> response.data);
}

export async function updateProjectGraph(projectId : string) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/graph`);
}

export async function updateJobNodeVertice(projectId : string, jobNodeId : string, jobVerticeDetails : JobNodeVerticeDetails) : Promise<JobNodeVertice>{
    return apiClient.put(`/projects/${projectId}/job_nodes/${jobNodeId}/graph`, jobVerticeDetails);
}