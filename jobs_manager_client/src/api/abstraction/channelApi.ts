import { AxiosResponse } from "axios";
import apiClient from "../ApiClient";
import { ChannelDetails } from "./projectApi";


export async function deleteChannel(projectId : string, channelId : string) : Promise<AxiosResponse<void>>{
    return apiClient.delete(`/projects/${projectId}/channels/${channelId}`);
}


export async function connect(projectId : string, parameters : string[], channelDetails : ChannelDetails) : Promise<AxiosResponse<void>>{
    return apiClient.put(`/projects/${projectId}/job_nodes/connect?${parameters.join("&")}`, channelDetails);
}