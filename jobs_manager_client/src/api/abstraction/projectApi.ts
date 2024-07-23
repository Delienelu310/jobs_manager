import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";


export interface ChannelList{
    id : string,
    channelList: ChannelFullData[];
}


export interface JobNodeDetails{
    name : string
}

export interface JobNodeFullData{
    id : string,
    jobNodeDetails : JobNodeDetails,
    input : {[key:string] :  ChannelList},
    output : {[key:string] :  ChannelList},

    project : any,
    privileges :  {[key:string] : {id: string, list: string[]}},


}

export interface ChannelFullData{
    id : string,
    inputJobs : {id : string, jobNodeDetails : {name : string}}[],
    outputJobs : {id : string, jobNodeDetails : {name : string}}[],
    channelDetails: ChannelDetails


    project: any
}

export enum ChannelTypes{
    MINIO = "minio"
}

export interface ChannelDetails{
    name : string,
    type : ChannelTypes,
    headers : string[]
}

export interface ProjectDetails{
    name : string,
    description : string
}

export interface ProjectFullData{
    id : string,
    projectDetails : ProjectDetails,
    admin : string,
    jobNodes : JobNodeFullData[],
    channels : ChannelFullData[],
    inputChannels : {[key:string] : ChannelFullData}
    outputChannels :  {[key:string] : ChannelFullData},
    privileges : {[key:string] : {id: string, list: string[]}}
}

export async function retrieveProject(projectId : string) : Promise<ProjectFullData>{
    return apiClient.get(`/projects/${projectId}`).then(response => response.data);
}


export async function addProjectPlug(projectId : string, rightOrientation : boolean, label : string, channelDetails : ChannelDetails) : 
    Promise<AxiosResponse<void>>
{
    return apiClient.put(`/projects/${projectId}/${rightOrientation ? "output" : "input"}/add/${label}`, channelDetails);
}

export async function removeProjectPlug(projectId : string, rightOrientation : boolean, label : string) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/${rightOrientation ? "output" : "input"}/remove/${label}`);
}