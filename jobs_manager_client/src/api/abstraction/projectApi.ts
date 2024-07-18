import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";

export interface JobNodeFullData{
    id : string,
    jobNodeDetails : {
        name : string
    },
    input : {[key:string] :  {id : string, channelList : {id : string, channelDetails : {name : string, type : string, headers : string[]}}[]}},
    output : {[key:string] :  {id : string, channelList : {id : string, channelDetails : {name : string, type : string, headers : string[]}}[]}}

    project : any,
    privileges :  {[key:string] : {id: string, list: string[]}}


}

export interface ChannelFullData{
    id : string,
    inputJobs : {id : string, jobNodeDetails : {name : string}}[],
    outputJobs : {id : string, jobNodeDetails : {name : string}}[],
    channelDetails: {
        name : string,
        type : string,
        headers : string[]
    }


    project: any
}

export interface ProjectFullData{
    id : string,
    projectDetails : {
        name : string,
        description : string
    },
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


export async function removeProjectPlug(projectId : string, rightOrientation : boolean, label : string) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/${rightOrientation ? "output" : "input"}/remove/${label}`);
}